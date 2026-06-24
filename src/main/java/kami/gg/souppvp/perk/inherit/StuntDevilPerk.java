package kami.gg.souppvp.perk.inherit;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.perk.Perk;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.util.CC;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class StuntDevilPerk extends Perk implements Listener {

    @Override
    public String getName() {
        return "Stunt Devil";
    }

    @Override
    public String getColor() {
        return "&5";
    }

    @Override
    public List<String> getDescription() {
        List<String> lore = new ArrayList<>();
        lore.add(CC.t("&7If you fall from 25 blocks or more,"));
        lore.add(CC.t("&7there is a 50% chance you will not"));
        lore.add(CC.t("&7take any fall damage."));
        return lore;
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.ENDER_PEARL);
    }

    @Override
    public int getCost() {
        return 500;
    }

    @Override
    public int getTier() {
        return 1;
    }

    @EventHandler
    public void onEntityDamageEventFall(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        if (!(event.getCause() == EntityDamageEvent.DamageCause.FALL)) return;
        Player player = (Player) event.getEntity();
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        if (profile.isInEvent()) return;
        Perk currentPerk = SoupPvP.getInstance().getPerksHandler().getPerkByName(profile.getActivePerks().get(0));
        if (currentPerk == null) return;
        Perk stuntDevilPerk = SoupPvP.getInstance().getPerksHandler().getPerkByName("Stunt Devil");
        if (currentPerk == stuntDevilPerk){
            if (new Random().nextDouble() <= 0.5){
                event.setCancelled(true);
            }
        }
    }

}
