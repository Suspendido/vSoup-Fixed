package kami.gg.souppvp.map;

import kami.gg.souppvp.SoupPvP;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter @Setter
public class MapManager {

    private FileConfiguration mapConfig;
    private File configFile;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private static final String CONFIG_FILE = "map.yml";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public MapManager() {
        loadConfig();
        loadDates();
    }

    private void save() {
        try {
            mapConfig.save(configFile);
        } catch (IOException e) {
            Bukkit.getLogger().severe("[MapHandler] Failed to save map.yml: " + e.getMessage());
        }
    }

    private void loadDates() {
        if (mapConfig.contains("start-date")) {
            String text = mapConfig.getString("start-date");
            if (text != null && !text.isEmpty()) {
                LocalDate date = LocalDate.parse(text, DATE_FORMAT);
                this.startDate = date.atStartOfDay();
            }
        }
        if (mapConfig.contains("end-date")) {
            String text = mapConfig.getString("end-date");
            if (text != null && !text.isEmpty()) {
                LocalDate date = LocalDate.parse(text, DATE_FORMAT);
                this.endDate = date.atStartOfDay();
            }
        }
    }

    private void loadConfig() {
        try {
            configFile = new File(SoupPvP.getInstance().getDataFolder(), CONFIG_FILE);

            if (!configFile.exists()) {
                SoupPvP.getInstance().saveResource(CONFIG_FILE, false);
            }

            mapConfig = YamlConfiguration.loadConfiguration(configFile);

        } catch (Exception e) {
            SoupPvP.getInstance().getLogger().severe("Failed to load map.yml: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void setStartDate() {
        this.startDate = LocalDateTime.now();
        mapConfig.set("start-date", startDate.format(DATE_FORMAT));
        save();
        Bukkit.getLogger().info("[MapHandler] Set start date to: " + startDate.format(DATE_FORMAT));
    }

    public void setDuration(String durationStr) {
        if (startDate == null) {
            Bukkit.getLogger().warning("[MapHandler] Start date not set! Use /map setstart first.");
            return;
        }

        Duration duration = parseDuration(durationStr);
        if (duration != null) {
            this.endDate = startDate.plus(duration);
            mapConfig.set("end-date", endDate.format(DATE_FORMAT));
            save();
            Bukkit.getLogger().info("[MapHandler] Set end date to: " + endDate.format(DATE_FORMAT));
        } else {
            Bukkit.getLogger().warning("[MapHandler] Invalid duration format: " + durationStr);
        }
    }

    public String getTimeLeft() {
        if (startDate == null || endDate == null || LocalDateTime.now().isAfter(endDate)) {
            return "";
        }

        Duration remaining = Duration.between(LocalDateTime.now(), endDate);
        long days = remaining.toDays();
        long hours = remaining.toHours() % 24;
        long minutes = remaining.toMinutes() % 60;
        long seconds = remaining.getSeconds() % 60;

        return String.format("%dd %dh %dm %ds", days, hours, minutes, seconds);
    }

    public String getStartDate() {
        return startDate != null ? startDate.format(DATE_FORMAT) : "Not set";
    }

    public String getEndDate() {
        return endDate != null ? endDate.format(DATE_FORMAT) : "Not set";
    }

    private Duration parseDuration(String input) {
        Pattern pattern = Pattern.compile("(\\d+d)?(\\d+h)?(\\d+m)?(\\d+s)?");
        Matcher matcher = pattern.matcher(input.toLowerCase());

        if (!matcher.matches()) return null;

        Duration duration = Duration.ZERO;
        Matcher m = Pattern.compile("(\\d+)([dhms])").matcher(input.toLowerCase());
        while (m.find()) {
            long value = Long.parseLong(m.group(1));
            switch (m.group(2)) {
                case "d" -> duration = duration.plusDays(value);
                case "h" -> duration = duration.plusHours(value);
                case "m" -> duration = duration.plusMinutes(value);
                case "s" -> duration = duration.plusSeconds(value);
            }
        }

        return duration;
    }

    public void reload() {
        try {
            loadConfig();
            loadDates();

            SoupPvP.instance.getLogger().info("Map configuration reloaded!");
        } catch (Exception e) {
            SoupPvP.instance.getLogger().severe("Error during reload: " + e.getMessage());
            e.printStackTrace();
        }
    }
}