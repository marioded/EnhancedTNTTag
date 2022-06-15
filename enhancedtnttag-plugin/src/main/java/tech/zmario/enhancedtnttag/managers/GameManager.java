package tech.zmario.enhancedtnttag.managers;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import tech.zmario.enhancedtnttag.EnhancedTNTTag;
import tech.zmario.enhancedtnttag.api.enums.GameState;
import tech.zmario.enhancedtnttag.api.manager.IGameManager;
import tech.zmario.enhancedtnttag.api.objects.IArena;
import tech.zmario.enhancedtnttag.api.objects.Placeholder;
import tech.zmario.enhancedtnttag.enums.MessagesConfiguration;
import tech.zmario.enhancedtnttag.enums.SettingsConfiguration;
import tech.zmario.enhancedtnttag.tasks.GamePlayingTask;
import tech.zmario.enhancedtnttag.utils.Utils;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class GameManager implements IGameManager {

    private final EnhancedTNTTag plugin;

    @Override
    public void startGame(@NotNull IArena arena) {
        if (arena.getGameState() != GameState.STARTING) {
            throw new IllegalStateException("Arena is not in waiting state! Please report this to the developer.");
        }

        plugin.getLeaderBoardManager().addArena(arena);

        // Select taggers
        arena.selectTaggers();

        // Get the taggers as a formatted string
        String taggedPlayers = Utils.createTaggedPlayersMessage(arena);

        // Loop through all players in the arena
        for (UUID uuid : arena.getPlayers()) {
            Player player = Bukkit.getPlayer(uuid);

            if (player.isDead()) {
                player.spigot().respawn();
            }

            // Update the player list
            if (SettingsConfiguration.TAB_LIST_FORMAT_UNTAGGED.getBoolean()) {
                player.setDisplayName(MessagesConfiguration.TAB_LIST_UNTAGGED.getString(player,
                        new Placeholder("%player%", player.getName())));
                player.setPlayerListName(MessagesConfiguration.TAB_LIST_UNTAGGED.getString(player,
                        new Placeholder("%player%", player.getName())));
            }

            // Check if the player is a tagger
            if (arena.getTaggers().contains(player.getUniqueId())) {
                // Give the player what he needs to be a tagger
                arena.addTagger(player, null);

                // Send to the players that the round has started
                MessagesConfiguration.ROUND_STARTED_TAGGED.sendList(player,
                        new Placeholder("%round%", String.valueOf(arena.getRound())),
                        new Placeholder("%players%", taggedPlayers));

                // Send to the tagged player the game started title and sound
                Utils.sendTitle(MessagesConfiguration.TITLES_GAME_STARTED_TAGGED.getString(player), player);
                Utils.playSound(MessagesConfiguration.SOUNDS_GAME_STARTED_TAGGED.getString(player), player);

            } else {
                // Send to the players that the round has started
                MessagesConfiguration.ROUND_STARTED.sendList(player,
                        new Placeholder("%round%", String.valueOf(arena.getRound())),
                        new Placeholder("%players%", taggedPlayers));

                // Send to the player the game started title and the sound
                Utils.sendTitle(MessagesConfiguration.TITLES_GAME_STARTED.getString(player), player);
                Utils.playSound(MessagesConfiguration.SOUNDS_GAME_STARTED.getString(player), player);

                // Add the speed effect to the player if enabled
                if (SettingsConfiguration.SPEED_UNTAGGED_ENABLED.getBoolean()) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, SettingsConfiguration.SPEED_UNTAGGED_LEVEL.getInt()));
                }
            }

            // Teleport the player to the arena spawn location
            player.teleport(arena.getSpawnLocation());
        }

        arena.setGameState(GameState.IN_GAME);

        // Start the game task
        new GamePlayingTask(plugin, arena).runTaskTimer(plugin, 0L, 20L);
    }

    @Override
    public void endGame(@NotNull IArena arena) {
        arena.setGameState(GameState.RESTARTING);

        final List<UUID> topPlayers = plugin.getLeaderBoardManager().getPlayers(arena);

        final Player winner = Bukkit.getPlayer(topPlayers.get(0));

        final String firstPlace = topPlayers.size() >= 1 ? Bukkit.getOfflinePlayer(topPlayers.get(0)).getName() : MessagesConfiguration.PLACEHOLDERS_NONE.getString(winner);
        final String secondPlace = topPlayers.size() >= 2 ? Bukkit.getOfflinePlayer(topPlayers.get(1)).getName() : MessagesConfiguration.PLACEHOLDERS_NONE.getString(winner);
        final String thirdPlace = topPlayers.size() == 3 ? Bukkit.getOfflinePlayer(topPlayers.get(2)).getName() : MessagesConfiguration.PLACEHOLDERS_NONE.getString(winner);

        arena.sendList(MessagesConfiguration.GAME_ENDED.getStringList(null,
                new Placeholder("%player-1%", firstPlace),
                new Placeholder("%player-2%", secondPlace),
                new Placeholder("%player-3%", thirdPlace)));

        Bukkit.getScheduler().runTaskLater(plugin, arena::clear, SettingsConfiguration.COUNTDOWNS_GAME_RESTART.getInt() * 20L);
    }
}
