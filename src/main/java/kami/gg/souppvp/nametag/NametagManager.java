package kami.gg.souppvp.nametag;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.nametag.adapter.NametagAdapter;
import kami.gg.souppvp.nametag.adapter.NametagColor;
import kami.gg.souppvp.nametag.task.NametagTask;
import kami.gg.souppvp.perk.Perk;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.NameThreadFactory;
import lombok.Getter;
import me.activated.core.plugin.AquaCoreAPI;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Getter
public class NametagManager {

    private final Map<UUID, Nametag> nametags;
    private final NametagAdapter adapter;
    private final Map<UUID, Long> frozenStartTimes = new ConcurrentHashMap<>();
    private final ScheduledExecutorService executor;
    private static final String CONFIG_FILE = "nametags.yml";
    private static final Random RNG = new Random();
    private FileConfiguration nametagConfig;

    public NametagManager() {
        this.nametags = new ConcurrentHashMap<>();
        this.adapter = new NametagColor();
        this.executor = Executors.newScheduledThreadPool(1, new NameThreadFactory("SoupPvP - NametagThread"));
        this.executor.scheduleAtFixedRate(new NametagTask(), 0L, 500L, TimeUnit.MILLISECONDS);

        this.loadConfig();
        new NametagListener();
    }

    private void loadConfig() {
        try {
            File configFile = new File(SoupPvP.getInstance().getDataFolder(), CONFIG_FILE);
            if (!configFile.exists()) {
                SoupPvP.getInstance().saveResource(CONFIG_FILE, false);
            }
            nametagConfig = YamlConfiguration.loadConfiguration(configFile);
        } catch (Exception e) {
            SoupPvP.getInstance().getLogger().severe("Failed to load nametag configuration: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void disable() {
        executor.shutdownNow();
    }

    public void handleUpdate(Player viewer, Player target) {
        if (viewer == null || target == null) return;
        String prefix = getAdapter().getAndUpdate(viewer, target);
        updateLunarTags(viewer, target, prefix);
    }

    public void updateLunarTags(Player viewer, Player target, String prefix) {
        if (SoupPvP.getInstance().getClientHook().getClients().isEmpty()) return;
        if (!nametags.containsKey(viewer.getUniqueId())) return;

        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(target.getUniqueId());
        boolean staff = AquaCoreAPI.INSTANCE.getPlayerData(target.getUniqueId()).isInStaffMode();
        List<String> lines = new ArrayList<>();

        if (profile == null) {
            for (String s : nametagConfig.getStringList("NAMETAGS.FORMAT.NORMAL")) {
                lines.add(s
                        .replace("%prefix%", prefix != null ? prefix : "")
                        .replace("%player%", target.getName())
                        .replace("%health%", String.valueOf((int) target.getHealth() / 2))
                );
            }
            handleLunar(target, viewer, CC.translate(lines));
            return;
        }

        if (staff) {
            for (String s : nametagConfig.getStringList("NAMETAGS.FORMAT.STAFF")) {
                lines.add(s
                        .replace("%player%", target.getName())
                        .replace("%health%", String.valueOf((int) target.getHealth() / 2))
                        .replace("%rank%", SoupPvP.getInstance().getRankHook().getRankColor(target) + SoupPvP.getInstance().getRankHook().getRankName(target))
                        .replace("%rank-prefix%", SoupPvP.getInstance().getRankHook().getRankPrefix(target))
                );
            }
            handleLunar(target, viewer, CC.translate(lines));
            return;
        }

        boolean isTrickster = false;
        try {
            if (!profile.getActivePerks().isEmpty()) {
                Perk currentPerk = SoupPvP.getInstance().getPerksHandler().getPerkByName(profile.getActivePerks().getFirst());
                Perk tricksterPerk = SoupPvP.getInstance().getPerksHandler().getPerkByName("Trickster");
                isTrickster = (currentPerk != null && currentPerk.equals(tricksterPerk));
            }
        } catch (Exception ignored) {}

        String formatKey;
        if (profile.getBounty() > 0) {
            formatKey = "NAMETAGS.FORMAT.BOUNTY";
            int bountyValue = isTrickster ? RNG.nextInt(1001) : profile.getBounty();

            for (String s : nametagConfig.getStringList(formatKey)) {
                lines.add(s
                        .replace("%prefix%", prefix != null ? prefix : "")
                        .replace("%player%", target.getName())
                        .replace("%health%", String.valueOf(isTrickster ? new Random().nextInt(11) : (int) target.getHealth() / 2))
                        .replace("%bounty%", String.valueOf(bountyValue))
                );
            }
        } else if (profile.isJuggernaut()) {
            formatKey = "NAMETAGS.FORMAT.JUGGERNAUT";

            for (String s : nametagConfig.getStringList(formatKey)) {
                lines.add(s
                        .replace("%prefix%", prefix != null ? prefix : "")
                        .replace("%player%", target.getName())
                        .replace("%health%", String.valueOf(isTrickster ? new Random().nextInt(11) : (int) target.getHealth() / 2))
                );
            }
        } else if (isTrickster) {
            formatKey = "NAMETAGS.FORMAT.TRICKSTER";

            for (String s : nametagConfig.getStringList(formatKey)) {
                lines.add(s
                        .replace("%prefix%", prefix != null ? prefix : "")
                        .replace("%player%", target.getName())
                        .replace("%health%", String.valueOf(new Random().nextInt(11)))
                        .replace("%bounty%", String.valueOf(RNG.nextInt(1001)))
                );
            }
        } else {
            formatKey = "NAMETAGS.FORMAT.NORMAL";

            for (String s : nametagConfig.getStringList(formatKey)) {
                lines.add(s
                        .replace("%prefix%", prefix != null ? prefix : "")
                        .replace("%player%", target.getName())
                        .replace("%health%", String.valueOf((int) target.getHealth() / 2))
                );
            }
        }

        handleLunar(target, viewer, CC.translate(lines));
    }

    private void handleLunar(Player target, Player viewer, List<String> lines) {
        SoupPvP.getInstance().getClientHook().overrideNametags(target, viewer, lines);
    }

    public void updateNametagForAll(Player target) {
        for (Player viewer : SoupPvP.getInstance().getServer().getOnlinePlayers()) {
            if (!viewer.equals(target)) {
                handleUpdate(viewer, target);
            }
        }
    }

    public void reload() {
        try {
            loadConfig();
            for (Player player : SoupPvP.getInstance().getServer().getOnlinePlayers()) {
                updateNametagForAll(player);
            }
            SoupPvP.getInstance().getLogger().info("Nametag configuration reloaded!");
        } catch (Exception e) {
            SoupPvP.getInstance().getLogger().severe("Error reloading nametags: " + e.getMessage());
        }
    }
}