package kami.gg.souppvp.changelog;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.changelog.storage.ChangeLogStorage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChangeLogHandler {
    
    private final SoupPvP plugin;
    private final ChangeLogStorage storage;
    private final List<ChangeLog> changeLogs;
    
    public ChangeLogHandler(SoupPvP plugin) {
        this.plugin = plugin;
        this.storage = new ChangeLogStorage(plugin);
        this.changeLogs = new ArrayList<>();
        loadChangeLogs();
    }
    
    public void loadChangeLogs() {
        changeLogs.clear();
        changeLogs.addAll(storage.loadAllChangeLogs());
    }
    
    public void addChangeLog(ChangeLog changeLog) {
        changeLogs.add(changeLog);
        storage.saveChangeLog(changeLog);
        notifyNewChangeLog();
    }
    
    public void updateChangeLog(ChangeLog changeLog) {
        storage.saveChangeLog(changeLog);
        int index = changeLogs.indexOf(changeLog);
        if (index != -1) {
            changeLogs.set(index, changeLog);
        }
        notifyNewChangeLog();
    }
    
    public void deleteChangeLog(String id) {
        ChangeLog changeLog = getChangeLogById(id);
        if (changeLog != null) {
            changeLogs.remove(changeLog);
            storage.deleteChangeLog(id);
        }
    }
    
    public ChangeLog getChangeLogById(String id) {
        return changeLogs.stream()
                .filter(cl -> cl.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
    
    public List<ChangeLog> getChangeLogs() {
        return new ArrayList<>(changeLogs);
    }
    
    public List<ChangeLog> getUnreadChangeLogs(UUID playerUuid) {
        List<String> allIds = new ArrayList<>();
        for (ChangeLog cl : changeLogs) {
            allIds.add(cl.getId());
        }
        
        List<String> unreadIds = storage.getUnreadChangeLogIds(playerUuid, allIds);
        List<ChangeLog> unread = new ArrayList<>();
        
        for (String id : unreadIds) {
            ChangeLog cl = getChangeLogById(id);
            if (cl != null) {
                unread.add(cl);
            }
        }
        
        return unread;
    }
    
    public void markAsRead(UUID playerUuid, String changeLogId) {
        storage.markAsRead(playerUuid, changeLogId);
    }
    
    public boolean hasUnreadChangeLogs(UUID playerUuid) {
        return !getUnreadChangeLogs(playerUuid).isEmpty();
    }
    
    private void notifyNewChangeLog() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (hasUnreadChangeLogs(player.getUniqueId())) {
                player.sendMessage("§e§lNEW CHANGELOG! §7There is a new changelog available. Type §e/changelog§7 to view it.");
            }
        }
    }
}
