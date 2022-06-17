package tech.zmario.enhancedtnttag.managers;

import com.google.common.collect.Maps;
import lombok.Getter;
import org.bukkit.entity.Player;
import tech.zmario.enhancedtnttag.api.manager.ILeaderBoardManager;
import tech.zmario.enhancedtnttag.api.objects.IArena;

import java.util.*;

@Getter
public class LeaderBoardManager implements ILeaderBoardManager {

    private final Map<String, List<UUID>> topPlayers = Maps.newHashMap();

    @Override
    public void addArena(IArena arena) {
        this.topPlayers.put(arena.getArenaName(), new ArrayList<>());
    }

    @Override
    public List<UUID> getPlayers(IArena arena) {
        List<UUID> players = this.topPlayers.get(arena.getArenaName());
        Collections.reverse(players);
        return players;
    }

    @Override
    public void addPlayer(IArena arena, Player player) {
        topPlayers.get(arena.getArenaName()).add(player.getUniqueId());
    }

    @Override
    public void clear(IArena arena) {
        topPlayers.remove(arena.getArenaName());
    }

    @Override
    public void disable() {
        topPlayers.clear();
    }
}
