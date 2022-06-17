package tech.zmario.enhancedtnttag.tasks;

import fr.minuskube.netherboard.Netherboard;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import tech.zmario.enhancedtnttag.EnhancedTNTTag;
import tech.zmario.enhancedtnttag.api.objects.IArena;
import tech.zmario.enhancedtnttag.enums.MessagesConfiguration;
import tech.zmario.enhancedtnttag.objects.Placeholder;
import tech.zmario.enhancedtnttag.utils.Utils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class PlayerUpdateTask extends BukkitRunnable {

    private final EnhancedTNTTag plugin;

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            Optional<IArena> arenaOptional = plugin.getArenaManager().getArena(player);

            if (!arenaOptional.isPresent()) {
                Netherboard.instance().getBoard(player)
                        .setAll(MessagesConfiguration.SCOREBOARD_LOBBY.getStringList(player,
                                        new Placeholder("%wins%", String.valueOf(0)))
                                .toArray(new String[0]));
                return;
            }

            IArena arena = arenaOptional.get();

            switch (arena.getGameState()) {
                case WAITING:
                    Netherboard.instance().getBoard(player)
                            .setAll(MessagesConfiguration.SCOREBOARD_WAITING.getStringList(player,
                                    new Placeholder("%map%", arena.getArenaName()),
                                    new Placeholder("%players%", arena.getPlayers().size() + ""),
                                    new Placeholder("%max-players%", arena.getMaxPlayers() + ""),
                                    new Placeholder("%date%", Utils.getDate())).toArray(new String[0]));
                    return;

                case STARTING:
                    Netherboard.instance().getBoard(player)
                            .setAll(MessagesConfiguration.SCOREBOARD_STARTING.getStringList(player,
                                    new Placeholder("%map%", arena.getArenaName()),
                                    new Placeholder("%players%", arena.getPlayers().size() + ""),
                                    new Placeholder("%max-players%", arena.getMaxPlayers() + ""),
                                    new Placeholder("%countdown%", arena.getCountdown() + ""),
                                    new Placeholder("%date%", Utils.getDate())).toArray(new String[0]));
                    return;

                case RESTARTING:
                    final List<UUID> topPlayers = plugin.getLeaderBoardManager().getPlayers(arena);

                    final String firstPlace = topPlayers.size() >= 1 ? Bukkit.getOfflinePlayer(topPlayers.get(0)).getName() :
                            MessagesConfiguration.PLACEHOLDERS_NONE.getString(player);
                    final String secondPlace = topPlayers.size() >= 2 ? Bukkit.getOfflinePlayer(topPlayers.get(1)).getName() :
                            MessagesConfiguration.PLACEHOLDERS_NONE.getString(player);
                    final String thirdPlace = topPlayers.size() == 3 ? Bukkit.getOfflinePlayer(topPlayers.get(2)).getName() :
                            MessagesConfiguration.PLACEHOLDERS_NONE.getString(player);

                    Netherboard.instance().getBoard(player)
                            .setAll(MessagesConfiguration.SCOREBOARD_RESTARTING.getStringList(player,
                                    new Placeholder("%map%", arena.getArenaName()),
                                    new Placeholder("%max-players%", arena.getMaxPlayers() + ""),
                                    new Placeholder("%date%", Utils.getDate()),
                                    new Placeholder("%player-1%", firstPlace),
                                    new Placeholder("%player-2%", secondPlace),
                                    new Placeholder("%player-3%", thirdPlace)).toArray(new String[0]));

                default:
                    break;
            }

            if (arena.getPlayers().contains(player.getUniqueId())) {

                if (arena.getTaggers().contains(player.getUniqueId())) {
                    Netherboard.instance().getBoard(player)
                            .setAll(MessagesConfiguration.SCOREBOARD_PLAYING_TAGGED.getStringList(player,
                                    new Placeholder("%round%", arena.getRound() + ""),
                                    new Placeholder("%map%", arena.getArenaName()),
                                    new Placeholder("%players%", arena.getPlayers().size() + ""),
                                    new Placeholder("%max-players%", arena.getMaxPlayers() + ""),
                                    new Placeholder("%explosion-time%", arena.getExplosionTime() + ""),
                                    new Placeholder("%date%", Utils.getDate())).toArray(new String[0]));
                    continue;
                }

                Netherboard.instance().getBoard(player)
                        .setAll(MessagesConfiguration.SCOREBOARD_PLAYING_UNTAGGED.getStringList(player,
                                new Placeholder("%round%", arena.getRound() + ""),
                                new Placeholder("%map%", arena.getArenaName()),
                                new Placeholder("%players%", arena.getPlayers().size() + ""),
                                new Placeholder("%max-players%", arena.getMaxPlayers() + ""),
                                new Placeholder("%explosion-time%", arena.getExplosionTime() + ""),
                                new Placeholder("%date%", Utils.getDate())).toArray(new String[0]));

            } else {
                Netherboard.instance().getBoard(player)
                        .setAll(MessagesConfiguration.SCOREBOARD_PLAYING_SPECTATOR.getStringList(player,
                                new Placeholder("%round%", arena.getRound() + ""),
                                new Placeholder("%map%", arena.getArenaName()),
                                new Placeholder("%players%", arena.getPlayers().size() + ""),
                                new Placeholder("%max-players%", arena.getMaxPlayers() + ""),
                                new Placeholder("%explosion-time%", arena.getExplosionTime() + ""),
                                new Placeholder("%date%", Utils.getDate())).toArray(new String[0]));
            }
        }
    }
}
