package kami.gg.souppvp.util.countdown;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.util.Formatter;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Getter
@Setter
public class Countdown extends BukkitRunnable {

    private final String broadcastMessage;
    private final int[] broadcastAt;
    private final Runnable tickHandler;
    private final Runnable broadcastHandler;
    private final Runnable finishHandler;
    private final List<Player> messageFilter;
    private int seconds;
    private boolean first = true;

    public static CountdownBuilder of(int amount, TimeUnit unit) {
        return new CountdownBuilder((int)unit.toSeconds(amount));
    }

    public Countdown(int seconds, String broadcastMessage, Runnable tickHandler, Runnable broadcastHandler, Runnable finishHandler, List<Player> messageFilter, int ... broadcastAt) {
        this.seconds = seconds;
        this.broadcastMessage = ChatColor.translateAlternateColorCodes('&', broadcastMessage);
        this.broadcastAt = broadcastAt;
        this.tickHandler = tickHandler;
        this.broadcastHandler = broadcastHandler;
        this.finishHandler = finishHandler;
        this.messageFilter = messageFilter;
        this.runTaskTimer(SoupPvP.getInstance(), 0L, 20L);
    }

    public final void run() {
        if (!this.first) {
            --this.seconds;
        } else {
            this.first = false;
        }
        for (int index : this.broadcastAt) {
            if (this.seconds != index) continue;
            String message = this.broadcastMessage.replace("{time}", Formatter.formatIntoDetailedString(this.seconds));
            for (Player player : this.messageFilter){
                player.sendMessage(message);
            }
            if (this.broadcastHandler == null) continue;
            this.broadcastHandler.run();
        }
        if (this.seconds == 0) {
            if (this.finishHandler != null) {
                this.finishHandler.run();
            }
            this.cancel();
        } else if (this.tickHandler != null) {
            this.tickHandler.run();
        }
    }

}

