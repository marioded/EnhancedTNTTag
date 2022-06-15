package tech.zmario.enhancedtnttag.tasks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import tech.zmario.enhancedtnttag.EnhancedTNTTag;
import tech.zmario.enhancedtnttag.api.enums.GameState;
import tech.zmario.enhancedtnttag.api.objects.IArena;
import tech.zmario.enhancedtnttag.api.objects.Placeholder;
import tech.zmario.enhancedtnttag.enums.MessagesConfiguration;
import tech.zmario.enhancedtnttag.enums.SettingsConfiguration;
import tech.zmario.enhancedtnttag.utils.Utils;

import java.util.UUID;

public class GameStartingTask extends BukkitRunnable {

    private final EnhancedTNTTag plugin;
    private final IArena arena;

    private int countdown;

    public GameStartingTask(EnhancedTNTTag plugin, IArena arena) {
        this.plugin = plugin;
        this.arena = arena;
        countdown = arena.getCountdown();
    }

    @Override
    public void run() {
        if (arena.getPlayers().size() < arena.getMinPlayers() && arena.getGameState() == GameState.WAITING) return;

        if (arena.getPlayers().size() < arena.getMinPlayers()) {
            arena.setGameState(GameState.WAITING);

            arena.sendMessage(MessagesConfiguration.NOT_ENOUGH_PLAYERS.getString(null,
                    new Placeholder("%players%", String.valueOf(arena.getPlayers().size()))));

            arena.setCountdown(SettingsConfiguration.COUNTDOWNS_GAME_START.getInt());
            return;
        }

        if (arena.getCountdown() != 1) { // If countdown is not 1, then reduce it by 1 and run this task again in 1 second
            arena.setCountdown(countdown--);
            for (UUID uuid : arena.getPlayers()) {
                Player player = Bukkit.getPlayer(uuid);

                MessagesConfiguration.GAME_STARTING.send(player,
                        new Placeholder("%countdown%", String.valueOf(countdown)),
                        new Placeholder("%second-or-seconds%", countdown == 1 ?
                        MessagesConfiguration.PLACEHOLDERS_SECOND.getString(player) : MessagesConfiguration.PLACEHOLDERS_SECONDS.getString(player)));

                // Send starting title and sound
                Utils.sendTitle(MessagesConfiguration.TITLES_GAME_STARTING.getString(player, new Placeholder("%countdown%", String.valueOf(countdown))), player);
                Utils.playSound(MessagesConfiguration.SOUNDS_GAME_STARTING.getString(player), player);
            }
            return;
        }

        // Start the game playing task
        plugin.getGameManager().startGame(arena);

        // Cancel the task, we don't need it anymore
        cancel();
    }
}
