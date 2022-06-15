package tech.zmario.enhancedtnttag.commands.subcommands;

import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tech.zmario.enhancedtnttag.EnhancedTNTTag;
import tech.zmario.enhancedtnttag.api.commands.interfaces.SubCommand;
import tech.zmario.enhancedtnttag.api.enums.GameState;
import tech.zmario.enhancedtnttag.api.objects.IArena;
import tech.zmario.enhancedtnttag.enums.MessagesConfiguration;
import tech.zmario.enhancedtnttag.enums.SettingsConfiguration;

import java.util.Optional;

@RequiredArgsConstructor
public class JoinSubCommand implements SubCommand {

    private final EnhancedTNTTag plugin;

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        if (plugin.getArenaManager().getArena(player).isPresent()) {
            MessagesConfiguration.SUBCOMMAND_JOIN_ALREADY_IN_GAME.send(player);
            return;
        }

        // Handler quick join
        if (args.length < 1) {
            Optional<IArena> arenaOptional = plugin.getArenaManager().getRandomArena();

            if (!arenaOptional.isPresent()) {
                MessagesConfiguration.SUBCOMMAND_JOIN_NO_ARENA_FOUND.send(player);
                return;
            }

            arenaOptional.get().addPlayer(player);
            return;
        }

        Optional<IArena> arenaOptional = plugin.getArenaManager().getArena(args[1]);

        if (!arenaOptional.isPresent()) {
            MessagesConfiguration.SUBCOMMAND_JOIN_NOT_EXIST.send(player);
            return;
        }

        IArena arena = arenaOptional.get();

        if (arena.getMaxPlayers() <= arena.getPlayers().size()) {
            MessagesConfiguration.SUBCOMMAND_JOIN_FULL.send(player);
            return;
        }

        if (arena.getGameState() == GameState.IN_GAME || arena.getGameState() == GameState.RESTARTING) {
            MessagesConfiguration.SUBCOMMAND_JOIN_GAME_IN_PROGRESS.send(player);
            return;
        }

        arena.addPlayer(player);
    }

    @Override
    public String getPermission() {
        return SettingsConfiguration.COMMANDS_JOIN_PERMISSION.getString();
    }

    @Override
    public boolean useConsole() {
        return false;
    }
}
