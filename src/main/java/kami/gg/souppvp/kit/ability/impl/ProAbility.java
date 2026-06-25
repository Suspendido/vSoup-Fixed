package kami.gg.souppvp.kit.ability.impl;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.kit.ability.KitAbility;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.profile.ProfileState;
import kami.gg.souppvp.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

public class ProAbility implements KitAbility {

    @Override
    public String getName() {
        return "Pro";
    }

    @Override
    public String getDescription() {
        return "&fEarn double credits on every kill";
    }

    @Override
    public String getColor() {
        return "&b";
    }

    @Override
    public ItemStack getItem() {
        return new ItemBuilder(Material.DIAMOND)
                .name("&b&lPro Bonus")
                .lore(
                        "&7Earn double credits",
                        "&7on every kill"
                )
                .build();
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (event.getEntity() != null && event.getEntity().getKiller() != null) {
            Player killer = event.getEntity().getKiller();
            Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(killer.getUniqueId());

            if (profile.isInEvent() || profile.getProfileState() == ProfileState.SPAWN) return;
            profile.setCredits(profile.getCredits() + 17);
        }
    }
}
