package kami.gg.souppvp.feats.actionbar.type;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.feats.actionbar.ActionBarPriority;
import kami.gg.souppvp.feats.actionbar.ActionBarProvider;
import kami.gg.souppvp.kit.ability.AbilityItemComparator;
import kami.gg.souppvp.kit.ability.KitAbility;
import kami.gg.souppvp.kit.ability.KitAbilityRegistry;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.timer.Timer;
import kami.gg.souppvp.util.CC;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/*
 * Copyright (c) 2026. @Comunidad, made since 28/6/2026
 * Use or redistribution of this source file is only permitted
 * if explicit permission is given by the author.
 */
public class AbilityActionBar implements ActionBarProvider {

    @Override
    public String getActionBar(Player player) {
        ItemStack itemInHand = player.getItemInHand();
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        KitAbilityRegistry registry = SoupPvP.getInstance().getKitAbilityRegistry();

        if (itemInHand == null || itemInHand.getType().name().equals("AIR")) return "";
        if (profile == null || profile.getCurrentKit() == null) return "";

        KitAbility matchingAbility = null;
        for (KitAbility ability : registry.getAbilities().values()) {
            if (AbilityItemComparator.isSameAbilityItem(ability.getItem(), itemInHand)) {
                matchingAbility = ability;
                break;
            }
        }

        if (matchingAbility == null) return "";

        Timer timer = SoupPvP.getInstance().getTimerManager().getTimer(matchingAbility.getName());
        if (timer == null || !timer.hasTimer(player)) return "";

        String remaining = timer.getRemainingString(player);
        return CC.t(matchingAbility.getColor() + matchingAbility.getName() + ": &c" + remaining);
    }

    @Override
    public ActionBarPriority priority() {
        return ActionBarPriority.ABILITY;
    }

    @Override
    public boolean isActive(Player player) {
        ItemStack itemInHand = player.getItemInHand();
        if (itemInHand == null || itemInHand.getType().name().equals("AIR")) return false;

        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        if (profile == null || profile.getCurrentKit() == null) return false;

        KitAbilityRegistry registry = SoupPvP.getInstance().getKitAbilityRegistry();
        for (KitAbility ability : registry.getAbilities().values()) {
            if (AbilityItemComparator.isSameAbilityItem(ability.getItem(), itemInHand)) {
                Timer timer = SoupPvP.getInstance().getTimerManager().getTimer(ability.getName());
                return timer != null && timer.hasTimer(player);
            }
        }

        return false;
    }
}
