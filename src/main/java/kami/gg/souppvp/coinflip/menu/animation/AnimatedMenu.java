package kami.gg.souppvp.coinflip.menu.animation;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.coinflip.CoinFlip;
import kami.gg.souppvp.coinflip.menu.animation.sub.GreenMenu;
import kami.gg.souppvp.coinflip.menu.animation.sub.RedMenu;
import kami.gg.souppvp.util.PlayerUtil;
import kami.gg.souppvp.util.menu.Button;
import kami.gg.souppvp.util.menu.Menu;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class AnimatedMenu extends Menu {

    private final CoinFlip coinFlip;

    public AnimatedMenu(CoinFlip coinFlip, Player player) {
        super(player, "&a&lCoinflip Match", 27, true);
        this.coinFlip = coinFlip;
    }

    @Override
    public Map<Integer, Button> getButtons() {
        HashMap<Integer, Button> buttonHashMap = new HashMap<>();
        new BukkitRunnable(){
            int i = 10;
            @Override
            public void run() {
                i--;
                if (Bukkit.getPlayer(coinFlip.getCreator()) == null || Bukkit.getPlayer(coinFlip.getOpponent()) == null){
                    player.closeInventory();
                    this.cancel();
                }
                if (i == 0) {
                    if (player.getUniqueId().equals(coinFlip.getCreator())){
                        coinFlip.start();
                    }
                    this.cancel();
                }
                if (i % 2 == 0) {
                    new GreenMenu(coinFlip, player).update();
                    PlayerUtil.playSound(player, Sound.CLICK, 1.0);
                } else {
                    new RedMenu(coinFlip, player).update();
                    PlayerUtil.playSound(player, Sound.CLICK, 1.0);
                }
            }
        }.runTaskTimer(SoupPvP.getInstance(), 0L, 10L);
        return buttonHashMap;
    }
}
