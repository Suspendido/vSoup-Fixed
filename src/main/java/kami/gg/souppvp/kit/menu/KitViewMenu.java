package kami.gg.souppvp.kit.menu;

import kami.gg.souppvp.kit.Kit;
import kami.gg.souppvp.kit.button.view.*;
import kami.gg.souppvp.kit.button.view.amor.*;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.menu.Button;
import kami.gg.souppvp.util.menu.Menu;
import kami.gg.souppvp.util.menu.button.BackButton;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

@Getter @Setter
public class KitViewMenu extends Menu {

    private Kit kit;

    public KitViewMenu(Kit kit) {
        this.kit = kit;
    }

    @Override
    public String getTitle(Player player) {
        return CC.t("Viewing the " + kit.getName() + " kit");
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        int slot = 14;
        for (int i = 0; i < kit.getCombatEquipments().size(); i++) {
            buttons.put(slot++, new CombatEquipmentButton(kit, i));
        }

        ItemStack[] armor = kit.getArmor();
        putArmor(buttons, 10, armor[3], new HelmetButton(kit));
        putArmor(buttons, 11, armor[2], new ChestplateButton(kit));
        putArmor(buttons, 12, armor[1], new LeggingsButton(kit));
        putArmor(buttons, 13, armor[0], new BootsButton(kit));

        buttons.put(22, new DescriptionButton(kit));
        buttons.put(26, new BackButton(new KitsSelectMenu()));

        for (int i = 0; i < 27; i++) {
            buttons.putIfAbsent(i, Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 15, " "));
        }

        return buttons;
    }

    private void putArmor(Map<Integer, Button> map, int slot, ItemStack item, Button button) {
        if (item == null) {
            map.put(slot, Button.placeholder(Material.AIR));
        } else {
            map.put(slot, button);
        }
    }
}