package kami.gg.souppvp.kit.ability.impl;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.kit.ability.KitAbility;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.profile.ProfileState;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class FishermanAbility implements KitAbility {

    @Override
    public String getName() {
        return "Fisherman";
    }

    @Override
    public String getDescription() {
        return "Hook players and pull them towards you";
    }

    @Override
    public String getColor() {
        return "&e";
    }

    @Override
    public ItemStack getItem() {
        return new ItemBuilder(Material.FISHING_ROD)
                .name("&e&lFisherman Rod")
                .lore(
                        "&7Hook players and pull",
                        "&7them towards you"
                )
                .build();
    }

    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        Player player = event.getPlayer();
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());

        if (profile == null) return;
        if (profile.getProfileState() == ProfileState.SPAWN) return;

        if (event.getState() == PlayerFishEvent.State.CAUGHT_ENTITY) {
            Entity caught = event.getCaught();
            
            if (caught instanceof Player target) {
                Profile targetProfile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(target.getUniqueId());
                
                if (targetProfile.getProfileState() == ProfileState.SPAWN) {
                    event.setCancelled(true);
                    player.sendMessage(CC.t("&cYou can't hook players in spawn."));
                    return;
                }

                // Pull target towards player
                Vector vector = player.getLocation().toVector().subtract(target.getLocation().toVector()).normalize().multiply(1.5);
                target.setVelocity(vector);
                
                event.setCancelled(true);
            }
        }
    }
}
