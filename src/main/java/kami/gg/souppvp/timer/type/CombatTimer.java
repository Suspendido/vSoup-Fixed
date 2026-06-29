package kami.gg.souppvp.timer.type;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.timer.Timer;
import kami.gg.souppvp.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CombatTimer extends Timer {

    private final SoupPvP plugin;

    public CombatTimer(SoupPvP plugin, long defaultDuration) {
        super("Combat", defaultDuration);
        this.plugin = plugin;
    }

    @Override
    public void tick() {
        timerCache.entrySet().removeIf(entry -> {
            if (entry.getValue() - System.currentTimeMillis() < 0L) {
                UUID uuid = entry.getKey();
                Player player = Bukkit.getPlayer(uuid);
                if (player != null) {
                    player.sendMessage(CC.t("&cYou are no longer combat tagged."));
                }
                return true;
            }
            return false;
        });
    }

    public void applyCombatTag(Player player) {
        applyTimer(player);
    }

    public void removeCombatTag(Player player) {
        removeTimer(player);
    }

    public boolean isCombatTagged(Player player) {
        return hasTimer(player);
    }

    public long getRemainingCombatTime(Player player) {
        return getRemaining(player);
    }

    public String getRemainingCombatTimeString(Player player) {
        return getRemainingString(player);
    }
}
