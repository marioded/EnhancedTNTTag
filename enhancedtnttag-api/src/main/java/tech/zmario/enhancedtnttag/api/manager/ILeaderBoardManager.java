package tech.zmario.enhancedtnttag.api.manager;

import org.bukkit.entity.Player;
import tech.zmario.enhancedtnttag.api.objects.IArena;

import java.util.List;
import java.util.UUID;

public interface ILeaderBoardManager {

    void addArena(IArena arena);

    List<UUID> getPlayers(IArena arena);

    void addPlayer(IArena arena, Player player);

    void clear(IArena arena);

}
