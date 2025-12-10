package kami.gg.souppvp.util.menu.menus;

import java.beans.ConstructorProperties;
import java.util.HashMap;
import java.util.Map;

import kami.gg.souppvp.util.Callback;
import kami.gg.souppvp.util.menu.button.BooleanButton;
import kami.gg.souppvp.util.menu.Button;
import kami.gg.souppvp.util.menu.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ConfirmMenu extends Menu {

    private final String title;
    private final Callback<Boolean> response;

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        HashMap<Integer, Button> buttons = new HashMap<>();
        buttons.put(11, new BooleanButton(true, this.response));
        buttons.put(15, new BooleanButton(false, this.response));
        for (int i = 0; i < 27; ++i) {
            buttons.putIfAbsent(i, Button.placeholder(Material.STAINED_GLASS_PANE, (byte)15));
        }
        return buttons;
    }

    @Override
    public String getTitle(Player player) {
        return this.title;
    }

    @ConstructorProperties(value={"title", "response"})
    public ConfirmMenu(String title, Callback<Boolean> response) {
        this.title = title;
        this.response = response;
    }
}

