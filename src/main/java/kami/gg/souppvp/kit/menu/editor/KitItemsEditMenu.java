package kami.gg.souppvp.kit.menu.editor;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.kit.Kit;
import kami.gg.souppvp.kit.storage.KitStorage;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.menu.Button;
import kami.gg.souppvp.util.menu.Menu;
import kami.gg.souppvp.util.menu.button.BackButton;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class KitItemsEditMenu extends Menu {

    private final Kit kit;
    private final KitStorage kitStorage;

    public KitItemsEditMenu(Kit kit, Player player) {
        super(player, "Edit Combat Items", 36, false);
        this.kit = kit;
        this.kitStorage = SoupPvP.getInstance().getKitStorage();
    }

    @Override
    public Map<Integer, Button> getButtons() {
        Map<Integer, Button> buttons = new HashMap<>();

        int slot = 9;
        for (int i = 0; i < kit.getCombatEquipments().size(); i++) {
            final int index = i;
            ItemStack item = kit.getCombatEquipments().get(i);

            buttons.put(slot++, new Button() {
                @Override
                public ItemStack getButtonItem(Player player) {
                    return item.clone();
                }

                @Override
                public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                    if (clickType.isRightClick()) {
                        kit.getCombatEquipments().remove(index);
                        saveKit();
                        playFail(player);
                        sendMessage(player, "&cItem removed from slot " + (index + 1));
                        update();
                    }
                }
            });
        }

        buttons.put(4, new BackButton(new KitEditorMenu(kit, player)));

        // Fill row
        Button filler = getPlaceholderButton();
        for (int i = 0; i < 9; i++) {
            if (!buttons.containsKey(i)) {
                buttons.put(i, filler);
            }
        }

        return buttons;
    }

    private void saveKit() {
        kitStorage.saveKit(kit);
    }

    @Override
    public void onClickOwn(InventoryClickEvent event) {
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) {
            return;
        }

        event.setCancelled(true);
        kit.getCombatEquipments().add(clickedItem.clone());
        saveKit();
        player.sendMessage(CC.t("&aItem added to kit!"));
        update();
    }
}
