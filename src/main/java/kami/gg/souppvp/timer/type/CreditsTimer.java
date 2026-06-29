package kami.gg.souppvp.timer.type;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.feats.actionbar.type.GeneralActionBar;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.util.DurationFormatter;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class CreditsTimer {

    private final String name;
    private final long rewardInterval;
    private final int creditsReward;
    private final Map<UUID, Long> playerStartTime;
    private final Map<UUID, Long> lastRewardTime;

    public CreditsTimer(String name, long rewardInterval, int creditsReward) {
        this.name = name;
        this.rewardInterval = rewardInterval;
        this.creditsReward = creditsReward;
        this.playerStartTime = new ConcurrentHashMap<>();
        this.lastRewardTime = new ConcurrentHashMap<>();

        startRunnable();
    }

    private void startRunnable() {
        new BukkitRunnable() {
            @Override
            public void run() {
                tick();
            }
        }.runTaskTimerAsynchronously(SoupPvP.getInstance(), 20L, 20L);
    }

    public void startTimer(Player player) {
        UUID uuid = player.getUniqueId();
        long now = System.currentTimeMillis();
        playerStartTime.put(uuid, now);
        lastRewardTime.put(uuid, now);
    }

    public void stopTimer(Player player) {
        UUID uuid = player.getUniqueId();
        playerStartTime.remove(uuid);
        lastRewardTime.remove(uuid);
    }

    public boolean hasTimer(Player player) {
        return playerStartTime.containsKey(player.getUniqueId());
    }

    public long getElapsed(Player player) {
        Long startTime = playerStartTime.get(player.getUniqueId());
        if (startTime == null) return 0L;
        return System.currentTimeMillis() - startTime;
    }

    public long getRemaining(Player player) {
        Long lastReward = lastRewardTime.get(player.getUniqueId());
        if (lastReward == null) return 0L;
        long elapsed = System.currentTimeMillis() - lastReward;
        return Math.max(0L, rewardInterval - elapsed);
    }

    public String getRemainingString(Player player) {
        long rem = getRemaining(player);
        return DurationFormatter.getRemaining(rem, true);
    }

    public void tick() {
        long now = System.currentTimeMillis();

        for (Map.Entry<UUID, Long> entry : lastRewardTime.entrySet()) {
            UUID uuid = entry.getKey();
            Long lastReward = entry.getValue();

            if (now - lastReward >= rewardInterval) {
                Player player = Bukkit.getPlayer(uuid);
                if (player != null && player.isOnline()) {
                    giveReward(player);
                    lastRewardTime.put(uuid, now);
                }
            }
        }
    }

    private void giveReward(Player player) {
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        if (profile == null) return;

        profile.setCredits(profile.getCredits() + creditsReward);
        profile.saveProfile();

        Bukkit.getScheduler().runTask(SoupPvP.getInstance(), () -> {
            player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
            GeneralActionBar.sendMessage(player, "&a+20 Credits &7(Playtime Reward)");
        });
    }

    public void clear() {
        playerStartTime.clear();
        lastRewardTime.clear();
    }
}
