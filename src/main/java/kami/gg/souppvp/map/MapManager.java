package kami.gg.souppvp.map;

import kami.gg.souppvp.SoupPvP;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter @Setter
public class MapManager {
    private FileConfiguration mapConfig;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    static final String CONFIG_FILE = "map.yml";

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public MapManager() {
        if (mapConfig.contains("map.map-start")) {
            String text = mapConfig.getString("start-date");
            LocalDate date = LocalDate.parse(text, DATE_FORMAT);
            this.startDate = date.atStartOfDay();
        }
        if (mapConfig.contains("map.map-end")) {
            String text = mapConfig.getString("end-date");
            LocalDate date = LocalDate.parse(text, DATE_FORMAT);
            this.endDate = date.atStartOfDay();
        }
    }

    private void loadConfig() {
        try {
            File configFile = new File(SoupPvP.getInstance().getDataFolder(), CONFIG_FILE);

            if (!configFile.exists()) {
                SoupPvP.getInstance().saveResource(CONFIG_FILE, false);
            }

            mapConfig = YamlConfiguration.loadConfiguration(configFile);


        } catch (Exception e) {
            SoupPvP.getInstance().getLogger().severe("Failed to load scoreboard configuration: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void setStartDate() {
        this.startDate = LocalDateTime.now();
        mapConfig.set("start-date", startDate.format(DATE_FORMAT));
        mapConfig.saveToString();
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
            mapConfig.saveToString();
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
        if (input.contains("d")) {
            String daysStr = input.split("d")[0].replaceAll("[^0-9]", "");
            duration = duration.plusDays(Long.parseLong(daysStr));
            input = input.replace(daysStr + "d", "");
        }
        if (input.contains("h")) {
            String hoursStr = input.split("h")[0].replaceAll("[^0-9]", "");
            duration = duration.plusHours(Long.parseLong(hoursStr));
            input = input.replace(hoursStr + "h", "");
        }
        if (input.contains("m")) {
            String minutesStr = input.split("m")[0].replaceAll("[^0-9]", "");
            duration = duration.plusMinutes(Long.parseLong(minutesStr));
            input = input.replace(minutesStr + "m", "");
        }
        if (input.contains("s")) {
            String secondsStr = input.split("s")[0].replaceAll("[^0-9]", "");
            duration = duration.plusSeconds(Long.parseLong(secondsStr));
        }

        return duration;
    }

    public void reload() {
        try {
            loadConfig();

            SoupPvP.instance.getLogger().info("Map configuration reloaded!");
        } catch (Exception e) {
            SoupPvP.instance.getLogger().severe("Error during reload: " + e.getMessage());
            e.printStackTrace();
        }
    }
}