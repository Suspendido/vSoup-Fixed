package kami.gg.souppvp.kit.button.view;

import kami.gg.souppvp.kit.Kit;
import kami.gg.souppvp.util.ItemBuilder;
import kami.gg.souppvp.util.menu.Button;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;

public class DescriptionButton extends Button {

    private final Kit kit;

    public DescriptionButton(Kit kit) {
        this.kit = kit;
    }

    @Override
    public ItemStack getButtonItem(Player player) {
        List<String> lore = new ArrayList<>();

        if (kit.getDescription() != null && !kit.getDescription().isEmpty()) {
            lore.addAll(kit.getDescription());
        } else {
            lore.add("&7No description available.");
        }

        lore.add("");

        if (kit.getPotionEffects() != null && !kit.getPotionEffects().isEmpty()) {
            lore.add("&bPermanent Effects:");

            for (PotionEffect effect : kit.getPotionEffects()) {
                String effectName = WordUtils.capitalize(
                        effect.getType().getName().replaceAll("_", " ").toLowerCase()
                );
                int level = effect.getAmplifier() + 1;

                lore.add("&b┃ &f" + effectName + " " + level);
            }
        }

        return new ItemBuilder(Material.BOOK_AND_QUILL)
                .name("&bDescription")
                .lore(lore)
                .build();
    }
}