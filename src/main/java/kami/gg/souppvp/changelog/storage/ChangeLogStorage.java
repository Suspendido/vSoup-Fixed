package kami.gg.souppvp.changelog.storage;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.changelog.ChangeLog;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChangeLogStorage {
    
    private final SoupPvP plugin;
    private final File changelogsFolder;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    
    public ChangeLogStorage(SoupPvP plugin) {
        this.plugin = plugin;
        this.changelogsFolder = new File(plugin.getDataFolder(), "changelogs");
        if (!changelogsFolder.exists()) {
            changelogsFolder.mkdirs();
        }
    }
    
    public void saveChangeLog(ChangeLog changeLog) {
        File file = new File(changelogsFolder, changeLog.getId() + ".yml");
        YamlConfiguration config = new YamlConfiguration();
        
        config.set("id", changeLog.getId());
        config.set("title", changeLog.getTitle());
        config.set("content", changeLog.getContent());
        config.set("author", changeLog.getAuthor());
        config.set("createdAt", changeLog.getCreatedAt().format(formatter));
        config.set("updatedAt", changeLog.getUpdatedAt().format(formatter));
        
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public ChangeLog loadChangeLog(String id) {
        File file = new File(changelogsFolder, id + ".yml");
        if (!file.exists()) {
            return null;
        }
        
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        
        String title = config.getString("title");
        List<String> content = config.getStringList("content");
        String author = config.getString("author");
        LocalDateTime createdAt = LocalDateTime.parse(config.getString("createdAt"), formatter);
        LocalDateTime updatedAt = LocalDateTime.parse(config.getString("updatedAt"), formatter);
        
        ChangeLog changeLog = new ChangeLog(id, title, content, author);
        changeLog.setCreatedAt(createdAt);
        changeLog.setUpdatedAt(updatedAt);
        
        return changeLog;
    }
    
    public List<ChangeLog> loadAllChangeLogs() {
        List<ChangeLog> changeLogs = new ArrayList<>();
        
        File[] files = changelogsFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null) {
            return changeLogs;
        }
        
        for (File file : files) {
            String id = file.getName().replace(".yml", "");
            ChangeLog changeLog = loadChangeLog(id);
            if (changeLog != null) {
                changeLogs.add(changeLog);
            }
        }
        
        return changeLogs;
    }
    
    public void deleteChangeLog(String id) {
        File file = new File(changelogsFolder, id + ".yml");
        if (file.exists()) {
            file.delete();
        }
    }
    
    public void markAsRead(UUID playerUuid, String changeLogId) {
        File playerFile = new File(plugin.getDataFolder(), "changelog_reads/" + playerUuid.toString() + ".yml");
        if (!playerFile.getParentFile().exists()) {
            playerFile.getParentFile().mkdirs();
        }
        
        YamlConfiguration config;
        if (playerFile.exists()) {
            config = YamlConfiguration.loadConfiguration(playerFile);
        } else {
            config = new YamlConfiguration();
        }
        
        config.set("read." + changeLogId, System.currentTimeMillis());
        
        try {
            config.save(playerFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public boolean hasRead(UUID playerUuid, String changeLogId) {
        File playerFile = new File(plugin.getDataFolder(), "changelog_reads/" + playerUuid.toString() + ".yml");
        if (!playerFile.exists()) {
            return false;
        }
        
        YamlConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
        return config.contains("read." + changeLogId);
    }
    
    public List<String> getUnreadChangeLogIds(UUID playerUuid, List<String> allChangeLogIds) {
        List<String> unread = new ArrayList<>();
        
        for (String id : allChangeLogIds) {
            if (!hasRead(playerUuid, id)) {
                unread.add(id);
            }
        }
        
        return unread;
    }
}
