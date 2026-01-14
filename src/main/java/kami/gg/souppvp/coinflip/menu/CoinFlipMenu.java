package kami.gg.souppvp.coinflip.menu;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.coinflip.CoinFlip;
import kami.gg.souppvp.coinflip.button.CoinFlipWagerButton;
import kami.gg.souppvp.coinflip.button.CreateWagerButton;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.menu.Button;
import kami.gg.souppvp.util.menu.Menu;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class CoinFlipMenu extends Menu {

    @Getter
    private int currentPage = 1, totalPages = 1;

    public CoinFlipMenu(){
        setAutoUpdate(true);
    }

    @Override
    public String getTitle(Player player) {
        return CC.translate("&a&lCoinflip");
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(27, new PageButton(-1, this));
        buttons.put(35, new PageButton(1, this));

        buttons.put(4, new CreateWagerButton());

        for (int i=0; i<19; i++){
            buttons.putIfAbsent(i, Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 15, " "));
        }

        buttons.put(26, Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 15, " "));
        buttons.put(36, Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 15, " "));
        buttons.put(44, Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 15, " "));
        buttons.put(45, Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 15, " "));

        for (int i=46; i<54; i++){
            buttons.put(i, Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 15, " "));
        }

        int i=19;
        for (CoinFlip coinFlip : SoupPvP.getInstance().getCoinFlipsHandler().getCoinFlips()){
            if (i == 26){
                i = 28;
            } else if (i == 35){
                i = 37;
            } else if (i == 44){
                i = buttons.size();
            }
            buttons.putIfAbsent(i++, new CoinFlipWagerButton(coinFlip));
        }

        return buttons;
    }

    protected void changePage(int mod) {
        currentPage += mod;
    }

    @Override
    public int size(Map<Integer, Button> buttons) {
        return 54;
    }
}
