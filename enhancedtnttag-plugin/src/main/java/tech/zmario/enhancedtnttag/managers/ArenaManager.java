package tech.zmario.enhancedtnttag.managers;

import com.google.common.collect.Maps;
import lombok.Getter;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import tech.zmario.enhancedtnttag.EnhancedTNTTag;
import tech.zmario.enhancedtnttag.api.enums.GameState;
import tech.zmario.enhancedtnttag.api.manager.IArenaManager;
import tech.zmario.enhancedtnttag.api.objects.IArena;
import tech.zmario.enhancedtnttag.objects.Arena;

import java.io.File;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Getter

public class ArenaManager implements IArenaManager {

    private final EnhancedTNTTag plugin;

    private final Map<String, IArena> arenas = Maps.newTreeMap(String.CASE_INSENSITIVE_ORDER);
    private final Map<UUID, IArena> arenasByPlayer = Maps.newHashMap();

    public ArenaManager(EnhancedTNTTag plugin) {
        this.plugin = plugin;
    }

    @Override
    public void enableArena(@NotNull IArena arena) {
        plugin.getServer().createWorld(new WorldCreator(arena.getSpawnLocation().getWorld().getName()));
        arenas.put(arena.getArenaName(), arena);
    }

    @Override
    public void disableArena(@NotNull IArena arena) {
        arenas.remove(arena.getArenaName());
    }

    @Override
    public void deleteArena(@NotNull IArena arena) {
        plugin.getGameManager().endGame(arena);

        arena.getArenaConfig().delete();
        arenas.remove(arena.getArenaName());
    }

    @Override
    public Optional<IArena> getRandomArena() {
        return arenas.values().stream().filter(arena ->
                (arena.getGameState() == GameState.WAITING || arena.getGameState() == GameState.STARTING)
                                && arena.getPlayers().size() < arena.getMaxPlayers()).findFirst();
    }

    @Override
    public Optional<IArena> getArena(@NotNull String name) {
        return Optional.ofNullable(arenas.get(name));
    }

    @Override
    public Optional<IArena> getArena(@NotNull Player player) {
        return Optional.ofNullable(arenasByPlayer.get(player.getUniqueId()));
    }

    @Override
    public void addPlayerToMap(Player player, IArena arena) {
        arenasByPlayer.put(player.getUniqueId(), arena);
    }

    @Override
    public void removePlayerFromMap(Player player) {
        arenasByPlayer.remove(player.getUniqueId());
    }

    @Override
    public void loadArenas() {
        File folder = new File(plugin.getDataFolder(), "arenas");
        if (!folder.exists()) {
            folder.mkdirs();
        }
        File[] files = folder.listFiles();
        if (files == null) {
            return;
        }
        for (File arenaFile : files) {
            if (!arenaFile.getName().endsWith(".yml"))
                return;
            IArena arena = new Arena(plugin, arenaFile.getName().replace(".yml", ""));
            arenas.put(arena.getArenaName(), arena);
        }
    }

    @Override
    public void unloadArenas() {
        for (IArena arena : arenas.values()) {
            if (arena.getGameState() == GameState.IN_GAME) {
                plugin.getGameManager().endGame(arena);
            }

            arena.clear();
        }

        arenas.clear();
    }
}
