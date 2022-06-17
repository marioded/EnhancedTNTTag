package tech.zmario.enhancedtnttag.hooks;

import lombok.RequiredArgsConstructor;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tech.zmario.enhancedtnttag.EnhancedTNTTag;
import tech.zmario.enhancedtnttag.api.objects.IArena;

import java.util.Optional;

@RequiredArgsConstructor
public class PlaceholderAPIHook extends PlaceholderExpansion {

    private final EnhancedTNTTag plugin;

    @Override
    public @NotNull String getIdentifier() {
        return "tnttag";
    }

    @Override
    public @NotNull String getAuthor() {
        return "zMario";
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        String identifier = params.toLowerCase();

        if (identifier.startsWith("arena_")) {
            Optional<IArena> arenaOptional = plugin.getArenaManager().getArena(identifier.split("arena_")[0]);

            if (!arenaOptional.isPresent()) {
                return "Arena not found";
            }

            IArena arena = arenaOptional.get();
            identifier = identifier.replace("arena_" + arena.getArenaName() + "_", "");

            switch (identifier) {
                case "players":
                    return String.valueOf(arena.getPlayers().size());
                case "taggers":
                    return String.valueOf(arena.getTaggers().size());
                case "min_players":
                    return String.valueOf(arena.getMinPlayers());
                case "max_players":
                    return String.valueOf(arena.getMaxPlayers());
                case "state":
                    return arena.getGameState().toString();
                case "explosion_time":
                    return String.valueOf(arena.getExplosionTime());
                case "round":
                    return String.valueOf(arena.getRound());
            }
        }

        if (identifier.equals("wins")) {
            return String.valueOf(plugin.getLocalStorage().getGamePlayers().get(player.getUniqueId()).getWins());
        }

        return "Not found";
    }
}
