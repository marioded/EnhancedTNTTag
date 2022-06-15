package tech.zmario.enhancedtnttag.tasks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import tech.zmario.enhancedtnttag.EnhancedTNTTag;
import tech.zmario.enhancedtnttag.api.objects.IArena;
import tech.zmario.enhancedtnttag.api.objects.Placeholder;
import tech.zmario.enhancedtnttag.enums.MessagesConfiguration;
import tech.zmario.enhancedtnttag.enums.SettingsConfiguration;
import tech.zmario.enhancedtnttag.utils.Utils;

import java.util.Iterator;
import java.util.UUID;

public class GamePlayingTask extends BukkitRunnable {

    private final EnhancedTNTTag plugin;
    private final IArena arena;

    private final int explosionDelayBase = SettingsConfiguration.EXPLOSION_DELAY_BASE.getInt();

    private long lastTime = -1;

    public GamePlayingTask(EnhancedTNTTag plugin, IArena arena) {
        this.plugin = plugin;
        this.arena = arena;
    }

    @Override
    public void run() {
        int round = arena.getRound();

        if (arena.getPlayers().size() == 1 || arena.getPlayers().size() == 0) {

            if (arena.getPlayers().size() == 1)
                plugin.getLeaderBoardManager().addPlayer(arena, Bukkit.getPlayer(arena.getPlayers().get(0)));

            plugin.getGameManager().endGame(arena);

            cancel();
            return;
        }

        if (lastTime != -1) {
            if ((System.currentTimeMillis() - lastTime) > (SettingsConfiguration.COUNTDOWNS_ROUND_CHANGE.getInt() * 1000L)) {
                arena.increaseRound(1);

                // Select the taggers for the next round
                arena.selectTaggers();

                String taggedPlayers = Utils.createTaggedPlayersMessage(arena);

                // Reset the explosion time
                arena.setExplosionTime(explosionDelayBase - SettingsConfiguration.EXPLOSION_DELAY_DECREMENT.getInt());

                // Check if the new explosion time is lower than 0, if true, set it to 15
                if (arena.getExplosionTime() <= 0) {
                    arena.setExplosionTime(15);
                }

                int deathMatchPlayers = SettingsConfiguration.DEATH_MATCH_PLAYERS_SIZE.getInt();

                // Loop through all players in the arena and send the message that the round has started
                for (UUID playerUuid : arena.getPlayers()) {
                    Player player = Bukkit.getPlayer(playerUuid);

                    // Check if the size of players is lower or equal to the death match players size
                    if (arena.getPlayers().size() <= deathMatchPlayers) {
                        if (arena.getTaggers().contains(playerUuid)) {
                            // Add the tag to the player
                            arena.addTagger(player, null);

                            // Send the message to the player that the death match has started
                            MessagesConfiguration.DEATH_MATCH_STARTED_TAGGED.sendList(player,
                                    new Placeholder("%round%", String.valueOf(round)),
                                    new Placeholder("%players%", taggedPlayers));

                            // Send the title to the tagged player that the round has started
                            Utils.sendTitle(MessagesConfiguration.TITLES_DEATH_MATCH_STARTED_TAGGED.getString(player), player);
                            Utils.playSound(MessagesConfiguration.SOUNDS_DEATH_MATCH_STARTED_TAGGED.getString(player), player);
                        } else {
                            // Send the message to the player that the death match has started
                            MessagesConfiguration.DEATH_MATCH_STARTED.sendList(player,
                                    new Placeholder("%round%", String.valueOf(round)),
                                    new Placeholder("%players%", taggedPlayers));

                            // Send the title to the tagged player that the round has started
                            Utils.sendTitle(MessagesConfiguration.TITLES_DEATH_MATCH_STARTED.getString(player), player);
                            Utils.playSound(MessagesConfiguration.SOUNDS_DEATH_MATCH_STARTED.getString(player), player);
                        }

                        // Teleport the player to the arena spawn location because it's a death match round
                        player.teleport(arena.getSpawnLocation());
                    } else {
                        if (arena.getTaggers().contains(playerUuid)) {
                            // Send the message to the player that the round has started
                            MessagesConfiguration.ROUND_STARTED.sendList(player,
                                    new Placeholder("%round%", String.valueOf(round)),
                                    new Placeholder("%players%", taggedPlayers));

                            // Send the title and sound to the tagged player that the round has started
                            Utils.sendTitle(MessagesConfiguration.TITLES_NEW_ROUND.getString(player), player);
                            Utils.playSound(MessagesConfiguration.SOUNDS_NEW_ROUND.getString(player), player);

                        } else {
                            // Send the message to the tagged player that the round has started
                            MessagesConfiguration.ROUND_STARTED_TAGGED.sendList(player,
                                    new Placeholder("%round%", String.valueOf(round)),
                                    new Placeholder("%players%", taggedPlayers));

                            // Send the title and sound to the tagged player that the round has started
                            Utils.sendTitle(MessagesConfiguration.TITLES_NEW_ROUND_TAGGED.getString(player), player);
                            Utils.playSound(MessagesConfiguration.SOUNDS_NEW_ROUND_TAGGED.getString(player), player);
                        }
                    }
                }

                for (UUID spectatorUuid : arena.getSpectators()) {
                    Player spectator = Bukkit.getPlayer(spectatorUuid);

                    // Check if the size of players is lower or equal to the death match players size
                    if (arena.getPlayers().size() <= deathMatchPlayers) {
                        // Send the message to the spectator that the death match has started
                       MessagesConfiguration.DEATH_MATCH_STARTED_SPECTATOR.sendList(spectator,
                               new Placeholder("%round%", String.valueOf(round)),
                               new Placeholder("%players%", taggedPlayers));

                        // Send the title to the spectator that the death match has started
                        Utils.sendTitle(MessagesConfiguration.TITLES_DEATH_MATCH_STARTED_SPECTATOR.getString(spectator), spectator);
                        Utils.playSound(MessagesConfiguration.SOUNDS_DEATH_MATCH_STARTED_SPECTATOR.getString(spectator), spectator);
                    } else {
                        // Send the message to the spectator that the round has started
                        MessagesConfiguration.ROUND_STARTED_SPECTATOR.sendList(spectator,
                                new Placeholder("%round%", String.valueOf(round)),
                                new Placeholder("%players%", taggedPlayers));

                        // Send the title to the spectator that the round has started
                        Utils.sendTitle(MessagesConfiguration.TITLES_NEW_ROUND_SPECTATOR.getString(spectator), spectator);
                        Utils.playSound(MessagesConfiguration.SOUNDS_NEW_ROUND_SPECTATOR.getString(spectator), spectator);
                    }
                }

                // Reset the last time variable
                lastTime = -1;
            } else {
                return;
            }
        }

        if (arena.getExplosionTime() == 0) {
            for (Iterator<UUID> iterator = arena.getTaggers().iterator(); iterator.hasNext(); ) {
                Player tagger = Bukkit.getPlayer(iterator.next());

                // Create the explosion effect
                tagger.getWorld().createExplosion(tagger.getLocation(), SettingsConfiguration.EXPLOSION_RADIUS.getInt());

                // Check if the alive players size is lower or equal to 3, if true, add the dead player to the top 3 leaderboard
                if (arena.getPlayers().size() <= 3) {
                    plugin.getLeaderBoardManager().addPlayer(arena, tagger);
                }

                // Add the player as a spectator
                arena.addSpectator(tagger);
                iterator.remove();
            }

            if (arena.getPlayers().size() == 1) {
                plugin.getLeaderBoardManager().addPlayer(arena, Bukkit.getPlayer(arena.getPlayers().get(0)));
                plugin.getGameManager().endGame(arena);
                cancel();
                return;
            }

            // Reset current time
            lastTime = System.currentTimeMillis();
            return;
        }

        arena.setExplosionTime(arena.getExplosionTime() - 1);
    }
}
