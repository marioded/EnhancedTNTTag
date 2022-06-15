package tech.zmario.enhancedtnttag.hooks;

import lombok.RequiredArgsConstructor;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tech.zmario.enhancedtnttag.EnhancedTNTTag;

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
            return null;
        }

        if (identifier.equals("wins")) {
            return String.valueOf(0);
        }

        return null;
    }
}
