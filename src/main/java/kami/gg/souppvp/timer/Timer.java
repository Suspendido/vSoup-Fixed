package kami.gg.souppvp.timer;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.DurationFormatter;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class Timer {

    protected final String name;
    protected final long defaultDuration;
    protected final Map<UUID, Long> timerCache;

    public Timer(String name, long defaultDuration) {
        this.name = name;
        this.defaultDuration = defaultDuration;
        this.timerCache = new ConcurrentHashMap<>();
    }

    public void applyTimer(Player player) {
        applyTimer(player, defaultDuration);
    }

    public void applyTimer(Player player, long duration) {
        timerCache.put(player.getUniqueId(), System.currentTimeMillis() + duration);
    }

    public void removeTimer(Player player) {
        timerCache.remove(player.getUniqueId());
    }

    public boolean hasTimer(Player player) {
        Long remaining = timerCache.get(player.getUniqueId());
        return remaining != null && remaining >= System.currentTimeMillis();
    }

    public long getRemaining(Player player) {
        Long remaining = timerCache.get(player.getUniqueId());
        if (remaining == null) return 0L;
        return remaining - System.currentTimeMillis();
    }

    public String getRemainingString(Player player) {
        long rem = getRemaining(player);
        return DurationFormatter.getRemaining(rem, true);
    }

    public void tick() {
        timerCache.entrySet().removeIf(entry -> {
            if (entry.getValue() - System.currentTimeMillis() < 0L) {
                UUID uuid = entry.getKey();
                // Schedule sync task for player operations
                Bukkit.getScheduler().runTask(SoupPvP.getInstance(), () -> {
                    Player player = Bukkit.getPlayer(uuid);
                    if (player != null) {
                        player.sendMessage(CC.t("&eYou may now use &d" + name + "&e!"));
                        player.playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 1.0F, 1.0F);
                    }
                });
                return true;
            }
            return false;
        });
    }

    public void clear() {
        timerCache.clear();
    }
}
