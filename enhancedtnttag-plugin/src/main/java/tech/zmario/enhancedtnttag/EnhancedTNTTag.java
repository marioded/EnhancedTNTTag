package tech.zmario.enhancedtnttag;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import tech.zmario.enhancedtnttag.api.EnhancedTNTTagAPI;
import tech.zmario.enhancedtnttag.api.commands.interfaces.SubCommand;
import tech.zmario.enhancedtnttag.api.manager.IArenaManager;
import tech.zmario.enhancedtnttag.api.manager.IGameManager;
import tech.zmario.enhancedtnttag.api.manager.ILeaderBoardManager;
import tech.zmario.enhancedtnttag.api.manager.ISetupManager;
import tech.zmario.enhancedtnttag.api.objects.TextFile;
import tech.zmario.enhancedtnttag.commands.CommandManager;
import tech.zmario.enhancedtnttag.hooks.PlaceholderAPIHook;
import tech.zmario.enhancedtnttag.listeners.DamageListener;
import tech.zmario.enhancedtnttag.listeners.GeneralListeners;
import tech.zmario.enhancedtnttag.listeners.PlayerChatListener;
import tech.zmario.enhancedtnttag.listeners.PlayerJoinQuitListener;
import tech.zmario.enhancedtnttag.managers.ArenaManager;
import tech.zmario.enhancedtnttag.managers.GameManager;
import tech.zmario.enhancedtnttag.managers.LeaderBoardManager;
import tech.zmario.enhancedtnttag.managers.SetupManager;
import tech.zmario.enhancedtnttag.storage.LocalStorage;
import tech.zmario.enhancedtnttag.tasks.PlayerUpdateTask;

@Getter
public final class EnhancedTNTTag extends JavaPlugin implements EnhancedTNTTagAPI {


    // TODO: powerups, remote database (mysql, sqlite), placeholderapi

    @Getter
    private static EnhancedTNTTag instance;

    private IArenaManager arenaManager;
    private IGameManager gameManager;
    private ISetupManager setupManager;
    private ILeaderBoardManager leaderBoardManager;
    private LocalStorage localStorage;

    private CommandManager commandManager;

    private TextFile messagesFile;

    @Override
    public void onEnable() {
        getLogger().info("Initializing the plugin...");
        loadConfigurations();
        startInstances();
        registerListeners(new PlayerJoinQuitListener(this), new DamageListener(this),
                new GeneralListeners(this), new PlayerChatListener(this));
        loadArenas();
        registerHooks();
        new PlayerUpdateTask(this).runTaskTimerAsynchronously(this, 1L, 1L);
    }

    private void registerHooks() {
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new PlaceholderAPIHook(this).register();
        }
    }

    private void loadArenas() {
        getLogger().info("Loading arenas...");
        arenaManager.loadArenas();
        getLogger().info("Loaded " + arenaManager.getArenas().size() + " arenas.");
    }

    private void registerListeners(Listener... listeners) {
        for (Listener listener : listeners) {
            getServer().getPluginManager().registerEvents(listener, this);
        }
    }

    private void loadConfigurations() {
        getLogger().info("Loading configurations...");
        saveDefaultConfig();
        messagesFile = new TextFile(this, getDataFolder(), "messages.yml");
    }

    private void startInstances() {
        getLogger().info("Starting instances...");
        instance = this;
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

        setupManager.removeAll();
        arenaManager.unloadArenas();
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
