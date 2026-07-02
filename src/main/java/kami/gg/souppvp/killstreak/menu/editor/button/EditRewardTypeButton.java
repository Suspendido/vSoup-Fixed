package kami.gg.souppvp.killstreak.menu.editor.button;

import kami.gg.souppvp.killstreak.ConfigurableKillstreak;
import kami.gg.souppvp.killstreak.menu.editor.KillstreakCreateMenu;
import kami.gg.souppvp.util.ItemBuilder;
import kami.gg.souppvp.util.menu.Button;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class EditRewardTypeButton extends Button {

    private final KillstreakCreateMenu createMenu;
    private final ConfigurableKillstreak killstreak;
    private final boolean isCreateMode;

    public EditRewardTypeButton(KillstreakCreateMenu createMenu) {
        this.createMenu = createMenu;
        this.killstreak = null;
        this.isCreateMode = true;
    }

    public EditRewardTypeButton(ConfigurableKillstreak killstreak) {
        this.createMenu = null;
        this.killstreak = killstreak;
        this.isCreateMode = false;
    }

    @Override
    public ItemStack getButtonItem(Player player) {
        ConfigurableKillstreak.RewardType currentType = isCreateMode ? createMenu.getRewardType() : killstreak.getRewardType();
        Material mat = switch (currentType) {
            case ITEMS -> Material.CHEST;
            case EFFECTS -> Material.POTION;
            case SPECIAL -> Material.TNT;
        };
        return new ItemBuilder(mat)
                .name("&b&lEdit Reward Type")
                .lore(
                        "&b┃ &fCurrent: &e" + currentType.name(),
                        "",
                        "&aClick to cycle through types"
                )
                .build();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
        ConfigurableKillstreak.RewardType[] types = ConfigurableKillstreak.RewardType.values();
        ConfigurableKillstreak.RewardType currentType = isCreateMode ? createMenu.getRewardType() : killstreak.getRewardType();
        int currentIndex = java.util.Arrays.asList(types).indexOf(currentType);
        ConfigurableKillstreak.RewardType newType = types[(currentIndex + 1) % types.length];

        if (isCreateMode) {
            createMenu.setRewardType(newType);
        } else {
            killstreak.setRewardType(newType);
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
