package kami.gg.souppvp.feats.scoreboard;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.assemble.Assemble;
import lombok.Getter;
import lombok.Setter;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.util.*;

@Getter @Setter
public class ScoreboardManager {
    private final SoupPvP instance;
    private FileConfiguration scoreboardConfig;
    private BukkitTask animationTask;
    private BukkitTask assembleTask;

    private List<String> titleFrames;
    private int currentTitleFrame = 0;
    private int titleInterval;

    private static final String CONFIG_FILE = "scoreboard.yml";

    public ScoreboardManager(SoupPvP plugin) {
        this.instance = plugin;
        loadConfig();
        startAnimationTask();
    }

    private void loadConfig() {
        try {
            File configFile = new File(instance.getDataFolder(), CONFIG_FILE);

            if (!configFile.exists()) {
                instance.saveResource(CONFIG_FILE, false);
            }

            scoreboardConfig = YamlConfiguration.loadConfiguration(configFile);

            titleFrames = scoreboardConfig.getStringList("TITLE.FRAMES");
            titleInterval = scoreboardConfig.getInt("TITLE.INTERVAL", 20);

            if (titleFrames.isEmpty()) {
                titleFrames.add(scoreboardConfig.getString("SCOREBOARD_INFO.STATIC_TITLE", "&b&lSoupPvP"));
            }

        } catch (Exception e) {
            instance.getLogger().severe("Failed to load scoreboard configuration: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void startAnimationTask() {
        if (animationTask != null) {
            animationTask.cancel();
        }

        animationTask = new BukkitRunnable() {
            @Override
            public void run() {
                currentTitleFrame++;
                if (currentTitleFrame >= titleFrames.size()) {
                    currentTitleFrame = 0;
                }
            }
        }.runTaskTimer(instance, 0L, titleInterval);
    }

    private void reloadLines() {
        Assemble old = instance.getAssemble();

        if (old != null) {
            old.cleanup();
        }

        Assemble assemble = new Assemble(instance, new ScoreboardAdapter());
        instance.setAssemble(assemble);
    }


    public String getAnimatedTitle(Player player) {
        if (titleFrames.isEmpty()) {
            return this.getScoreboardConfig().getString("SCOREBOARD_INFO.STATIC_TITLE");
        }

        if (currentTitleFrame >= titleFrames.size()) {
            currentTitleFrame = 0;
        }

        String frame = titleFrames.get(currentTitleFrame);
        return processPlaceholders(player, frame);
    }

    private String processPlaceholders(Player player, String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            text = PlaceholderAPI.setPlaceholders(player, text);
        }

        text = text.replace("%player%", player.getName());
        text = text.replace("%online%", String.valueOf(Bukkit.getOnlinePlayers().size()));

        text = CC.t(text);

        return text;
    }

    public void reload() {
        try {
            if (animationTask != null) {
                animationTask.cancel();
            }

            loadConfig();
            startAnimationTask();
            reloadLines();

            instance.getLogger().info("Scoreboard configuration reloaded!");
        } catch (Exception e) {
            instance.getLogger().severe("Error during reload: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void cleanup() {
        if (animationTask != null) {
            animationTask.cancel();
        }
    }
}