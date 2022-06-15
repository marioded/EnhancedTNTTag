package tech.zmario.enhancedtnttag.listeners;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import tech.zmario.enhancedtnttag.EnhancedTNTTag;
import tech.zmario.enhancedtnttag.api.objects.IArena;
import tech.zmario.enhancedtnttag.enums.MessagesConfiguration;
import tech.zmario.enhancedtnttag.enums.SettingsConfiguration;
import tech.zmario.enhancedtnttag.utils.Utils;

import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
public class PlayerChatListener implements Listener {

    private final EnhancedTNTTag plugin;

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        if (!SettingsConfiguration.CHAT_FORMAT_ENABLED.getBoolean()) return;

        Player player = e.getPlayer();
        Set<Player> recipients = e.getRecipients();

        Optional<IArena> arena = plugin.getArenaManager().getArena(player);

        String format;

        recipients.clear();

        if (!arena.isPresent()) {
            format = MessagesConfiguration.CHAT_FORMAT_LOBBY.getString(player);

            for (Player online : Bukkit.getOnlinePlayers()) {
                if (plugin.getArenaManager().getArena(online).isPresent()) continue;
                recipients.add(online);
            }
        } else if (arena.get().getSpectators().contains(player.getUniqueId())) {
            format = MessagesConfiguration.CHAT_FORMAT_SPECTATOR.getString(player);

            for (Player online : Bukkit.getOnlinePlayers()) {
                Optional<IArena> onlineArena = plugin.getArenaManager().getArena(online);

                if (!onlineArena.isPresent() || !onlineArena.equals(arena) || !onlineArena.get().getSpectators().contains(online.getUniqueId()))
                    continue;
                recipients.add(online);
            }
        } else {
            format = arena.get().getTaggers().contains(player.getUniqueId()) ?
                    MessagesConfiguration.CHAT_FORMAT_TAGGED.getString(player) :
                    MessagesConfiguration.CHAT_FORMAT_PLAYER.getString(player);

            for (Player online : Bukkit.getOnlinePlayers()) {
                Optional<IArena> onlineArena = plugin.getArenaManager().getArena(online);

                if (!onlineArena.isPresent() || !onlineArena.equals(arena)) continue;
                recipients.add(online);
            }
        }

        format = Utils.colorize(format.replace("%level%", "0")
                .replace("%player%", player.getName())
                .replace("%message%", e.getMessage()).replace("%", "%%"));

        e.setFormat(format);
    }
}
