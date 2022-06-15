package tech.zmario.enhancedtnttag.commands;

import com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import tech.zmario.enhancedtnttag.EnhancedTNTTag;
import tech.zmario.enhancedtnttag.api.commands.interfaces.SubCommand;
import tech.zmario.enhancedtnttag.api.objects.Placeholder;
import tech.zmario.enhancedtnttag.commands.subcommands.*;
import tech.zmario.enhancedtnttag.commands.subcommands.setup.*;
import tech.zmario.enhancedtnttag.enums.MessagesConfiguration;
import tech.zmario.enhancedtnttag.enums.SettingsConfiguration;
import tech.zmario.enhancedtnttag.utils.Utils;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

public class CommandManager implements CommandExecutor {

    private final EnhancedTNTTag plugin;
    private final Map<String, SubCommand> subCommands = Maps.newHashMap();

    public CommandManager(EnhancedTNTTag plugin) {
        this.plugin = plugin;
        String label = SettingsConfiguration.COMMANDS_MAIN_NAME.getString();

        if (SettingsConfiguration.COMMANDS_MAIN_NAME.getString() == null || SettingsConfiguration.COMMANDS_MAIN_NAME.getString().isEmpty())
            label = "tnttag";

        PluginCommand pluginCommand = plugin.getCommand(label);
        String[] aliases = SettingsConfiguration.COMMANDS_MAIN_ALIASES.getStringList().toArray(new String[0]);

        if (pluginCommand == null) {
            try {
                CommandMap commandMap = (CommandMap) Utils.getPrivateField(Bukkit.getPluginManager(), "commandMap");
                Constructor<PluginCommand> commandConstructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);

                commandConstructor.setAccessible(true);

                pluginCommand = commandConstructor.newInstance(label, plugin);

                if (aliases.length > 0) {
                    for (String alias : aliases) {
                        if (alias == null || alias.isEmpty()) {
                            throw new RuntimeException("Empty or null alias.");
                        }

                        if (alias.contains(":")) {
                            throw new RuntimeException("Illegal characters in alias.");
                        }
                    }

                    pluginCommand.setAliases(Arrays.asList(aliases));
                }

                if (!commandMap.register(plugin.getName(), pluginCommand)) {
                    plugin.getLogger().severe("Failed to register the command. Maybe another plugin has registered a command with this name?");
                    return;
                }

            } catch (Exception e) {
                e.printStackTrace();
                plugin.getLogger().severe("Failed to register the command. Maybe another plugin has registered a command with this name?");
                return;
            }
        }

        pluginCommand.setExecutor(this);

        subCommands.put(SettingsConfiguration.COMMANDS_RELOAD_NAME.getString(), new ReloadSubCommand(plugin));
        subCommands.put(SettingsConfiguration.COMMANDS_BUILD_NAME.getString(), new BuildSubCommand(plugin));

        subCommands.put(SettingsConfiguration.COMMANDS_JOIN_NAME.getString(), new JoinSubCommand(plugin));
        subCommands.put(SettingsConfiguration.COMMANDS_LEAVE_NAME.getString(), new LeaveSubCommand(plugin));

        subCommands.put(SettingsConfiguration.COMMANDS_SETUP_NAME.getString(), new SetupSubCommand(plugin));

        subCommands.put(SettingsConfiguration.COMMANDS_SET_MAX_PLAYERS_NAME.getString(), new SetMaxPlayersSubCommand(plugin));
        subCommands.put(SettingsConfiguration.COMMANDS_SET_MIN_PLAYERS_NAME.getString(), new SetMinPlayersSubCommand(plugin));
        subCommands.put(SettingsConfiguration.COMMANDS_SET_LOBBY_NAME.getString(), new SetLobbySubCommand(plugin));
        subCommands.put(SettingsConfiguration.COMMANDS_SET_SPAWN_NAME.getString(), new SetSpawnSubCommand(plugin));
        subCommands.put(SettingsConfiguration.COMMANDS_SET_SPECTATOR_SPAWN_NAME.getString(), new SetSpectatorSpawnSubCommand(plugin));
        subCommands.put(SettingsConfiguration.COMMANDS_SET_MAIN_LOBBY_NAME.getString(), new SetMainLobbySubCommand(plugin));

        subCommands.put(SettingsConfiguration.COMMANDS_SAVE_NAME.getString(), new SaveSubCommand(plugin));
    }


    public void registerSubCommand(String label, SubCommand subCommand) {
        subCommands.put(label, subCommand);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission(SettingsConfiguration.COMMANDS_MAIN_PERMISSION.getString())) {
            MessagesConfiguration.NO_PERMISSION.send(sender instanceof Player ? (Player) sender : null);
            return true;
        }

        if (args.length > 0) {
            Optional<SubCommand> subCommandOptional = Optional.ofNullable(subCommands.get(args[0].toLowerCase()));

            if (subCommandOptional.isPresent()) {
                SubCommand subCommand = subCommandOptional.get();

                if (!subCommand.useConsole() && sender instanceof ConsoleCommandSender) {
                    MessagesConfiguration.NO_CONSOLE.send(sender);
                    return true;
                }

                String permission = subCommand.getPermission();

                if (permission != null && !permission.isEmpty() && !sender.hasPermission(permission)) {
                    MessagesConfiguration.NO_PERMISSION.send(sender);
                    return true;
                }

                subCommand.execute(sender, args);

                return true;
            }
        }

        if (!sender.hasPermission(SettingsConfiguration.ADMIN_PERMISSION.getString())) {
            MessagesConfiguration.COMMANDS_USER_HELP.sendList(sender,
                    new Placeholder("%version%", plugin.getDescription().getVersion()));
        } else {
            MessagesConfiguration.COMMANDS_ADMIN_HELP.sendList(sender,
            new Placeholder("%version%", plugin.getDescription().getVersion()));
        }
        return true;
    }
}
