package kami.gg.souppvp.killstreak.menu.editor.button;

import kami.gg.souppvp.killstreak.ConfigurableKillstreak;
import kami.gg.souppvp.killstreak.menu.editor.KillstreakCreateMenu;
import kami.gg.souppvp.killstreak.menu.editor.KillstreakEditMenu;
import kami.gg.souppvp.killstreak.menu.editor.reward.KillstreakRewardEffectsEditMenu;
import kami.gg.souppvp.killstreak.menu.editor.reward.KillstreakRewardItemsEditMenu;
import kami.gg.souppvp.killstreak.menu.editor.reward.KillstreakSpecialTypeSelectMenu;
import kami.gg.souppvp.util.ItemBuilder;
import kami.gg.souppvp.util.menu.Button;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class ConfigureRewardButton extends Button {

    private final KillstreakCreateMenu createMenu;
    private final KillstreakEditMenu editMenu;
    private final ConfigurableKillstreak killstreak;
    private final boolean isCreateMode;

    public ConfigureRewardButton(KillstreakCreateMenu createMenu) {
        this.createMenu = createMenu;
        this.editMenu = null;
        this.killstreak = null;
        this.isCreateMode = true;
    }

    public ConfigureRewardButton(KillstreakEditMenu editMenu, ConfigurableKillstreak killstreak) {
        this.createMenu = null;
        this.editMenu = editMenu;
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
                .name("&b&lConfigure Reward")
                .lore(
                        "&b┃ &fType: &e" + currentType.name(),
                        "",
                        "&7Click to configure the reward",
                        "&7based on the selected type"
                )
                .build();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
        playNeutral(player);
        ConfigurableKillstreak.RewardType rewardType = isCreateMode ? createMenu.getRewardType() : killstreak.getRewardType();
        
        ConfigurableKillstreak targetKillstreak;
        int listIndex;
        
        if (isCreateMode) {
            // Create temporary killstreak for configuration
            targetKillstreak = new ConfigurableKillstreak(
                0,
                createMenu.getName().isEmpty() ? "Temp" : createMenu.getName(),
                createMenu.getRequiredKills(),
                rewardType,
                createMenu.getRewardData(),
                createMenu.getIconMaterial(),
                createMenu.getLore()
            );
            createMenu.setTempKillstreak(targetKillstreak);
            listIndex = -1;
        } else {
            targetKillstreak = killstreak;
            listIndex = editMenu.getListIndex();
        }

        switch (rewardType) {
            case EFFECTS:
                new KillstreakRewardEffectsEditMenu(targetKillstreak, listIndex, player).open();
                break;
            case ITEMS:
                new KillstreakRewardItemsEditMenu(targetKillstreak, listIndex, player).open();
                break;
            case SPECIAL:
                new KillstreakSpecialTypeSelectMenu(targetKillstreak, listIndex, player).open();
                break;
        }
    }
}
