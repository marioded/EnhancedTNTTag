package tech.zmario.enhancedtnttag.api.manager;

import org.bukkit.entity.Player;
import tech.zmario.enhancedtnttag.api.objects.IArena;

import java.util.Map;
import java.util.Optional;

public interface IArenaManager {

    Map<String, IArena> getArenas();

    void enableArena(IArena arena);

    void disableArena(IArena arena);

    void deleteArena(IArena arena);

    Optional<IArena> getRandomArena();

    Optional<IArena> getArena(String name);

    Optional<IArena> getArena(Player player);

    void addPlayerToMap(Player player, IArena arena);

    void removePlayerFromMap(Player player);

    void loadArenas();

    void unloadArenas();

}
