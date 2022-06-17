package tech.zmario.enhancedtnttag.commands.subcommands.setup;

import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tech.zmario.enhancedtnttag.EnhancedTNTTag;
import tech.zmario.enhancedtnttag.api.manager.interfaces.SubCommand;
import tech.zmario.enhancedtnttag.api.objects.IArena;
import tech.zmario.enhancedtnttag.enums.MessagesConfiguration;
import tech.zmario.enhancedtnttag.enums.SettingsConfiguration;
import tech.zmario.enhancedtnttag.objects.Placeholder;

import java.util.Optional;

@RequiredArgsConstructor
public class SetMinPlayersSubCommand implements SubCommand {

    private final EnhancedTNTTag plugin;

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        Optional<IArena> arenaOptional = plugin.getSetupManager().getArena(player);

        if (!arenaOptional.isPresent()) {
            MessagesConfiguration.SUBCOMMAND_SETUP_NOT_SETTING.send(player);
            return;
        }

        if (args.length < 1) {
            MessagesConfiguration.SUBCOMMAND_SET_MIN_PLAYERS_USAGE.send(player);
            return;
        }

        int minPlayers;

        try {
            minPlayers = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            MessagesConfiguration.SUBCOMMAND_SET_MIN_PLAYERS_NOT_A_NUMBER.send(player);
            return;
        }

        if (minPlayers < 1) {
            MessagesConfiguration.SUBCOMMAND_SET_MIN_PLAYERS_NOT_A_NUMBER.send(player);
            return;
        }

        IArena arena = arenaOptional.get();
        arena.setMinPlayers(minPlayers);

        MessagesConfiguration.SUBCOMMAND_SET_MIN_PLAYERS_SUCCESS.send(player, new Placeholder("%min-players%", String.valueOf(minPlayers)));
    }

    @Override
    public String getPermission() {
        return SettingsConfiguration.COMMANDS_SET_MIN_PLAYERS_PERMISSION.getString();
    }

    @Override
    public boolean useConsole() {
        return false;
    }
}
