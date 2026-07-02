package kami.gg.souppvp.killstreak.menu.editor.button;

import kami.gg.souppvp.killstreak.ConfigurableKillstreak;
import kami.gg.souppvp.killstreak.menu.editor.KillstreakCreateMenu;
import kami.gg.souppvp.util.ItemBuilder;
import kami.gg.souppvp.util.menu.Button;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class EditIconButton extends Button {

    private final KillstreakCreateMenu createMenu;
    private final ConfigurableKillstreak killstreak;
    private final boolean isCreateMode;

    public EditIconButton(KillstreakCreateMenu createMenu) {
        this.createMenu = createMenu;
        this.killstreak = null;
        this.isCreateMode = true;
    }

    public EditIconButton(ConfigurableKillstreak killstreak) {
        this.createMenu = null;
        this.killstreak = killstreak;
        this.isCreateMode = false;
    }

    @Override
    public ItemStack getButtonItem(Player player) {
        Material currentIcon = isCreateMode ? createMenu.getIconMaterial() : killstreak.getIconMaterial();
        return new ItemBuilder(currentIcon)
                .name("&b&lEdit Icon")
                .lore(
                        "&b┃ &fCurrent: " + currentIcon.name(),
                        "",
                        "&aClick to change icon",
                        "&aHold item in hand and click"
                )
                .build();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
        ItemStack handItem = player.getItemInHand();
        if (handItem == null || handItem.getType() == Material.AIR) {
            sendMessage(player, "&cYou must hold an item in your hand!");
            playFail(player);
            return;
        }

        Material newIcon = handItem.getType();
        if (isCreateMode) {
            createMenu.setIconMaterial(newIcon);
        } else {
            killstreak.setIconMaterial(newIcon);
        }

        playSuccess(player);
        sendMessage(player, "&aIcon updated to: " + newIcon.name());
        updateMenu();
    }

    private void updateMenu() {
        if (isCreateMode) {
            createMenu.update();
        }
    }
}
