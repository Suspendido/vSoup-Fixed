package kami.gg.souppvp.killstreak.menu.editor.button;

import kami.gg.souppvp.killstreak.ConfigurableKillstreak;
import kami.gg.souppvp.killstreak.menu.editor.KillstreakCreateMenu;
import kami.gg.souppvp.util.ItemBuilder;
import kami.gg.souppvp.util.menu.Button;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class EditRequiredKillsButton extends Button {

    private final KillstreakCreateMenu createMenu;
    private final ConfigurableKillstreak killstreak;
    private final boolean isCreateMode;

    public EditRequiredKillsButton(KillstreakCreateMenu createMenu) {
        this.createMenu = createMenu;
        this.killstreak = null;
        this.isCreateMode = true;
    }

    public EditRequiredKillsButton(ConfigurableKillstreak killstreak) {
        this.createMenu = null;
        this.killstreak = killstreak;
        this.isCreateMode = false;
    }

    @Override
    public ItemStack getButtonItem(Player player) {
        int currentKills = isCreateMode ? createMenu.getRequiredKills() : killstreak.getRequiredKills();
        return new ItemBuilder(Material.IRON_SWORD)
                .name("&b&lEdit Required Kills")
                .lore(
                        "&b┃ &fCurrent: " + (isCreateMode ? "&d" : "&b") + currentKills,
                        "",
                        "&eLeft Click: &b+1",
                        "&eRight Click: &b-1",
                        "&aShift Left Click: &b+5",
                        "&aShift Right Click: &b-5"
                )
                .build();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
        int amount = clickType.isShiftClick() ? 5 : 1;
        int newKills = isCreateMode ? createMenu.getRequiredKills() : killstreak.getRequiredKills();

        if (clickType.isLeftClick()) {
            newKills += amount;
        } else if (clickType.isRightClick()) {
            newKills = Math.max(1, newKills - amount);
        }

        if (isCreateMode) {
            createMenu.setRequiredKills(newKills);
        } else {
            killstreak.setRequiredKills(newKills);
        }

        playNeutral(player);
        updateMenu();
    }

    private void updateMenu() {
        if (isCreateMode) {
            createMenu.update();
        }
    }
}
