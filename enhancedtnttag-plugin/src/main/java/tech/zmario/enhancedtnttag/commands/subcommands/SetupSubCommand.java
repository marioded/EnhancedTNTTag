package tech.zmario.enhancedtnttag.commands.subcommands;

import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tech.zmario.enhancedtnttag.EnhancedTNTTag;
import tech.zmario.enhancedtnttag.api.manager.interfaces.SubCommand;
import tech.zmario.enhancedtnttag.enums.MessagesConfiguration;
import tech.zmario.enhancedtnttag.enums.SettingsConfiguration;

@RequiredArgsConstructor
public class SetupSubCommand implements SubCommand {

    private final EnhancedTNTTag plugin;

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        if (SettingsConfiguration.MAIN_LOBBY_LOCATION.getString() == null) {
            MessagesConfiguration.MAIN_LOBBY_NOT_SET.send(player);
            return;
        }

        if (args.length < 1) {
            MessagesConfiguration.SUBCOMMAND_SETUP_USAGE.send(player);
            return;
        }

        if (plugin.getSetupManager().isInSetup(player)) {
            MessagesConfiguration.SUBCOMMAND_SETUP_ALREADY_SETTING.send(player);
            return;
        }

        if (plugin.getArenaManager().getArena(args[1]).isPresent()) {
            MessagesConfiguration.SUBCOMMAND_SETUP_ALREADY_EXISTS.send(player);
            return;
        }

        MessagesConfiguration.SUBCOMMAND_SETUP_START.sendList(player);

        plugin.getSetupManager().start(player, args[1]);
    }

    @Override
    public String getPermission() {
        return SettingsConfiguration.COMMANDS_SETUP_PERMISSION.getString();
    }

    @Override
    public boolean useConsole() {
        return false;
    }
}
