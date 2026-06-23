package kami.gg.souppvp.perk.menu.adjust;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.perk.Perk;
import kami.gg.souppvp.perk.menu.PerksMenu;
import kami.gg.souppvp.perk.menu.adjust.button.*;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.menu.Button;
import kami.gg.souppvp.util.menu.Menu;
import kami.gg.souppvp.util.menu.button.BackButton;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class AdjustPerksMenu extends Menu {

    private final int tier;

    public AdjustPerksMenu(int tier) {
        this.tier = tier;
    }

    @Override
    public String getTitle(Player player) {
        String color = null;
        if (tier == 1) color = "&e";
        if (tier == 2) color = "&c";
        if (tier == 3) color = "&5";
        return CC.t(color + "Tier " + tier + " Perks");
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(0, new BackButton(new PerksMenu()));
        buttons.put(4, new AdjustPerkSlotButton(tier));
        buttons.put(8, new AdjustResetPerkButton(tier));

        for (int i = 0; i < 27; i++) {
            buttons.putIfAbsent(i, Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 15, " "));
        }

        for (int i = 27; i < 54; i++) {
            if (i == 27 || i == 35 || i == 36 || i >= 44){
                buttons.put(i, Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 15, " "));
            }
        }

        int i=28;

        for (Perk perk : SoupPvP.getInstance().getPerksHandler().getPerks()){
            if (perk.getTier() == tier) {
                buttons.putIfAbsent(i, new PerkButton(perk, perk.getTier()));
                if (i == 34) {
                    i = 37;
                } else {
                    i++;
                }
            }
        }
        return buttons;
    }

    @Override
    public int size(Map<Integer, Button> buttons) {
        return 54;
    }
}
