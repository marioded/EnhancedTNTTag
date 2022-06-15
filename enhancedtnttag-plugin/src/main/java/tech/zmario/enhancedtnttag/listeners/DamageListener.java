package tech.zmario.enhancedtnttag.listeners;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.FireworkEffect;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import tech.zmario.enhancedtnttag.EnhancedTNTTag;
import tech.zmario.enhancedtnttag.api.enums.GameState;
import tech.zmario.enhancedtnttag.api.objects.IArena;
import tech.zmario.enhancedtnttag.enums.SettingsConfiguration;
import tech.zmario.enhancedtnttag.utils.Utils;

import java.util.Optional;

@RequiredArgsConstructor
public class DamageListener implements Listener {

    private final EnhancedTNTTag plugin;

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        Player player = (Player) e.getEntity();

        if (!plugin.getArenaManager().getArena(player).isPresent()) return;
        IArena arena = plugin.getArenaManager().getArena(player).get();

        e.setDamage(0D);

        // Cancel damage if the cause is fall
        if (e.getCause() == EntityDamageEvent.DamageCause.FALL) {
            e.setCancelled(true);
        }

        // Player death because of the explosion of a tagged player
        if (e.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION
                && arena.getPlayers().contains(player.getUniqueId()) && !arena.getTaggers().contains(player.getUniqueId())) {

            if (arena.getPlayers().size() <= 3) {
                plugin.getLeaderBoardManager().addPlayer(arena, player);
            }

            arena.addSpectator(player);

            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        e.setCancelled(true);

        if (!(e.getDamager() instanceof Player) || !(e.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) e.getEntity();
        Player damager = (Player) e.getDamager();

        Optional<IArena> entityArenaOptional = plugin.getArenaManager().getArena(player);
        Optional<IArena> damagerArenaOptional = plugin.getArenaManager().getArena(damager);


        if (!entityArenaOptional.isPresent() || !damagerArenaOptional.isPresent()) {
            return;
        }

        IArena arena = entityArenaOptional.get();
        IArena damagerArena = damagerArenaOptional.get();

        // Players aren't in the same arena

        if (!arena.equals(damagerArena) || arena.getGameState() == GameState.WAITING || arena.getGameState() == GameState.STARTING || arena.getSpectators().contains(damager.getUniqueId())) {
            return;
        }

        e.setCancelled(false);

        // Player is neither a tagger nor a player
        if (!arena.getTaggers().contains(damager.getUniqueId()) || !arena.getPlayers().contains(player.getUniqueId())) {
            return;
        }

        if (SettingsConfiguration.TAG_FIREWORK_ENABLED.getBoolean())
            Utils.spawnFireworks(player.getLocation(), SettingsConfiguration.TAG_FIREWORK_POWER.getInt(),
                    FireworkEffect.Type.valueOf(SettingsConfiguration.TAG_FIREWORK_TYPE.getString()));

        arena.removeTagger(damager);
        arena.addTagger(player, damager);

        arena.getTaggers().add(player.getUniqueId());
    }
}
