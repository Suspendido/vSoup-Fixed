package kami.gg.souppvp.killstreak.menu.editor.button;

import kami.gg.souppvp.killstreak.ConfigurableKillstreak;
import kami.gg.souppvp.killstreak.menu.editor.KillstreakCreateMenu;
import kami.gg.souppvp.killstreak.menu.editor.KillstreakEditMenu;
import kami.gg.souppvp.killstreak.menu.editor.KillstreakLoreEditMenu;
import kami.gg.souppvp.util.ItemBuilder;
import kami.gg.souppvp.util.menu.Button;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class EditDescriptionButton extends Button {

    private final KillstreakCreateMenu createMenu;
    private final KillstreakEditMenu editMenu;
    private final ConfigurableKillstreak killstreak;
    private final boolean isCreateMode;

    public EditDescriptionButton(KillstreakCreateMenu createMenu) {
        this.createMenu = createMenu;
        this.editMenu = null;
        this.killstreak = null;
        this.isCreateMode = true;
    }

    public EditDescriptionButton(KillstreakEditMenu editMenu, ConfigurableKillstreak killstreak) {
        this.createMenu = null;
        this.editMenu = editMenu;
        this.killstreak = killstreak;
        this.isCreateMode = false;
    }

    @Override
    public ItemStack getButtonItem(Player player) {
        List<String> lore = new ArrayList<>(isCreateMode ? createMenu.getLore() : killstreak.getLore());
        lore.add("");
        lore.add("&aClick to edit description");

        return new ItemBuilder(Material.BOOK_AND_QUILL)
                .name("&b&lEdit Description")
                .lore(lore)
                .build();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
        playNeutral(player);
        if (isCreateMode) {
            // Create temporary killstreak for configuration
            ConfigurableKillstreak tempKillstreak = new ConfigurableKillstreak(
                0,
                createMenu.getName().isEmpty() ? "Temp" : createMenu.getName(),
                createMenu.getRequiredKills(),
                createMenu.getRewardType(),
                createMenu.getRewardData(),
                createMenu.getIconMaterial(),
                createMenu.getLore()
            );
            createMenu.setTempKillstreak(tempKillstreak);
            new KillstreakLoreEditMenu(tempKillstreak, -1, player).open();
        } else {
            new KillstreakLoreEditMenu(killstreak, editMenu.getListIndex(), player).open();
        }
    }
}
