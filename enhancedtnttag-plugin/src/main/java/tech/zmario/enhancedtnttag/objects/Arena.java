package tech.zmario.enhancedtnttag.objects;

import com.google.common.collect.Lists;
import lombok.Data;
import org.bukkit.*;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tech.zmario.enhancedtnttag.EnhancedTNTTag;
import tech.zmario.enhancedtnttag.api.enums.GameState;
import tech.zmario.enhancedtnttag.api.objects.ArenaConfig;
import tech.zmario.enhancedtnttag.api.objects.IArena;
import tech.zmario.enhancedtnttag.api.objects.Placeholder;
import tech.zmario.enhancedtnttag.enums.MessagesConfiguration;
import tech.zmario.enhancedtnttag.enums.SettingsConfiguration;
import tech.zmario.enhancedtnttag.tasks.GameStartingTask;
import tech.zmario.enhancedtnttag.utils.Utils;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Data
public class Arena implements IArena {

    private final EnhancedTNTTag plugin;

    private final String arenaName;

    private final ArenaConfig arenaConfig;

    private final List<UUID> players = Lists.newArrayList();
    private final List<UUID> taggers = Lists.newArrayList();
    private final List<UUID> spectators = Lists.newArrayList();

    private int minPlayers;
    private int maxPlayers;

    private Location lobbyLocation;
    private Location spawnLocation;
    private Location spectatorLocation;

    // Cached variables

    private GameState gameState = GameState.WAITING;

    private int round = 1;

    private int countdown = SettingsConfiguration.COUNTDOWNS_GAME_START.getInt();

    private int explosionTime = SettingsConfiguration.EXPLOSION_DELAY_BASE.getInt();

    public Arena(EnhancedTNTTag plugin, String arenaName) {
        this.plugin = plugin;

        this.arenaName = arenaName;
        this.arenaConfig = new ArenaConfig(plugin, this);

        final Configuration config = arenaConfig.getConfig();

        this.minPlayers = config.getInt("minPlayers");
        this.maxPlayers = config.getInt("maxPlayers");

        this.lobbyLocation = Utils.deserializeLocation(config.getString("lobbyLocation"));
        this.spawnLocation = Utils.deserializeLocation(config.getString("spawnLocation"));
        this.spectatorLocation = Utils.deserializeLocation(config.getString("spectatorLocation"));

        plugin.getServer().createWorld(new WorldCreator(spawnLocation.getWorld().getName()));

        new GameStartingTask(plugin, this).runTaskTimer(plugin, 0L, 20L);
    }

    @Override
    public void increaseRound(int amount) {
        round += amount;
    }

    @Override
    public void selectTaggers() {
        taggers.clear();

        final int playersToPick = (int) Math.ceil(players.size() * (SettingsConfiguration.TAGGED_PLAYERS_PERCENTAGE.getInt() / 100.0));

        final List<UUID> selectedPlayers = Lists.newArrayList(players);

        for (int i = 0; i < playersToPick; i++) {
            ThreadLocalRandom random = ThreadLocalRandom.current();
            int index = random.nextInt(selectedPlayers.size());

            Player randomPlayer = Bukkit.getPlayer(selectedPlayers.get(index));

            getTaggers().add(randomPlayer.getUniqueId());
            selectedPlayers.remove(index);
        }
    }

    @Override
    public void addPlayer(Player player) {
        // Add player to the arena players list
        players.add(player.getUniqueId());

        // Add player to the arena by player hashmap
        plugin.getArenaManager().addPlayerToMap(player, this);

        // Remove build mode if enabled
        plugin.getLocalStorage().getBuildPlayers().remove(player.getUniqueId());
        
        // Send arena join message
        MessagesConfiguration.ARENA_JOIN_MOTD.sendList(player,
                new Placeholder("%arena%", getArenaName()),
                new Placeholder("%players%", String.valueOf(players.size())),
                new Placeholder("%max-players%", String.valueOf(getMaxPlayers())));

        // Send to the players that are in the arena the message that a new player has joined
        sendMessage(MessagesConfiguration.PLAYER_JOINED.getString(player,
                new Placeholder("%player%", player.getName()),
                new Placeholder("%arena%", getArenaName()),
                new Placeholder("%players%", String.valueOf(players.size())),
                new Placeholder("%max-players%", String.valueOf(getMaxPlayers()))));

        // Update the player list
        if (SettingsConfiguration.TAB_LIST_FORMAT_WAITING.getBoolean()) {
            player.setDisplayName(MessagesConfiguration.TAB_LIST_WAITING.getString(player,
                    new Placeholder("%player%", player.getName())));
            player.setPlayerListName(MessagesConfiguration.TAB_LIST_WAITING.getString(player,
                    new Placeholder("%player%", player.getName())));
        }

        // Clear inventory
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);

        // Teleport the player to the lobby location
        player.teleport(lobbyLocation);

        // Hide the players from the player list
        Utils.hidePlayers(player, plugin.getArenaManager());

        // Check if we can start the game
        if (players.size() >= minPlayers && gameState == GameState.WAITING) {
            setGameState(GameState.STARTING);
        }
    }

    @Override
    public void addTagger(@NotNull Player player, @Nullable Player damager) {
        // Spawn a firework if enabled
        if (SettingsConfiguration.TAG_FIREWORK_ENABLED.getBoolean())
            Utils.spawnFireworks(player.getLocation(), SettingsConfiguration.TAG_FIREWORK_POWER.getInt(),
                    FireworkEffect.Type.valueOf(SettingsConfiguration.TAG_FIREWORK_TYPE.getString()));

        // Set the player inventory as a tagger (TNT as head and in the hotbar)
        player.getInventory().setHelmet(new ItemStack(Material.TNT));

        for (int i = 0; i < 9; i++) {
            player.getInventory().setItem(i, new ItemStack(Material.TNT));
        }

        player.updateInventory();

        // Update the player list
        if (SettingsConfiguration.TAB_LIST_FORMAT_TAGGED.getBoolean()) {
            player.setDisplayName((MessagesConfiguration.TAB_LIST_TAGGED.getString(player,
                    new Placeholder("%player%", player.getName()))));
            player.setPlayerListName(MessagesConfiguration.TAB_LIST_TAGGED.getString(player,
                    new Placeholder("%player%", player.getName())));
        }

        // Add the speed effect to the tagger if enabled
        if (SettingsConfiguration.SPEED_TAGGED_ENABLED.getBoolean()) {
            player.removePotionEffect(PotionEffectType.SPEED);
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, SettingsConfiguration.SPEED_TAGGED_LEVEL.getInt()));
        }

        // Check if the damager is not null
        if (damager != null) {
            // Send to every player that a player has been tagged
            sendMessage(MessagesConfiguration.TAGGED_BROADCAST.getString(player,
                    new Placeholder("%player%", player.getName())));

            // Send the player that tagged the player a message
            MessagesConfiguration.TARGET_TAGGED.send(damager,
                    new Placeholder("%player%", player.getName()));

            // Send the player that is now tagged a message
            MessagesConfiguration.PLAYER_TAGGED.send(player,
                    new Placeholder("%player%", damager.getName()));
        }
    }

    @Override
    public void addSpectator(@NotNull Player player) {
        // Add player to the arena spectators list
        players.remove(player.getUniqueId());
        spectators.add(player.getUniqueId());

        // Clear the player inventory
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);

        player.teleport(spectatorLocation);

        // TODO: spectator settings items

        // Update the player list
        if (SettingsConfiguration.TAB_LIST_FORMAT_SPECTATOR.getBoolean())
            player.setPlayerListName(MessagesConfiguration.TAB_LIST_SPECTATOR.getString(player,
                    new Placeholder("%player%", player.getName())));

        // Send to every player that a player has been eliminated
        sendMessage(MessagesConfiguration.PLAYER_BLOWN_UP.getString(player,
                new Placeholder("%player%", player.getName())));

        // Hide the players from the player list
        Utils.hidePlayers(player, plugin.getArenaManager());
    }

    @Override
    public void removePlayer(Player player) {
        // Remove the player from the arena by player hashmap
        plugin.getArenaManager().removePlayerFromMap(player);

        // Clear the player inventory
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);

        player.teleport(Utils.getMainLobby());

        Utils.hidePlayers(player, plugin.getArenaManager());

        // Update the player list
        if (SettingsConfiguration.TAB_LIST_FORMAT_LOBBY.getBoolean()) {
            player.setDisplayName(MessagesConfiguration.TAB_LIST_LOBBY.getString(player,
                    new Placeholder("%player%", player.getName())));
            player.setPlayerListName(MessagesConfiguration.TAB_LIST_LOBBY.getString(player,
                    new Placeholder("%player%", player.getName())));
        }
    }

    @Override
    public void removeTagger(Player player) {
        // Remove player from the arena taggers list
        taggers.remove(player.getUniqueId());

        // Clear the player inventory
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);

        // Remove the tagger speed effect
        if (SettingsConfiguration.SPEED_UNTAGGED_ENABLED.getBoolean()) {
            player.removePotionEffect(PotionEffectType.SPEED);
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, SettingsConfiguration.SPEED_UNTAGGED_LEVEL.getInt()));
        }

        // Update the player list
        if (SettingsConfiguration.TAB_LIST_FORMAT_UNTAGGED.getBoolean()) {
            player.setDisplayName(MessagesConfiguration.TAB_LIST_UNTAGGED.getString(player,
                    new Placeholder("%player%", player.getName())));
            player.setPlayerListName(MessagesConfiguration.TAB_LIST_UNTAGGED.getString(player,
                    new Placeholder("%player%", player.getName())));
        }
    }

    @Override
    public void clear() {
        gameState = GameState.WAITING;
        round = 1;
        countdown = SettingsConfiguration.COUNTDOWNS_GAME_START.getInt();
        explosionTime = SettingsConfiguration.EXPLOSION_DELAY_BASE.getInt();

        Location mainLobby = Utils.getMainLobby();

        for (UUID playerUuid : players) {
            Player player = Bukkit.getPlayer(playerUuid);

            player.teleport(mainLobby);

            removePlayer(player);
        }

        for (UUID spectatorUuid : spectators) {
            Player spectator = Bukkit.getPlayer(spectatorUuid);

            spectator.teleport(mainLobby);

            removePlayer(spectator);
        }

        players.clear();
        spectators.clear();
        taggers.clear();

        new GameStartingTask(plugin, this).runTaskTimer(plugin, 0L, 20L);
    }

    @Override
    public void sendMessage(String message) {
        for (UUID playerUuid : players) {
            Bukkit.getPlayer(playerUuid).sendMessage(message);
        }

        for (UUID spectatorUuid : spectators) {
            Bukkit.getPlayer(spectatorUuid).sendMessage(message);
        }
    }

    @Override
    public void sendList(List<String> messages) {
        for (UUID playerUuid : players) {
            Player player = Bukkit.getPlayer(playerUuid);

            for (String message : messages) {
                player.sendMessage(message);
            }
        }

        for (UUID spectatorUuid : spectators) {
            Player spectator = Bukkit.getPlayer(spectatorUuid);

            for (String message : messages) {
                spectator.sendMessage(message);
            }
        }
    }

    @Override
    public void handleQuit(Player player) {
        players.remove(player.getUniqueId());
        taggers.remove(player.getUniqueId());
        spectators.remove(player.getUniqueId());

        plugin.getArenaManager().removePlayerFromMap(player);

        player.getInventory().clear();
        player.getInventory().setArmorContents(null);

        if (gameState == GameState.WAITING || gameState == GameState.STARTING) {
            sendMessage(MessagesConfiguration.PLAYER_LEFT.getString(player,
                    new Placeholder("%player%", player.getName()),
                    new Placeholder("%players%", String.valueOf(players.size())),
                    new Placeholder("%max-players%", String.valueOf(maxPlayers))));
        }
    }
}
