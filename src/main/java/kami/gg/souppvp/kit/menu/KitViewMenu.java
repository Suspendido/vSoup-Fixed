package kami.gg.souppvp.kit.menu;

import kami.gg.souppvp.kit.Kit;
import kami.gg.souppvp.kit.button.view.*;
import kami.gg.souppvp.kit.button.view.amor.*;
import kami.gg.souppvp.util.menu.Button;
import kami.gg.souppvp.util.menu.Menu;
import kami.gg.souppvp.util.menu.button.BackButton;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter @Setter
public class KitViewMenu extends Menu {

    private Kit kit;

    public KitViewMenu(Kit kit, Player player) {
        super(player, "Viewing the " + kit.getName() + " kit", 27, false);
        this.kit = kit;
    }

    private boolean shouldDisplayItem(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return true;
        List<String> lore = item.getItemMeta().getLore();
        if (lore == null) return true;
        for (String line : lore) {
            if (line != null && line.contains("Dont Display")) return false;
        }
        return true;
    }

    @Override
    public Map<Integer, Button> getButtons() {
        Map<Integer, Button> buttons = new HashMap<>();

        int slot = 14;
        for (int i = 0; i < kit.getCombatEquipments().size(); i++) {
            buttons.put(slot++, new CombatEquipmentButton(kit, i));
        }

        ItemStack primaryAbility = kit.getPrimaryAbility() != null ? kit.getPrimaryAbility().getItem() : null;
        if (shouldDisplayItem(primaryAbility)) {
            buttons.put(slot++, new PrimaryAbilityButton(kit));
        }

        ItemStack secondaryAbility = kit.getSecondaryAbility() != null ? kit.getSecondaryAbility().getItem() : null;
        if (shouldDisplayItem(secondaryAbility)) {
            buttons.put(slot++, new SecondaryAbilityButton(kit));
        }

        ItemStack[] armor = kit.getArmor();
        putArmor(buttons, 10, armor[3], new HelmetButton(kit));
        putArmor(buttons, 11, armor[2], new ChestplateButton(kit));
        putArmor(buttons, 12, armor[1], new LeggingsButton(kit));
        putArmor(buttons, 13, armor[0], new BootsButton(kit));

        buttons.put(22, new DescriptionButton(kit));
        buttons.put(26, new BackButton(new KitsSelectMenu(player)));

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