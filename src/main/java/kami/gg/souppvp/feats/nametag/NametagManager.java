package kami.gg.souppvp.feats.nametag;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.feats.nametag.adapter.NametagAdapter;
import kami.gg.souppvp.feats.nametag.adapter.NametagColor;
import kami.gg.souppvp.feats.nametag.task.NametagTask;
import kami.gg.souppvp.perk.Perk;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.profile.ProfileState;
import kami.gg.souppvp.tier.TierCategory;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.NameThreadFactory;
import lombok.Getter;
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

    private final SoupPvP instance;
    private final Map<UUID, Nametag> nametags;
    private final NametagAdapter adapter;
    private final Map<UUID, Long> frozenStartTimes = new ConcurrentHashMap<>();
    private final ScheduledExecutorService executor;
    private static final String CONFIG_FILE = "nametags.yml";
    private static final Random RNG = new Random();
    private FileConfiguration nametagConfig;

    public NametagManager() {
        this.instance = SoupPvP.getInstance();
        this.nametags = new ConcurrentHashMap<>();
        this.adapter = new NametagColor();
        this.executor = Executors.newScheduledThreadPool(1, new NameThreadFactory("SoupPvP - NametagThread"));
        this.executor.scheduleAtFixedRate(new NametagTask(), 0L, 500L, TimeUnit.MILLISECONDS);

        this.loadConfig();
        new NametagListener();
    }

    private void loadConfig() {
        try {
            File configFile = new File(instance.getDataFolder(), CONFIG_FILE);
            if (!configFile.exists()) {
                instance.saveResource(CONFIG_FILE, false);
            }
            nametagConfig = YamlConfiguration.loadConfiguration(configFile);
        } catch (Exception e) {
            instance.getLogger().severe("Failed to load nametag configuration: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void handleUpdate(Player viewer, Player target) {
        if (viewer == null || target == null) return;
        String prefix = getAdapter().getAndUpdate(viewer, target);
        updateLunarTags(viewer, target, prefix);
    }

    public void updateLunarTags(Player viewer, Player target, String prefix) {
        if (instance.getClientHook().getClients().isEmpty()) return;
        if (!nametags.containsKey(viewer.getUniqueId())) return;

        Profile profile = instance.getProfilesHandler().getProfileByUUID(target.getUniqueId());
        boolean staff = instance.getStaffManager().isStaffEnabled(target.getPlayer());
        boolean isInSpawn = instance.getSpawnHandler().getCuboid().contains(target) && profile.getProfileState() == ProfileState.SPAWN;
        List<String> lines = new ArrayList<>();

        TierCategory tierCategory = TierCategory.getCategoryByName(profile.getSelectedTierIcon());
        String tierDisplay = profile.getTier().getTierLevel() + tierCategory.getFormattedIcon();

        if (staff) {
            for (String s : nametagConfig.getStringList("NAMETAGS.FORMAT.STAFF")) {
                lines.add(s
                        .replace("%player%", target.getName())
                        .replace("%health%", String.valueOf((int) target.getHealth() / 2))
                        .replace("%rank%", instance.getRankHook().getRankColor(target) + instance.getRankHook().getRankName(target))
                        .replace("%rank-prefix%", instance.getRankHook().getRankPrefix(target))
                        .replace("%tier%", tierDisplay)
                );
            }
            handleLunar(target, viewer, CC.t(lines));
            return;
        }

        boolean isTrickster = false;
        try {
            if (!profile.getActivePerks().isEmpty()) {
                Perk currentPerk = instance.getPerksHandler().getPerkByName(profile.getActivePerks().getFirst());
                Perk tricksterPerk = instance.getPerksHandler().getPerkByName("Trickster");
                isTrickster = (currentPerk != null && currentPerk.equals(tricksterPerk));
            }
        } catch (Exception ignored) {}

        if (isInSpawn) {
            int bountyValue = isTrickster ? RNG.nextInt(1001) : profile.getBounty();
            for (String s : nametagConfig.getStringList("NAMETAGS.FORMAT.SPAWN")) {
                lines.add(s
                        .replace("%player%", target.getName())
                        .replace("%health%", String.valueOf((int) target.getHealth() / 2))
                        .replace("%rank%", instance.getRankHook().getRankColor(target) + instance.getRankHook().getRankName(target))
                        .replace("%rank-prefix%", instance.getRankHook().getRankPrefix(target))
                        .replace("%rank_color%", instance.getRankHook().getRankColor(target.getPlayer()))
                        .replace("%rank_suffix%", instance.getRankHook().getRankSuffix(target.getPlayer()))
                        .replace("%bounty%", String.valueOf(bountyValue))
                        .replace("%tier%", tierDisplay)

                );
            }
            handleLunar(target, viewer, CC.t(lines));
            return;
        }

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
                        .replace("%tier%", tierDisplay)
                );
            }
        } else if (profile.isJuggernaut()) {
            formatKey = "NAMETAGS.FORMAT.JUGGERNAUT";

            for (String s : nametagConfig.getStringList(formatKey)) {
                lines.add(s
                        .replace("%prefix%", prefix != null ? prefix : "")
                        .replace("%player%", target.getName())
                        .replace("%health%", String.valueOf(isTrickster ? new Random().nextInt(11) : (int) target.getHealth() / 2))
                        .replace("%tier%", tierDisplay)
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
                        .replace("%tier%", tierDisplay)
                );
            }
        } else if (profile.isInEvent()) {
            if (profile.getTntTagGame() != null) {
                boolean hasTNT = profile.getTntTagGame().getTntHolder() != null && profile.getTntTagGame().getTntHolder().equals(target.getUniqueId());
                formatKey = hasTNT ? "NAMETAGS.FORMAT.TNTTAG_HOLDER" : "NAMETAGS.FORMAT.TNTTAG_NORMAL";

                for (String s : nametagConfig.getStringList(formatKey)) {
                    lines.add(s
                            .replace("%prefix%", prefix != null ? prefix : "")
                            .replace("%player%", target.getName())
                            .replace("%health%", String.valueOf((int) target.getHealth() / 2))
                            .replace("%tier%", tierDisplay)
                    );
                }
            }
            else if (profile.getSumoEvent() != null) {
                formatKey = "NAMETAGS.FORMAT.SUMO";

                for (String s : nametagConfig.getStringList(formatKey)) {
                    lines.add(s
                            .replace("%prefix%", prefix != null ? prefix : "")
                            .replace("%player%", target.getName())
                            .replace("%health%", String.valueOf((int) target.getHealth() / 2))
                            .replace("%tier%", tierDisplay)
                    );
                }
            }
        } else {
            formatKey = "NAMETAGS.FORMAT.NORMAL";

            for (String s : nametagConfig.getStringList(formatKey)) {
                lines.add(s
                        .replace("%prefix%", prefix != null ? prefix : "")
                        .replace("%player%", target.getName())
                        .replace("%health%", String.valueOf((int) target.getHealth() / 2))
                        .replace("%tier%", tierDisplay)
                );
            }
        }

        handleLunar(target, viewer, CC.t(lines));
    }

    private void handleLunar(Player target, Player viewer, List<String> lines) {
        instance.getClientHook().overrideNametags(target, viewer, lines);
    }

    public void updateNametagForAll(Player target) {
        for (Player viewer : instance.getServer().getOnlinePlayers()) {
            if (!viewer.equals(target)) {
                handleUpdate(viewer, target);
            }
        }
    }

    public void reload() {
        try {
            loadConfig();
            for (Player player : instance.getServer().getOnlinePlayers()) {
                updateNametagForAll(player);
            }
            instance.getLogger().info("Nametag configuration reloaded!");
        } catch (Exception e) {
            instance.getLogger().severe("Error reloading nametags: " + e.getMessage());
        }
    }
}