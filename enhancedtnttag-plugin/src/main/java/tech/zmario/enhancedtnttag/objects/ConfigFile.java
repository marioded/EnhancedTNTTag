package tech.zmario.enhancedtnttag.objects;

import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Getter
public class ConfigFile {
    private final Plugin plugin;
    private final String path;

    private final File file;

    private YamlConfiguration config;

    public ConfigFile(Plugin plugin, File folder, String path) {

        this.plugin = plugin;
        this.path = path;

        if (!folder.exists()) {
            folder.mkdirs();
        }

        file = new File(folder, path);

        try {
            config = create();
        } catch (IOException e) {
            e.printStackTrace();
            plugin.getLogger().severe("Failed to create configuration file: " + path);
        }
    }

    private YamlConfiguration create() throws IOException {
        if (!file.exists()) {
            Files.copy(plugin.getResource(path), file.toPath());
        }

        return YamlConfiguration.loadConfiguration(file);
    }

    public void reload() {
        config = YamlConfiguration.loadConfiguration(file);
    }
}