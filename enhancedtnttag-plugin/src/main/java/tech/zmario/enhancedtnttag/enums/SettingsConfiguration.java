package tech.zmario.enhancedtnttag.enums;

import tech.zmario.enhancedtnttag.EnhancedTNTTag;

import java.util.List;

public enum SettingsConfiguration {

    TAGGED_PLAYERS_PERCENTAGE("tagged-players-percentage"),
    DEATH_MATCH_PLAYERS_SIZE("death-match-players-size"),
    ADMIN_PERMISSION("admin-permission"),

    EXPLOSION_DELAY_BASE("explosion-delay.base"),
    EXPLOSION_DELAY_DECREMENT("explosion-delay.decrement"),
    EXPLOSION_RADIUS("explosion-radius"),

    TAG_FIREWORK_ENABLED("tag-firework.enabled"),
    TAG_FIREWORK_TYPE("tag-firework.type"),
    TAG_FIREWORK_POWER("tag-firework.power"),

    SPEED_UNTAGGED_ENABLED("speed.untagged.enabled"),
    SPEED_UNTAGGED_LEVEL("speed.untagged.level"),
    SPEED_TAGGED_ENABLED("speed.tagged.enabled"),
    SPEED_TAGGED_LEVEL("speed.tagged.level"),

    COUNTDOWNS_GAME_START("countdowns.game-start"),
    COUNTDOWNS_GAME_RESTART("countdowns.game-restart"),
    COUNTDOWNS_ROUND_CHANGE("countdowns.round-change"),

    TAB_LIST_FORMAT_UNTAGGED("tab-list.format-untagged"),
    TAB_LIST_FORMAT_TAGGED("tab-list.format-tagged"),
    TAB_LIST_FORMAT_SPECTATOR("tab-list.format-spectator"),
    TAB_LIST_FORMAT_WAITING("tab-list.format-waiting"),
    TAB_LIST_FORMAT_LOBBY("tab-list.format-lobby"),

    CHAT_FORMAT_ENABLED("chat-format-enabled"),

    COMMANDS_MAIN_NAME("commands.main.name"),
    COMMANDS_MAIN_ALIASES("commands.main.aliases"),
    COMMANDS_MAIN_PERMISSION("commands.main.permission"),

    COMMANDS_RELOAD_NAME("commands.reload.name"),
    COMMANDS_RELOAD_PERMISSION("commands.reload.permission"),

    COMMANDS_BUILD_NAME("commands.build.name"),
    COMMANDS_BUILD_PERMISSION("commands.build.permission"),

    COMMANDS_JOIN_NAME("commands.join.name"),
    COMMANDS_JOIN_PERMISSION("commands.join.permission"),

    COMMANDS_LEAVE_NAME("commands.leave.name"),
    COMMANDS_LEAVE_PERMISSION("commands.leave.permission"),

    COMMANDS_SETUP_NAME("commands.setup.name"),
    COMMANDS_SETUP_PERMISSION("commands.setup.permission"),

    COMMANDS_SET_MAX_PLAYERS_NAME("commands.set-max-players.name"),
    COMMANDS_SET_MAX_PLAYERS_PERMISSION("commands.set-max-players.permission"),

    COMMANDS_SET_MIN_PLAYERS_NAME("commands.set-min-players.name"),
    COMMANDS_SET_MIN_PLAYERS_PERMISSION("commands.set-min-players.permission"),

    COMMANDS_SET_LOBBY_NAME("commands.set-lobby.name"),
    COMMANDS_SET_LOBBY_PERMISSION("commands.set-lobby.permission"),

    COMMANDS_SET_SPAWN_NAME("commands.set-spawn.name"),
    COMMANDS_SET_SPAWN_PERMISSION("commands.set-spawn.permission"),

    COMMANDS_SET_SPECTATOR_SPAWN_NAME("commands.set-spectator-spawn.name"),
    COMMANDS_SET_SPECTATOR_SPAWN_PERMISSION("commands.set-spectator-spawn.permission"),

    COMMANDS_SAVE_NAME("commands.save.name"),
    COMMANDS_SAVE_PERMISSION("commands.save.permission"),

    COMMANDS_SET_MAIN_LOBBY_NAME("commands.set-main-lobby.name"),
    COMMANDS_SET_MAIN_LOBBY_PERMISSION("commands.set-main-lobby.permission"),

    COMMANDS_UNLOAD_NAME("commands.unload.name"),
    COMMANDS_UNLOAD_PERMISSION("commands.unload.permission"),
    
    MAIN_LOBBY_LOCATION("main-lobby-location"),
    ;


    private final String path;
    private final EnhancedTNTTag instance = EnhancedTNTTag.getInstance();

    SettingsConfiguration(String path) {
        this.path = path;
    }

    public String getString() {
        return instance.getConfig().getString(path);
    }

    public int getInt() {
        return instance.getConfig().getInt(path);
    }

    public double getDouble() {
        return instance.getConfig().getDouble(path);
    }

    public boolean getBoolean() {
        return instance.getConfig().getBoolean(path);
    }

    public List<String> getStringList() {
        return instance.getConfig().getStringList(path);
    }
}
