package tech.zmario.enhancedtnttag.commands.subcommands;

import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;
import tech.zmario.enhancedtnttag.EnhancedTNTTag;
import tech.zmario.enhancedtnttag.api.manager.interfaces.SubCommand;
import tech.zmario.enhancedtnttag.enums.MessagesConfiguration;
import tech.zmario.enhancedtnttag.enums.SettingsConfiguration;

@RequiredArgsConstructor
public class ReloadSubCommand implements SubCommand {

    private final EnhancedTNTTag plugin;

    @Override
    public void execute(CommandSender sender, String[] args) {
        plugin.reloadConfig();
        plugin.getMessagesFile().reload();

        MessagesConfiguration.SUBCOMMAND_RELOAD_SUCCESS.send(sender);
    }

    @Override
    public String getPermission() {
        return SettingsConfiguration.COMMANDS_RELOAD_PERMISSION.getString();
    }

    @Override
    public boolean useConsole() {
        return true;
    }
}
