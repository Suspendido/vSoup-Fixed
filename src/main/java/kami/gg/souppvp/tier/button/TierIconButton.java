package kami.gg.souppvp.tier.button;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.tier.TierCategory;
import kami.gg.souppvp.util.ItemBuilder;
import kami.gg.souppvp.util.menu.Button;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class TierIconButton extends Button {

    private final TierCategory category;
    private final int currentLevel;
    private final Profile profile;

    public TierIconButton(TierCategory category, int currentLevel, Profile profile) {
        this.category = category;
        this.currentLevel = currentLevel;
        this.profile = profile;
    }

    @Override
    public ItemStack getButtonItem(Player player) {
        boolean unlocked = currentLevel >= category.getMinLevel();
        boolean selected = profile.getSelectedTierIcon().equals(category.name());

        List<String> lore = new ArrayList<>();
        lore.add("&7Preview: " + category.getColor() + "[" + profile.getTier().getTierLevel()  + category.getFormattedIcon() + "]");
        lore.add("");
        lore.add("&7Chat Example:");
        lore.add(category.getColor() + "[" + profile.getTier().getTierLevel()  + category.getFormattedIcon() + "] " + SoupPvP.getInstance().getRankHook().getRankPrefix(player) +  player.getName() + "&7: &fHellow World!");
        lore.add("");

        if (unlocked) {
            if (selected) {
                lore.add("&aCurrently Selected");
            } else {
                lore.add("&eClick to select this icon");
            }
        } else {
            lore.add("&cUnlocked at level " + category.getMinLevel() + "!");
        }

        Material material = category.getMaterial();
        ItemBuilder itemBuilder = new ItemBuilder(material)
                .name(category.getColor() + category.getName() + " " + category.getFormattedIcon())
                .setGlow(selected)
                .lore(lore);

        if (material == Material.SKULL_ITEM && category.getTexture() != null) {
            itemBuilder.durability((short) 3);
            itemBuilder.setHeadTexture(category.getTexture());
        }

        return itemBuilder.build();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
        if (currentLevel < category.getMinLevel()) {
            sendMessage(player, "&cYou need to reach level " + category.getMinLevel() + " to unlock this icon!");
            playFail(player);
            return;
        }

        profile.setSelectedTierIcon(category.name());
        profile.saveProfile();
        sendMessage(player, "&aYou have selected the " + category.getFormattedIcon() + " &aicon!");
        playSuccess(player);
    }
}
