package kami.gg.souppvp.killstreak.menu.editor.reward;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.killstreak.ConfigurableKillstreak;
import kami.gg.souppvp.killstreak.KillstreaksHandler;
import kami.gg.souppvp.killstreak.menu.editor.KillstreakCreateMenu;
import kami.gg.souppvp.killstreak.menu.editor.KillstreakEditMenu;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.ItemBuilder;
import kami.gg.souppvp.util.menu.Button;
import kami.gg.souppvp.util.menu.Menu;
import kami.gg.souppvp.util.menu.button.BackButton;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KillstreakRewardItemsEditMenu extends Menu {

    private final ConfigurableKillstreak killstreak;
    private final int listIndex;

    public KillstreakRewardItemsEditMenu(ConfigurableKillstreak killstreak, int listIndex, Player player) {
        super(player, "Edit Reward Items", 36, false);
        this.killstreak = killstreak;
        this.listIndex = listIndex;
    }

    @Override
    public Map<Integer, Button> getButtons() {
        Map<Integer, Button> buttons = new HashMap<>();

        final List<ItemStack> items;
        if (killstreak.getRewardData().getItems() == null) {
            items = new ArrayList<>();
            killstreak.getRewardData().setItems(items);
        } else {
            items = killstreak.getRewardData().getItems();
        }

        int slot = 9;
        for (int i = 0; i < items.size(); i++) {
            final int index = i;
            ItemStack item = items.get(i);

            buttons.put(slot++, new Button() {
                @Override
                public ItemStack getButtonItem(Player player) {
                    return item.clone();
                }

                @Override
                public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                    if (clickType.isRightClick()) {
                        items.remove(index);
                        saveKillstreak();
                        playFail(player);
                        sendMessage(player, "&cItem removed from slot " + (index + 1));
                        update();
                    }
                }
            });
        }

        if (listIndex == -1) {
            buttons.put(4, new Button() {
                @Override
                public ItemStack getButtonItem(Player player) {
                    return new ItemBuilder(Material.ARROW)
                            .name("&cBack")
                            .build();
                }

                @Override
                public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                    KillstreakCreateMenu createMenu = new KillstreakCreateMenu(player);
                    createMenu.setRewardData(killstreak.getRewardData());
                    createMenu.open();
                }
            });
        } else {
            buttons.put(4, new BackButton(new KillstreakEditMenu(player, killstreak, listIndex)));
        }

        // Fill row
        Button filler = getPlaceholderButton();
        for (int i = 0; i < 9; i++) {
            if (!buttons.containsKey(i)) {
                buttons.put(i, filler);
            }
        }

        return buttons;
    }

    private void saveKillstreak() {
        if (listIndex == -1) {
            // Temporary killstreak, don't save to handler
            return;
        }
        KillstreaksHandler handler = SoupPvP.getInstance().getKillstreaksHandler();
        handler.updateKillstreak(killstreak);
    }

    @Override
    public void onClickOwn(InventoryClickEvent event) {
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) {
            return;
        }

        event.setCancelled(true);
        
        List<ItemStack> items = killstreak.getRewardData().getItems();
        if (items == null) {
            items = new ArrayList<>();
            killstreak.getRewardData().setItems(items);
        }
        
        items.add(clickedItem.clone());
        saveKillstreak();
        player.sendMessage(CC.t("&aItem added to reward!"));
        update();
    }
}
