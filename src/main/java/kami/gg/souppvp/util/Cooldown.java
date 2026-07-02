package kami.gg.souppvp.util;

import lombok.Data;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class Cooldown {

	private long start = System.currentTimeMillis();
	private long expire;
    private final Map<UUID, Long> cooldowns;
	private boolean notified;

	public Cooldown(long duration) {
		this.expire = this.start + duration;
        this.cooldowns = new ConcurrentHashMap<>();

        if (duration == 0) {
			this.notified = true;
		}
	}

	public long getPassed() {
		return System.currentTimeMillis() - this.start;
	}

	public long getRemaining() {
		return this.expire - System.currentTimeMillis();
	}

	public boolean hasExpired() {
		return System.currentTimeMillis() - this.expire >= 0;
	}

	public String getTimeLeft() {
		if (this.getRemaining() >= 60_000) {
			return Formatter.millisToRoundedTime(this.getRemaining());
		} else {
			return Formatter.millisToSeconds(this.getRemaining());
		}
	}
    public boolean hasCooldown(Player player) {
        return cooldowns.containsKey(player.getUniqueId()) && (cooldowns.get(player.getUniqueId()) >= System.currentTimeMillis());
    }

    public void applyCooldownTicks(Player player, int ticks) {
        cooldowns.put(player.getUniqueId(), System.currentTimeMillis() + ticks);
    }

}
