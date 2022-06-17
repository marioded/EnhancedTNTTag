package tech.zmario.enhancedtnttag.commands.subcommands;

import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tech.zmario.enhancedtnttag.EnhancedTNTTag;
import tech.zmario.enhancedtnttag.api.manager.interfaces.SubCommand;
import tech.zmario.enhancedtnttag.api.objects.IArena;
import tech.zmario.enhancedtnttag.enums.MessagesConfiguration;
import tech.zmario.enhancedtnttag.enums.SettingsConfiguration;

import java.util.Optional;

@RequiredArgsConstructor
public class UnloadSubCommand implements SubCommand {

    private final EnhancedTNTTag plugin;

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        if (args.length < 1) {
            MessagesConfiguration.SUBCOMMAND_UNLOAD_USAGE.send(player);
            return;
        }
        Optional<IArena> arenaOptional = plugin.getArenaManager().getArena(args[1].toLowerCase());

        if (!arenaOptional.isPresent()) {
            player.sendMessage(MessagesConfiguration.SUBCOMMAND_UNLOAD_NOT_EXIST.getString(player));
            return;
        }
        IArena arena = arenaOptional.get();

        plugin.getArenaManager().disableArena(arena);
        player.sendMessage(MessagesConfiguration.SUBCOMMAND_UNLOAD_SUCCESS.getString(player));
    }

    @Override
    public String getPermission() {
        return SettingsConfiguration.COMMANDS_UNLOAD_PERMISSION.getString();
    }

    @Override
    public boolean useConsole() {
        return true;
    }
}
