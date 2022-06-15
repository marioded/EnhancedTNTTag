package tech.zmario.enhancedtnttag.commands.subcommands;

import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tech.zmario.enhancedtnttag.EnhancedTNTTag;
import tech.zmario.enhancedtnttag.api.commands.interfaces.SubCommand;
import tech.zmario.enhancedtnttag.enums.MessagesConfiguration;
import tech.zmario.enhancedtnttag.enums.SettingsConfiguration;
import tech.zmario.enhancedtnttag.utils.Utils;

@RequiredArgsConstructor
public class SetMainLobbySubCommand implements SubCommand {

    private final EnhancedTNTTag plugin;

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        plugin.getConfig().set("main-lobby-location", Utils.serializeLocation(player.getLocation()));
        plugin.saveConfig();

        MessagesConfiguration.SUBCOMMAND_SET_MAIN_LOBBY_SUCCESS.send(player);
    }

    @Override
    public String getPermission() {
        return SettingsConfiguration.COMMANDS_SET_MAIN_LOBBY_PERMISSION.getString();
    }

    @Override
    public boolean useConsole() {
        return false;
    }
}
