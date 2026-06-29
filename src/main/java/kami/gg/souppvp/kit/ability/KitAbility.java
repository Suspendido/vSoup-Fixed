package kami.gg.souppvp.kit.ability;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.kit.Kit;
import kami.gg.souppvp.profile.Profile;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public interface KitAbility extends Listener {

    String getName();
    String getDescription();
    String getColor();
    ItemStack getItem();

    default void onKitSelect(Player player) {
    }

    default void onKitDeselect(Player player) {
    }

    default boolean hasAbility(Player player, Profile profile, String abilityName) {
        if (profile == null || profile.getCurrentKit() == null) return false;
        Kit kit = SoupPvP.getInstance().getKitsHandler().getKitByName(profile.getCurrentKit());
        if (kit == null) return false;
        
        return (kit.getPrimaryAbility() != null && kit.getPrimaryAbility().getName().equals(abilityName)) || (kit.getSecondaryAbility() != null && kit.getSecondaryAbility().getName().equals(abilityName));
    }
}
