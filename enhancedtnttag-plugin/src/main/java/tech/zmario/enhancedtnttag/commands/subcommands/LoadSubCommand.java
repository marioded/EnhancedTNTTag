package tech.zmario.enhancedtnttag.commands.subcommands;

import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tech.zmario.enhancedtnttag.EnhancedTNTTag;
import tech.zmario.enhancedtnttag.api.manager.interfaces.SubCommand;
import tech.zmario.enhancedtnttag.api.objects.IArena;
import tech.zmario.enhancedtnttag.enums.MessagesConfiguration;
import tech.zmario.enhancedtnttag.enums.SettingsConfiguration;
import tech.zmario.enhancedtnttag.objects.Arena;

@RequiredArgsConstructor
public class LoadSubCommand implements SubCommand {

    private final EnhancedTNTTag plugin;

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        if (args.length < 1) {
            MessagesConfiguration.SUBCOMMAND_LOAD_USAGE.send(player);
            return;
        }

        if (plugin.getArenaManager().getArena(args[1].toLowerCase()).isPresent()) {
            player.sendMessage(MessagesConfiguration.SUBCOMMAND_LOAD_ALREADY_LOADED.getString(player));
            return;
        }
        IArena arena = new Arena(plugin, args[1].toLowerCase());

        plugin.getArenaManager().enableArena(arena);
        player.sendMessage(MessagesConfiguration.SUBCOMMAND_LOAD_SUCCESS.getString(player));
    }

    @Override
    public String getPermission() {
        return SettingsConfiguration.COMMANDS_LOAD_PERMISSION.getString();
    }

    @Override
    public boolean useConsole() {
        return true;
    }
}
