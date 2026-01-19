package kami.gg.souppvp.coinflip.menu.confirmation;

import kami.gg.souppvp.coinflip.menu.confirmation.button.*;
import kami.gg.souppvp.util.menu.Button;
import kami.gg.souppvp.util.menu.Menu;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class ConfirmWagerMenu extends Menu {

    private final int amount;

    public ConfirmWagerMenu(int amount) {
        this.amount = amount;
        setPlaceholder(true);
    }


    @Override
    public String getTitle(Player player) {
        return "&a&lConfirm Settings";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        HashMap<Integer, Button> buttonHashMap = new HashMap<>();
        buttonHashMap.put(10, new ConfirmSettingsButton(amount));
        buttonHashMap.put(13, new GameSettingsButton(amount));
        buttonHashMap.put(16, new CancelSettingsButton(amount));
        return buttonHashMap;
    }

    @Override
    public int size(Map<Integer, Button> buttons) {
        return 27;
    }
}
