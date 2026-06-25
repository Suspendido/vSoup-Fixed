package kami.gg.souppvp.coinflip.menu.confirmation;

import kami.gg.souppvp.coinflip.menu.confirmation.button.*;
import kami.gg.souppvp.util.menu.Button;
import kami.gg.souppvp.util.menu.Menu;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class ConfirmWagerMenu extends Menu {

    private final int amount;

    public ConfirmWagerMenu(int amount, Player player) {
        super(player, "&a&lConfirm Settings", 27, false);
        this.amount = amount;
    }

    @Override
    public Map<Integer, Button> getButtons() {
        HashMap<Integer, Button> buttonHashMap = new HashMap<>();
        buttonHashMap.put(10, new ConfirmSettingsButton(amount));
        buttonHashMap.put(13, new GameSettingsButton(amount));
        buttonHashMap.put(16, new CancelSettingsButton(amount));
        return buttonHashMap;
    }
}
