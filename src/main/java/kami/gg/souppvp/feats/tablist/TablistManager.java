package kami.gg.souppvp.feats.tablist;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.feats.tablist.task.TablistTask;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.NameThreadFactory;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TablistManager {

    private FileConfiguration tablistConfig;
    private BukkitTask bukkitTask;
    private final Map<Player, String> playerNames = new ConcurrentHashMap<>();
    private final List<String> sortingGroups = new ArrayList<>();

    private static final String TABLIST_FILE = "tablist.yml";
    private static final int UPDATE_INTERVAL = 1;
    private final ScheduledExecutorService executor;

    public TablistManager() {
        executor = Executors.newScheduledThreadPool(1, new NameThreadFactory("SoupPvP - TablistThread"));
        executor.scheduleAtFixedRate(new TablistTask(), 0L, 200L, TimeUnit.MILLISECONDS);

        this.loadTablistConfig();
    }

    private void loadTablistConfig() {
        try {
            File tablistFile = new File(SoupPvP.getInstance().getDataFolder(), TABLIST_FILE);
            if (!tablistFile.exists()) {
                SoupPvP.getInstance().saveResource(TABLIST_FILE, false);
            }

            tablistConfig = YamlConfiguration.loadConfiguration(tablistFile);
            loadSortingGroups();
        } catch (Exception e) {
            Bukkit.getLogger().severe("Failed to load tablist configuration: " + e.getMessage());
        }
    }

    private void loadSortingGroups() {
        sortingGroups.clear();
        List<String> groups = tablistConfig.getStringList("tablist.sorting-types");

        for (String groupLine : groups) {
            if (groupLine.startsWith("GROUPS:")) {
                String groupsString = groupLine.substring(7);
                String[] groupArray = groupsString.split(",");
                Collections.addAll(sortingGroups, groupArray);
            }
        }
    }

    public void startUpdateTask() {
        if (bukkitTask != null) {
            bukkitTask.cancel();
        }

        if (!isEnabled()) {
            return;
        }

        bukkitTask = new BukkitRunnable() {
            @Override
            public void run() {
                updateAllTablists();
            }
        }.runTaskTimer(SoupPvP.getInstance(), 0L, UPDATE_INTERVAL);
    }

    public void stopbukkitTask() {
        if (bukkitTask != null) {
            bukkitTask.cancel();
            bukkitTask = null;
        }
    }

    public boolean isEnabled() {
        return tablistConfig != null && tablistConfig.getBoolean("tablist.enabled", true);
    }

    public void updateAllTablists() {
        if (!isEnabled()) return;
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player != null && player.isOnline()) {
                updatePlayerTablist(player);
            }
        }
    }

    public void updatePlayerTablist(Player player) {
        if (player == null || !player.isOnline() || !isEnabled()) {
            return;
        }

        try {
            updateHeaderFooter(player);
            updatePlayerName(player);
            sortPlayers();

        } catch (Exception e) {
            Bukkit.getLogger().warning("Error updating tablist for " + player.getName() + ": " + e.getMessage());
        }
    }

    private void updateHeaderFooter(Player player) {
        List<String> headerLines = tablistConfig.getStringList("tablist.header");
        List<String> footerLines = tablistConfig.getStringList("tablist.footer");

        StringBuilder header = new StringBuilder();
        StringBuilder footer = new StringBuilder();

        for (String line : headerLines) {
            if (line != null && !line.isEmpty()) {
                String processedLine = processPlaceholders(player, line);
                header.append(processedLine).append("\n");
            } else {
                header.append("\n");
            }
        }

        for (String line : footerLines) {
            if (line != null && !line.isEmpty()) {
                String processedLine = processPlaceholders(player, line);
                footer.append(processedLine).append("\n");
            } else {
                footer.append("\n");
            }
        }

        String headerText = header.toString().trim();
        String footerText = footer.toString().trim();

        this.sendHeaderFooter(player, headerText, footerText);
    }

    private void updatePlayerName(Player player) {
        String customNameFormat = tablistConfig.getString("tablist.tab-name-format", "%player_name%");

        if (customNameFormat != null && !customNameFormat.isEmpty()) {
            String customName = processPlaceholders(player, customNameFormat);

            if (customName.length() > 64) {
                customName = customName.substring(0, 64);
            }

            playerNames.put(player, customName);
            player.setPlayerListName(customName);
        }
    }

    private void sortPlayers() {
        if (sortingGroups.isEmpty()) {
            return;
        }

        List<Player> sortedPlayers = new ArrayList<>(Bukkit.getOnlinePlayers());

        Map<Player, Integer> priorityCache = new HashMap<>();
        sortedPlayers.sort((p1, p2) -> {
            int priority1 = priorityCache.computeIfAbsent(p1, this::getPlayerGroupPriority);
            int priority2 = priorityCache.computeIfAbsent(p2, this::getPlayerGroupPriority);

            if (priority1 != priority2) {
                return Integer.compare(priority1, priority2);
            }

            return p1.getName().compareToIgnoreCase(p2.getName());
        });

        for (Player player : sortedPlayers) {
            String customName = playerNames.get(player);
            if (customName != null) {
                player.setPlayerListName(customName);
            }
        }
    }

    private int getPlayerGroupPriority(Player player) {
        for (int i = 0; i < sortingGroups.size(); i++) {
            String group = sortingGroups.get(i);
            if (player.hasPermission("group." + group) || player.hasPermission("vault.group." + group)) {
                return i;
            }
        }
        return sortingGroups.size(); // Default group has lowest priority
    }

    private String processPlaceholders(Player player, String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        boolean shouldIncludeName = text.contains("%player_name%");

        String playerName = player.getName();
        int online = Bukkit.getOnlinePlayers().size();
        int max = Bukkit.getMaxPlayers();

        if (shouldIncludeName) {
            text = text.replace("%player_name%", playerName);
        }

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            text = PlaceholderAPI.setPlaceholders(player, text);
        }

        text = text.replace("%online%", String.valueOf(online));
        text = text.replace("%max_online%", String.valueOf(max));
        text = CC.translate(text);

        return text;
    }

    public void reload() {
        try {
            stopbukkitTask();
            loadTablistConfig();
            startUpdateTask();
            updateAllTablists();

            SoupPvP.getInstance().getLogger().info("Tablist configuration reloaded!");
        } catch (Exception e) {
            SoupPvP.getInstance().getLogger().severe("Error during reload: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void cleanup() {
        stopbukkitTask();
        playerNames.clear();
    }

    private void sendHeaderFooter(Player player, String header, String footer) {
        try {
            PacketContainer packet = ProtocolLibrary.getProtocolManager()
                    .createPacket(PacketType.Play.Server.PLAYER_LIST_HEADER_FOOTER);

            packet.getChatComponents()
                    .write(0, WrappedChatComponent.fromText(header))
                    .write(1, WrappedChatComponent.fromText(footer));

            ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}