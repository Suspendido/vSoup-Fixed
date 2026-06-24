package kami.gg.souppvp.lang;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.util.CC;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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

    public String getMessage(String path) {
        String message = messages.get(path);
        if (message == null) {
            return "&cMessage not found: " + path;
        }
        return CC.t(message);
    }

    public String getMessage(String path, Map<String, String> placeholders) {
        String message = getMessage(path);
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            message = message.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return message;
    }

    public String getMessage(String path, String... placeholders) {
        String message = getMessage(path);
        for (int i = 0; i < placeholders.length; i += 2) {
            if (i + 1 < placeholders.length) {
                message = message.replace("{" + placeholders[i] + "}", placeholders[i + 1]);
            }
        }
        return message;
    }

    public String getLangMessage(String langConstant) {
        return getMessage(langConstant);
    }

    public String getLangMessage(String langConstant, Map<String, String> placeholders) {
        return getMessage(langConstant, placeholders);
    }

    public String getLangMessage(String langConstant, String... placeholders) {
        return getMessage(langConstant, placeholders);
    }

    public void save() {
        try {
            langConfig.save(langFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
