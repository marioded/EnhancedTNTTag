package tech.zmario.enhancedtnttag.api.objects;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import tech.zmario.enhancedtnttag.api.enums.GameState;

import java.util.List;
import java.util.UUID;

public interface IArena {

    String getArenaName();

    List<UUID> getPlayers();

    List<UUID> getTaggers();

    List<UUID> getSpectators();

    void setGameState(GameState gameState);

    GameState getGameState();

    ArenaConfig getArenaConfig();

    void setMinPlayers(int minPlayers);

    int getMinPlayers();

    void setMaxPlayers(int maxPlayers);

    int getMaxPlayers();

    Location getLobbyLocation();

    void setLobbyLocation(Location lobbyLocation);

    Location getSpawnLocation();

    void setSpawnLocation(Location spawnLocation);

    Location getSpectatorLocation();

    void setSpectatorLocation(Location spectatorLocation);

    int getRound();

    void increaseRound(int amount);

    void selectTaggers();

    void addPlayer(Player player);

    void addTagger(Player player, Player damager);

    void addSpectator(Player player);

    void removePlayer(Player player);

    void removeTagger(Player player);

    void clear();

    void sendMessage(String message);

    void sendList(List<String> messages);

    void handleQuit(Player player);

    int getCountdown();

    int getExplosionTime();

    void setCountdown(int seconds);

    void setExplosionTime(int seconds);
}
