package kami.gg.souppvp.coinflip.menu.wager;

import kami.gg.souppvp.coinflip.CoinFlip;
import kami.gg.souppvp.coinflip.button.*;
import kami.gg.souppvp.coinflip.menu.CoinFlipMenu;
import kami.gg.souppvp.util.menu.Button;
import kami.gg.souppvp.util.menu.Menu;
import kami.gg.souppvp.util.menu.button.BackButton;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

@Getter @Setter
public class WagerResultMenu extends Menu {

    private CoinFlip coinFlip;

    public WagerResultMenu(CoinFlip coinFlip) {
        this.coinFlip = coinFlip;
        setPlaceholder(true);
    }

    @Override
    public String getTitle(Player player) {
        return (coinFlip.getWinner().equals(player.getUniqueId()) ? "&a&lYOU WON!!" : "&c&lYOU LOST!!");
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        int[] decorationSlots = {
                3, 4, 5,
                12, 13, 14,
                21, 22, 23
        };

        for (int slot : decorationSlots) {
            buttons.put(slot, new WagerWinnerButton(coinFlip));
        }

        buttons.put(26, new BackButton(new CoinFlipMenu()));
        return buttons;
    }

    @Override
    public int size(Map<Integer, Button> buttons) {
        return 27;
    }
}
