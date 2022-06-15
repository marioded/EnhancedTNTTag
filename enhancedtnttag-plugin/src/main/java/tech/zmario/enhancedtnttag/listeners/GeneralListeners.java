package tech.zmario.enhancedtnttag.listeners;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.WeatherChangeEvent;
import tech.zmario.enhancedtnttag.EnhancedTNTTag;
import tech.zmario.enhancedtnttag.api.objects.IArena;

import java.util.Optional;

@RequiredArgsConstructor
public class GeneralListeners implements Listener {

    private final EnhancedTNTTag plugin;

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        if (plugin.getLocalStorage().getBuildPlayers().contains(e.getPlayer().getUniqueId())) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        if (plugin.getLocalStorage().getBuildPlayers().contains(e.getPlayer().getUniqueId())) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onArmorStandManipulate(PlayerArmorStandManipulateEvent e) {
        if (plugin.getLocalStorage().getBuildPlayers().contains(e.getPlayer().getUniqueId())) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (plugin.getLocalStorage().getBuildPlayers().contains(e.getPlayer().getUniqueId())) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onBedEnter(PlayerBedEnterEvent e) {
        if (plugin.getLocalStorage().getBuildPlayers().contains(e.getPlayer().getUniqueId())) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onAchievementAwarded(PlayerAchievementAwardedEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onKick(PlayerKickEvent e) {
        Player player = e.getPlayer();
        Optional<IArena> arena = plugin.getArenaManager().getArena(player);

        arena.ifPresent(playerArena -> arena.get().handleQuit(player));
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent e) {
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            e.setCancelled(true);
            if (p.getFoodLevel() < 19.0D)
                p.setFoodLevel(20);
        }
    }
}
