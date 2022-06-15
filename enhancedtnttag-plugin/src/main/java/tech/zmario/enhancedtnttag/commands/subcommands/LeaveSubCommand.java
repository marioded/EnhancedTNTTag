package tech.zmario.enhancedtnttag.commands.subcommands;

import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tech.zmario.enhancedtnttag.EnhancedTNTTag;
import tech.zmario.enhancedtnttag.api.commands.interfaces.SubCommand;
import tech.zmario.enhancedtnttag.api.enums.GameState;
import tech.zmario.enhancedtnttag.api.objects.IArena;
import tech.zmario.enhancedtnttag.api.objects.Placeholder;
import tech.zmario.enhancedtnttag.enums.MessagesConfiguration;
import tech.zmario.enhancedtnttag.enums.SettingsConfiguration;

import java.util.Optional;

@RequiredArgsConstructor
public class LeaveSubCommand implements SubCommand {

    private final EnhancedTNTTag plugin;

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        Optional<IArena> arenaOptional = plugin.getArenaManager().getArena(player);

        if (!arenaOptional.isPresent()) {
            MessagesConfiguration.SUBCOMMAND_LEAVE_NOT_IN_GAME.send(player);
            return;
        }

        IArena arena = arenaOptional.get();

        arena.getPlayers().remove(player.getUniqueId());
        arena.getTaggers().remove(player.getUniqueId());
        arena.getSpectators().remove(player.getUniqueId());

        arena.removePlayer(player);

        if (arena.getGameState() == GameState.WAITING || arena.getGameState() == GameState.STARTING) {
            arena.sendMessage(MessagesConfiguration.PLAYER_LEFT.getString(player, new Placeholder("%player%", player.getName()),
                    new Placeholder("%players%", String.valueOf(arena.getPlayers().size())),
                    new Placeholder("%max-players%", String.valueOf(arena.getMaxPlayers()))));
        }

        MessagesConfiguration.SUBCOMMAND_LEAVE_SUCCESS.send(sender, new Placeholder("%arena%", arena.getArenaName()));

    }

    @Override
    public String getPermission() {
        return SettingsConfiguration.COMMANDS_LEAVE_PERMISSION.getString();
    }

    @Override
    public boolean useConsole() {
        return false;
    }
}
