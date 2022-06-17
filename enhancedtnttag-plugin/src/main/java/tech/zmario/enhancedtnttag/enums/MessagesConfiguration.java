package tech.zmario.enhancedtnttag.enums;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tech.zmario.enhancedtnttag.EnhancedTNTTag;
import tech.zmario.enhancedtnttag.objects.Placeholder;
import tech.zmario.enhancedtnttag.utils.Utils;

import java.util.List;
import java.util.stream.Collectors;

public enum MessagesConfiguration {

    NO_PERMISSION("commands.no-permission"),
    NO_CONSOLE("commands.no-console"),
    MAIN_LOBBY_NOT_SET("main-lobby-not-set"),
    COMMANDS_USER_HELP("commands.user-help"),
    COMMANDS_ADMIN_HELP("commands.admin-help"),

    SUBCOMMAND_RELOAD_SUCCESS("commands.subcommands.reload.success"),

    SUBCOMMAND_JOIN_NO_ARENA_FOUND("commands.subcommands.join.no-arena-found"),
    SUBCOMMAND_JOIN_NOT_EXIST("commands.subcommands.join.not-exist"),
    SUBCOMMAND_JOIN_ALREADY_IN_GAME("commands.subcommands.join.already-in-game"),
    SUBCOMMAND_JOIN_FULL("commands.subcommands.join.full"),
    SUBCOMMAND_JOIN_GAME_IN_PROGRESS("commands.subcommands.join.game-in-progress"),

    SUBCOMMAND_LEAVE_NOT_IN_GAME("commands.subcommands.leave.not-in-game"),
    SUBCOMMAND_LEAVE_SUCCESS("commands.subcommands.leave.success"),

    SUBCOMMAND_SETUP_USAGE("commands.subcommands.setup.usage"),
    SUBCOMMAND_SETUP_ALREADY_EXISTS("commands.subcommands.setup.already-exists"),
    SUBCOMMAND_SETUP_ALREADY_SETTING("commands.subcommands.setup.already-setting"),
    SUBCOMMAND_SETUP_START("commands.subcommands.setup.start"),

    SUBCOMMAND_SETUP_NOT_SETTING("commands.subcommands.setup.not-setting"),

    SUBCOMMAND_SAVE_SUCCESS("commands.subcommands.setup.save.success"),

    SUBCOMMAND_SET_MAX_PLAYERS_USAGE("commands.subcommands.setup.set-max-players.usage"),
    SUBCOMMAND_SET_MAX_PLAYERS_NOT_A_NUMBER("commands.subcommands.setup.set-max-players.not-a-number"),
    SUBCOMMAND_SET_MAX_PLAYERS_SUCCESS("commands.subcommands.setup.set-max-players.success"),

    SUBCOMMAND_SET_MIN_PLAYERS_USAGE("commands.subcommands.setup.set-min-players.usage"),
    SUBCOMMAND_SET_MIN_PLAYERS_NOT_A_NUMBER("commands.subcommands.setup.set-min-players.not-a-number"),
    SUBCOMMAND_SET_MIN_PLAYERS_SUCCESS("commands.subcommands.setup.set-min-players.success"),

    SUBCOMMAND_SET_LOBBY_SUCCESS("commands.subcommands.setup.set-lobby.success"),
    SUBCOMMAND_SET_SPAWN_SUCCESS("commands.subcommands.setup.set-spawn.success"),
    SUBCOMMAND_SET_SPECTATOR_SPAWN_SUCCESS("commands.subcommands.setup.set-spectator-spawn.success"),

    SUBCOMMAND_SET_MAIN_LOBBY_SUCCESS("commands.subcommands.set-main-lobby.success"),

    SUBCOMMAND_LOAD_USAGE("commands.subcommands.load.usage"),
    SUBCOMMAND_LOAD_ALREADY_LOADED("commands.subcommands.load.already-loaded"),
    SUBCOMMAND_LOAD_SUCCESS("commands.subcommands.load.success"),

    SUBCOMMAND_UNLOAD_USAGE("commands.subcommands.unload.usage"),
    SUBCOMMAND_UNLOAD_NOT_EXIST("commands.subcommands.unload.not-exist"),
    SUBCOMMAND_UNLOAD_SUCCESS("commands.subcommands.unload.success"),

    SUBCOMMAND_BUILD_TOGGLED_ON("commands.subcommands.build.toggled-on"),
    SUBCOMMAND_BUILD_TOGGLED_OFF("commands.subcommands.build.toggled-off"),

    ARENA_JOIN_MOTD("arena-join-motd"),
    PLAYER_JOINED("player-joined"),
    PLAYER_LEFT("player-left"),
    NOT_ENOUGH_PLAYERS("not-enough-players"),
    GAME_STARTING("game-starting"),

    PLAYER_TAGGED("player-tagged"),
    TARGET_TAGGED("target-tagged"),
    TAGGED_BROADCAST("tagged-broadcast"),
    PLAYER_BLOWN_UP("player-blown-up"),

    ROUND_STARTED("round-started"),
    ROUND_STARTED_TAGGED("round-started-tagger"),
    ROUND_STARTED_SPECTATOR("round-started-spectator"),

    DEATH_MATCH_STARTED("death-match-started"),
    DEATH_MATCH_STARTED_TAGGED("death-match-started-tagger"),
    DEATH_MATCH_STARTED_SPECTATOR("death-match-started-spectator"),

    GAME_ENDED("game-ended"),

    TAB_LIST_UNTAGGED("tab-list.untagged"),
    TAB_LIST_TAGGED("tab-list.tagged"),
    TAB_LIST_SPECTATOR("tab-list.spectator"),
    TAB_LIST_WAITING("tab-list.waiting"),
    TAB_LIST_LOBBY("tab-list.lobby"),

    TITLES_GAME_STARTING("titles.game-starting"),
    TITLES_GAME_STARTED("titles.game-started"),
    TITLES_GAME_STARTED_TAGGED("titles.game-started-tagged"),
    TITLES_NEW_ROUND("titles.new-round"),
    TITLES_NEW_ROUND_TAGGED("titles.new-round-tagged"),
    TITLES_NEW_ROUND_SPECTATOR("titles.new-round-spectator"),
    TITLES_DEATH_MATCH_STARTED("titles.death-match-started"),
    TITLES_DEATH_MATCH_STARTED_TAGGED("titles.death-match-started-tagged"),
    TITLES_DEATH_MATCH_STARTED_SPECTATOR("titles.death-match-started-spectator"),

    SOUNDS_GAME_STARTING("sounds.game-starting"),
    SOUNDS_GAME_STARTED("sounds.game-started"),
    SOUNDS_GAME_STARTED_TAGGED("sounds.game-started-tagged"),
    SOUNDS_NEW_ROUND("sounds.new-round"),
    SOUNDS_NEW_ROUND_TAGGED("sounds.new-round-tagged"),
    SOUNDS_NEW_ROUND_SPECTATOR("sounds.new-round-spectator"),
    SOUNDS_DEATH_MATCH_STARTED("sounds.death-match-started"),
    SOUNDS_DEATH_MATCH_STARTED_TAGGED("sounds.death-match-started-tagged"),
    SOUNDS_DEATH_MATCH_STARTED_SPECTATOR("sounds.death-match-started-spectator"),

    CHAT_FORMAT_LOBBY("chat-format.lobby"),
    CHAT_FORMAT_PLAYER("chat-format.player"),
    CHAT_FORMAT_TAGGED("chat-format.tagged"),
    CHAT_FORMAT_SPECTATOR("chat-format.spectator"),

    SCOREBOARD_TITLE("scoreboard.title"),

    SCOREBOARD_LOBBY("scoreboard.lobby"),
    SCOREBOARD_WAITING("scoreboard.waiting"),
    SCOREBOARD_STARTING("scoreboard.starting"),
    SCOREBOARD_PLAYING_UNTAGGED("scoreboard.playing.untagged"),
    SCOREBOARD_PLAYING_TAGGED("scoreboard.playing.tagged"),
    SCOREBOARD_PLAYING_SPECTATOR("scoreboard.playing.spectator"),
    SCOREBOARD_RESTARTING("scoreboard.restarting"),

    PLACEHOLDERS_SECOND("placeholders.second"),
    PLACEHOLDERS_SECONDS("placeholders.seconds"),
    PLACEHOLDERS_NONE("placeholders.none"),
    PLACEHOLDERS_PLAYER_LIST_COMMA("placeholders.players-list.comma"),
    PLACEHOLDERS_PLAYER_LIST_DOT("placeholders.players-list.dot"),
    PLACEHOLDERS_PLAYER_LIST_PLAYER("placeholders.players-list.player"),
    ;

    private final String path;
    private final EnhancedTNTTag instance = EnhancedTNTTag.getInstance();

    MessagesConfiguration(String path) {
        this.path = path;
    }

    public String getString(OfflinePlayer player, Placeholder... placeholders) {
        String message = Utils.colorize(instance.getMessages().getString(path));

        for (Placeholder placeholder : placeholders) {
            message = message.replace(placeholder.getInput(), placeholder.getOutput());
        }

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI") && player != null) {
            return PlaceholderAPI.setPlaceholders(player, message);
        }

        return message;
    }

    public List<String> getStringList(OfflinePlayer player, Placeholder... placeholders) {

        return instance.getMessages().getStringList(path).stream().map(Utils::colorize).map(message -> {

            for (Placeholder placeholder : placeholders) {
                message = message.replace(placeholder.getInput(), placeholder.getOutput());
            }

            if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI") && player != null) {
                return PlaceholderAPI.setPlaceholders(player, message);
            }

            return message;
        }).collect(Collectors.toList());
    }

    public void send(CommandSender sender, Placeholder... placeholders) {
        String message = getString(sender instanceof Player ? (Player) sender : null, placeholders);

        sender.sendMessage(message);
    }

    public void sendList(CommandSender sender, Placeholder... placeholders) {
        for (String message : getStringList(sender instanceof Player ? (Player) sender : null, placeholders)) {

            sender.sendMessage(message);
        }
    }

}
