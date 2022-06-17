package tech.zmario.enhancedtnttag;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import tech.zmario.enhancedtnttag.api.EnhancedTNTTagAPI;
import tech.zmario.enhancedtnttag.api.manager.interfaces.SubCommand;
import tech.zmario.enhancedtnttag.api.manager.IArenaManager;
import tech.zmario.enhancedtnttag.api.manager.IGameManager;
import tech.zmario.enhancedtnttag.api.manager.ILeaderBoardManager;
import tech.zmario.enhancedtnttag.api.manager.ISetupManager;
import tech.zmario.enhancedtnttag.commands.CommandManager;
import tech.zmario.enhancedtnttag.hooks.PlaceholderAPIHook;
import tech.zmario.enhancedtnttag.listeners.*;
import tech.zmario.enhancedtnttag.managers.ArenaManager;
import tech.zmario.enhancedtnttag.managers.GameManager;
import tech.zmario.enhancedtnttag.managers.LeaderBoardManager;
import tech.zmario.enhancedtnttag.managers.SetupManager;
import tech.zmario.enhancedtnttag.objects.ConfigFile;
import tech.zmario.enhancedtnttag.sql.DatabaseManager;
import tech.zmario.enhancedtnttag.storage.LocalStorage;
import tech.zmario.enhancedtnttag.tasks.PlayerUpdateTask;

@Getter
public final class EnhancedTNTTag extends JavaPlugin implements EnhancedTNTTagAPI {


    // TODO: powerups

    @Getter
    private static EnhancedTNTTag instance;

    private IArenaManager arenaManager;
    private IGameManager gameManager;
    private ISetupManager setupManager;
    private ILeaderBoardManager leaderBoardManager;
    private LocalStorage localStorage;

    private DatabaseManager databaseManager;
    private CommandManager commandManager;

    private ConfigFile messagesFile;

    @Override
    public void onEnable() {
        loadConfigurations();
        startInstances();
        registerListeners(new PlayerJoinQuitListener(this), new DamageListener(this),
                new GeneralListeners(this), new PlayerChatListener(this),
                new PlayerInteractListener(), new InventoryInteractListener(this));
        loadArenas();
        registerHooks();

        new PlayerUpdateTask(this).runTaskTimerAsynchronously(this, 1L, 1L);

        Bukkit.getServicesManager().register(EnhancedTNTTagAPI.class, this, this, ServicePriority.Normal);

        getLogger().info("The plugin has been enabled!");
    }

    private void registerHooks() {
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new PlaceholderAPIHook(this).register();

            getLogger().info("Hooked into PlaceholderAPI!");
        }
    }

    private void loadArenas() {
        arenaManager.loadArenas();

        getLogger().info("Loaded " + arenaManager.getArenas().size() + " arenas.");
    }

    private void registerListeners(Listener... listeners) {
        for (Listener listener : listeners) {
            getServer().getPluginManager().registerEvents(listener, this);
        }
    }

    private void loadConfigurations() {
        saveDefaultConfig();
        messagesFile = new ConfigFile(this, getDataFolder(), "messages.yml");

        getLogger().info("Loaded configurations!");
    }

    private void startInstances() {
        getLogger().info("Starting instances...");

        instance = this;
        databaseManager = new DatabaseManager(this);
        arenaManager = new ArenaManager(this);
        gameManager = new GameManager(this);
        setupManager = new SetupManager(this);
        leaderBoardManager = new LeaderBoardManager();
        localStorage = new LocalStorage();
        commandManager = new CommandManager(this);
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabling the plugin...");

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.kickPlayer(ChatColor.RED + "The server has been reloaded. Please rejoin.");
        }

        databaseManager.close();
        setupManager.removeAll();
        arenaManager.unloadArenas();
        leaderBoardManager.disable();
        databaseManager = null;
        arenaManager = null;
        gameManager = null;
        leaderBoardManager = null;
        setupManager = null;
        messagesFile = null;
        instance = null;
    }

    public Configuration getMessages() {
        return messagesFile.getConfig();
    }

    @Override
    public void registerSubCommand(String name, SubCommand subCommand) {
        commandManager.registerSubCommand(name, subCommand);
    }
}
