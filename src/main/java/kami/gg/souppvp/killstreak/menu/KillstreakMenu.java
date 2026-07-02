package kami.gg.souppvp.killstreak.menu;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.killstreak.ConfigurableKillstreak;
import kami.gg.souppvp.killstreak.menu.button.KillstreakButton;
import kami.gg.souppvp.killstreak.menu.editor.KillstreakCreateMenu;
import kami.gg.souppvp.util.ItemBuilder;
import kami.gg.souppvp.util.menu.Button;
import kami.gg.souppvp.util.menu.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class KillstreakMenu extends Menu {

    public KillstreakMenu(Player player) {
        super(player, "Killstreaks", 36, true);
    }

    @Override
    public Map<Integer, Button> getButtons() {
        HashMap<Integer, Button> buttons = new HashMap<>();

        int i=10;
        for (ConfigurableKillstreak killstreak : SoupPvP.getInstance().getKillstreaksHandler().getKillstreaks()) {
            if (i == 17) {
                i = 19;
            }
            buttons.put(i, new KillstreakButton(killstreak));
            i++;
        }

        if (player.hasPermission("souppvp.admin")) {
            buttons.put(4, new Button() {
                @Override
                public ItemStack getButtonItem(Player player) {
                    return new ItemBuilder(Material.SKULL_ITEM)
                            .name("&a&lCreate Killstreak")
                            .data(3)
                            .setHeadTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjA1NmJjMTI0NGZjZmY5OTM0NGYxMmFiYTQyYWMyM2ZlZTZlZjZlMzM1MWQyN2QyNzNjMTU3MjUzMWYifX19")
                            .lore("&aClick to create a new killstreak.")
                            .build();
                }

                @Override
                public void clicked(Player player, ClickType clickType) {
                    playNeutral(player);
                    new KillstreakCreateMenu(player).open();
                }
            });
        }
        setFillEnabled(true);
        return buttons;
    }
}
