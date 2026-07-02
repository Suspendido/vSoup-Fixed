package kami.gg.souppvp.lang;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.util.CC;
import lombok.Getter;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public class LangManager {

    private final SoupPvP plugin;
    private File langFile;
    private FileConfiguration langConfig;
    private final Map<String, String> messages;

    public LangManager(SoupPvP plugin) {
        this.plugin = plugin;
        this.messages = new HashMap<>();
    }

    public void load() {
        langFile = new File(plugin.getDataFolder(), "lang.yml");
        
        if (!langFile.exists()) {
            plugin.saveResource("lang.yml", false);
        }

        langConfig = YamlConfiguration.loadConfiguration(langFile);
        loadMessages();
    }

    public void reload() {
        langConfig = YamlConfiguration.loadConfiguration(langFile);
        messages.clear();
        loadMessages();
    }

    private void loadMessages() {
        for (String key : langConfig.getKeys(true)) {
            if (langConfig.isString(key)) {
                messages.put(key, langConfig.getString(key));
            }
        }
    }

    public String getString(String path) {
        String message = messages.get(path);
        if (message == null) {
            return "&cMessage not found: " + path;
        }
        if (message.isEmpty()) {
            return null;
        }

        return CC.t(message);
    }

    public String getString(String path, Player player) {
        String message = getString(path);
        if (message == null) {
            return null;
        }

        if (plugin.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            message = PlaceholderAPI.setPlaceholders(player, message);
        }

        return message;
    }

    public List<String> getString(List<String> path) {
        return path.stream().map(this::getString).collect(Collectors.toList());
    }

    public int getInt(String path) {
        return langConfig.getInt(path, 0);
    }

    public int getInt(String path, int defaultValue) {
        return langConfig.getInt(path, defaultValue);
    }

    public double getDouble(String path) {
        return langConfig.getDouble(path, 0.0);
    }

    public double getDouble(String path, double defaultValue) {
        return langConfig.getDouble(path, defaultValue);
    }

    public boolean getBoolean(String path) {
        return langConfig.getBoolean(path, false);
    }

    public boolean getBoolean(String path, boolean defaultValue) {
        return langConfig.getBoolean(path, defaultValue);
    }

    public List<String> getStringList(String path) {
        return langConfig.getStringList(path).stream().map(CC::t).collect(Collectors.toList());
    }

    public List<String> getStringList(String path, Player player) {
        if (plugin.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            return langConfig.getStringList(path).stream()
                    .map(CC::t)
                    .map(line -> PlaceholderAPI.setPlaceholders(player, line))
                    .collect(Collectors.toList());
        }
        return getStringList(path);
    }

    public String getString(String path, Map<String, String> placeholders) {
        String message = getString(path);
        if (message == null) {
            return null;
        }
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            message = message.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return message;
    }

    public String getString(String path, Player player, Map<String, String> placeholders) {
        String message = getString(path, player);
        if (message == null) {
            return null;
        }
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            message = message.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return message;
    }

    public String getString(String path, String... placeholders) {
        String message = getString(path);
        if (message == null) {
            return null;
        }
        for (int i = 0; i < placeholders.length; i += 2) {
            if (i + 1 < placeholders.length) {
                message = message.replace("{" + placeholders[i] + "}", placeholders[i + 1]);
            }
        }
        return message;
    }

    public String getString(String path, Player player, String... placeholders) {
        String message = getString(path, player);
        if (message == null) {
            return null;
        }
        for (int i = 0; i < placeholders.length; i += 2) {
            if (i + 1 < placeholders.length) {
                message = message.replace("{" + placeholders[i] + "}", placeholders[i + 1]);
            }
        }
        return message;
    }

    public void save() {
        try {
            langConfig.save(langFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
