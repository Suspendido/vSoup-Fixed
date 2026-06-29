package kami.gg.souppvp.kit.ability.impl;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.kit.ability.KitAbility;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.profile.ProfileState;
import kami.gg.souppvp.timer.Timer;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.DurationFormatter;
import kami.gg.souppvp.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.concurrent.TimeUnit;

public class FishermanAbility implements KitAbility {

    private final Timer fishermanTimer;

    public FishermanAbility() {
        this.fishermanTimer = new Timer(getName(), TimeUnit.SECONDS.toMillis(30));
        SoupPvP.getInstance().getTimerManager().registerTimer(fishermanTimer);
    }

    @Override
    public String getName() {
        return "Fisherman";
    }

    @Override
    public String getDescription() {
        return "&fHook players and pull them towards you";
    }

    @Override
    public String getColor() {
        return "&e";
    }

    @Override
    public ItemStack getItem() {
        return new ItemBuilder(Material.FISHING_ROD).name("&aFishing Rod").build();
    }

    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        Player player = event.getPlayer();
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());

        if (profile == null) return;
        if (profile.getProfileState() == ProfileState.SPAWN) return;
        if (!hasAbility(player, profile, getName())) return;

        if (event.getState() == PlayerFishEvent.State.CAUGHT_ENTITY) {
            Entity caught = event.getCaught();
            
            if (caught instanceof Player target) {
                Profile targetProfile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(target.getUniqueId());
                
                if (targetProfile.getProfileState() == ProfileState.SPAWN) {
                    event.setCancelled(true);
                    player.sendMessage(CC.t("&cYou can't hook players in spawn."));
                    return;
                }

                if (fishermanTimer.hasTimer(player)) {
                    player.sendMessage(CC.t("&cYou can't use this for another &e" + DurationFormatter.getRemaining(fishermanTimer.getRemaining(player), true) + "&c."));
                    return;
                }

                fishermanTimer.applyTimer(player);
                Vector vector = player.getLocation().toVector().subtract(target.getLocation().toVector()).normalize().multiply(1.5);
                target.setVelocity(vector);
            }
        }
    }
}
