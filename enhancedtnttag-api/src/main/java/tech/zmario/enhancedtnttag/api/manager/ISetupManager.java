package tech.zmario.enhancedtnttag.api.manager;

import org.bukkit.entity.Player;
import tech.zmario.enhancedtnttag.api.objects.IArena;

import java.util.Optional;

public interface ISetupManager {

    void start(Player player, String arenaName);

    void save(Player player);

    boolean isInSetup(Player player);

    Optional<IArena> getArena(Player player);

    void removeAll();

}
