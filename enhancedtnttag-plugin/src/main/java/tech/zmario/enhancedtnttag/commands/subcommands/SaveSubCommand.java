package tech.zmario.enhancedtnttag.commands.subcommands;

import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tech.zmario.enhancedtnttag.EnhancedTNTTag;
import tech.zmario.enhancedtnttag.api.commands.interfaces.SubCommand;
import tech.zmario.enhancedtnttag.enums.MessagesConfiguration;
import tech.zmario.enhancedtnttag.enums.SettingsConfiguration;

@RequiredArgsConstructor
public class SaveSubCommand implements SubCommand {

    private final EnhancedTNTTag plugin;

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        if (!plugin.getSetupManager().isInSetup(player)) {
            MessagesConfiguration.SUBCOMMAND_SETUP_NOT_SETTING.send(player);
            return;
        }

        if (SettingsConfiguration.MAIN_LOBBY_LOCATION.getString() == null) {
            MessagesConfiguration.MAIN_LOBBY_NOT_SET.send(player);
            return;
        }

        plugin.getSetupManager().save(player);

        MessagesConfiguration.SUBCOMMAND_SAVE_SUCCESS.send(player);
    }

    @Override
    public String getPermission() {
        return SettingsConfiguration.COMMANDS_SAVE_PERMISSION.getString();
    }

    @Override
    public boolean useConsole() {
        return false;
    }
}
