package tech.zmario.enhancedtnttag.commands.subcommands.setup;

import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tech.zmario.enhancedtnttag.EnhancedTNTTag;
import tech.zmario.enhancedtnttag.api.commands.interfaces.SubCommand;
import tech.zmario.enhancedtnttag.api.objects.IArena;
import tech.zmario.enhancedtnttag.enums.MessagesConfiguration;
import tech.zmario.enhancedtnttag.enums.SettingsConfiguration;

import java.util.Optional;

@RequiredArgsConstructor
public class SetSpawnSubCommand implements SubCommand {

    private final EnhancedTNTTag plugin;

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        Optional<IArena> arenaOptional = plugin.getSetupManager().getArena(player);

        if (!arenaOptional.isPresent()) {
            MessagesConfiguration.SUBCOMMAND_SETUP_NOT_SETTING.send(player);
            return;
        }

        IArena arena = arenaOptional.get();
        arena.setSpawnLocation(player.getLocation());

        MessagesConfiguration.SUBCOMMAND_SET_SPAWN_SUCCESS.send(player);;
    }

    @Override
    public String getPermission() {
        return SettingsConfiguration.COMMANDS_SET_SPAWN_PERMISSION.getString();
    }

    @Override
    public boolean useConsole() {
        return false;
    }
}
