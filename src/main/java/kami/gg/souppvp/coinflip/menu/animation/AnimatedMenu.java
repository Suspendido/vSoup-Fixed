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

    public AnimatedMenu(CoinFlip coinFlip) {
        this.coinFlip = coinFlip;
        setAutoUpdate(true);
    }

    @Override
    public String getTitle(Player player) {
        return "&a&lCoinflip Match";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
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
                    new GreenMenu(coinFlip).openMenu(player);
                    PlayerUtil.playSound(player, Sound.CLICK, 1.0);
                } else {
                    new RedMenu(coinFlip).openMenu(player);
                    PlayerUtil.playSound(player, Sound.CLICK, 1.0);
                }
            }
        }.runTaskTimer(SoupPvP.getInstance(), 0L, 10L);
        return buttonHashMap;
    }

    @Override
    public int size(Map<Integer, Button> buttons) {
        return 27;
    }

    @Override
    public boolean isAutoUpdate() {
        return true;
    }

}
