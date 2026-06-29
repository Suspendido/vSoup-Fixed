package kami.gg.souppvp.kit.button.view;

import kami.gg.souppvp.kit.Kit;
import kami.gg.souppvp.util.menu.Button;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/*
 * Copyright (c) 2026. @Comunidad, made since 28/6/2026
 * Use or redistribution of this source file is only permitted
 * if explicit permission is given by the author.
 */
public class SecondaryAbilityButton extends Button {

    private final Kit kit;

    public SecondaryAbilityButton(Kit kit) {
        this.kit = kit;
    }

    @Override
    public ItemStack getButtonItem(Player player) {
        return kit.getSecondaryAbility() != null ? kit.getSecondaryAbility().getItem() : Button.placeholder(Material.AIR).getButtonItem(player);
    }
}
