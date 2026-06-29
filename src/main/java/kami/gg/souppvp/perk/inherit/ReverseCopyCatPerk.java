package kami.gg.souppvp.perk.inherit;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.perk.Perk;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ReverseCopyCatPerk extends Perk implements Listener {

    @Override
    public String getName() {
        return "Reverse CopyCat";
    }

    @Override
    public String getColor() {
        return "&b";
    }

    @Override
    public List<String> getDescription() {
        List<String> lore = new ArrayList<>();
        lore.add(CC.t("&7When you die, your killer has"));
        lore.add(CC.t("&7a 50% chance of getting your"));
        lore.add(CC.t("&7kit."));
        return lore;
    }

    @Override
    public ItemStack getIcon() {
        return new ItemBuilder(Material.MONSTER_EGG).durability(98).build();
    }

    @Override
    public int getCost() {
        return 350;
    }


    @EventHandler
    public void onPlayerDeathEvent(PlayerDeathEvent event){
        if (event.getEntity().getKiller() == null) return;
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(event.getEntity().getUniqueId());
        Perk currentPerk = SoupPvP.getInstance().getPerksHandler().getPerkByName(profile.getActivePerks().get(1));
        Perk reverseCopyCatPerk = SoupPvP.getInstance().getPerksHandler().getPerkByName("Reverse CopyCat");
        if (currentPerk == reverseCopyCatPerk){
            if (new Random().nextInt(100) >= 50){
                event.getEntity().getKiller().playSound(event.getEntity().getKiller().getLocation(), Sound.CAT_MEOW, 1F, 1F);

                Bukkit.getScheduler().runTaskLater(SoupPvP.getInstance(), () -> {
                    Profile killerProfile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(event.getEntity().getKiller().getUniqueId());
                    killerProfile.setPreviousKit(profile.getCurrentKit());
                }, 10L);

                Bukkit.getScheduler().scheduleSyncDelayedTask(SoupPvP.getInstance(), () ->
                        event.getEntity().playSound(event.getEntity().getLocation(), Sound.CAT_HISS, 1F, 1F), 40L);
            }
        }
    }

}
