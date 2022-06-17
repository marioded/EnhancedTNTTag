package tech.zmario.enhancedtnttag.managers;

import com.google.common.collect.Maps;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import tech.zmario.enhancedtnttag.EnhancedTNTTag;
import tech.zmario.enhancedtnttag.api.manager.ISetupManager;
import tech.zmario.enhancedtnttag.api.objects.IArena;
import tech.zmario.enhancedtnttag.objects.Arena;
import tech.zmario.enhancedtnttag.utils.Utils;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Getter
public class SetupManager implements ISetupManager {

    private final EnhancedTNTTag plugin;
    private final Map<UUID, IArena> setupPlayers = Maps.newHashMap();

    public SetupManager(EnhancedTNTTag plugin) {
        this.plugin = plugin;
    }

    public void start(Player player, String arenaName) {
        plugin.getLocalStorage().getBuildPlayers().remove(player.getUniqueId());
        plugin.getLocalStorage().getBuildPlayers().add(player.getUniqueId());

        setupPlayers.put(player.getUniqueId(), new Arena(plugin, arenaName));
    }

    public void save(Player player) {
        IArena arena = setupPlayers.get(player.getUniqueId());

        FileConfiguration configuration = arena.getArenaConfig().getConfig();

        configuration.set("maxPlayers", arena.getMaxPlayers());
        configuration.set("minPlayers", arena.getMinPlayers());
        configuration.set("lobbyLocation", Utils.serializeLocation(arena.getLobbyLocation()));
        configuration.set("spawnLocation", Utils.serializeLocation(arena.getSpawnLocation()));
        configuration.set("spectatorLocation", Utils.serializeLocation(arena.getSpectatorLocation()));

        arena.getArenaConfig().save();

        plugin.getArenaManager().enableArena(arena);
        setupPlayers.remove(player.getUniqueId());
    }

    public Optional<IArena> getArena(Player player) {
        return Optional.ofNullable(setupPlayers.get(player.getUniqueId()));
    }

    public boolean isInSetup(Player player) {
        return setupPlayers.containsKey(player.getUniqueId());
    }

    public void removeAll() {
        setupPlayers.clear();
    }
}
