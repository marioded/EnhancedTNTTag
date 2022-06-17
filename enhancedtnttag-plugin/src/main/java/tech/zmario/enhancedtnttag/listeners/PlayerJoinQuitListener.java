package tech.zmario.enhancedtnttag.listeners;

import fr.minuskube.netherboard.Netherboard;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import tech.zmario.enhancedtnttag.EnhancedTNTTag;
import tech.zmario.enhancedtnttag.api.objects.GamePlayer;
import tech.zmario.enhancedtnttag.api.objects.IArena;
import tech.zmario.enhancedtnttag.enums.MessagesConfiguration;
import tech.zmario.enhancedtnttag.enums.SettingsConfiguration;
import tech.zmario.enhancedtnttag.objects.Placeholder;
import tech.zmario.enhancedtnttag.utils.Utils;

import java.util.Optional;

@RequiredArgsConstructor
public class PlayerJoinQuitListener implements Listener {

    private final EnhancedTNTTag plugin;

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {

            if (!plugin.getDatabaseManager().isPresent(player)) {
                plugin.getDatabaseManager().createPlayer(player);
            }

            GamePlayer gamePlayer = new GamePlayer(player.getUniqueId());

            gamePlayer.setWins(plugin.getDatabaseManager().getWins(player));

            plugin.getLocalStorage().getGamePlayers().put(player.getUniqueId(), gamePlayer);
        });

        Netherboard.instance().createBoard(player, MessagesConfiguration.SCOREBOARD_TITLE.getString(player));
        if (Utils.getMainLobby() == null && player.hasPermission(SettingsConfiguration.ADMIN_PERMISSION.getString())) {
            MessagesConfiguration.MAIN_LOBBY_NOT_SET.send(player);
        } else {
            player.teleport(Utils.getMainLobby());
        }

        // Update the player list
        if (SettingsConfiguration.TAB_LIST_FORMAT_LOBBY.getBoolean()) {
            player.setDisplayName(MessagesConfiguration.TAB_LIST_LOBBY.getString(player,
                    new Placeholder("%player%", player.getName())));
            player.setPlayerListName(MessagesConfiguration.TAB_LIST_LOBBY.getString(player,
                    new Placeholder("%player%", player.getName())));
        }

        Utils.hidePlayers(player, plugin.getArenaManager());
        Utils.sendItems(player, "main-lobby");


        e.setJoinMessage(null);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Optional<IArena> arena = plugin.getArenaManager().getArena(player);

        arena.ifPresent(playerArena -> arena.get().handleQuit(player));
        event.setQuitMessage(null);
    }
}
