package kami.gg.souppvp.perk.inherit;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.perk.Perk;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LifeSupportPerk extends Perk implements Listener {

    @Override
    public String getName() {
        return "Life Support";
    }

    @Override
    public String getColor() {
        return "&d";
    }

    @Override
    public List<String> getDescription() {
        List<String> lore = new ArrayList<>();
        lore.add(CC.t("&7Chance to be saved from death by"));
        lore.add(CC.t("&7receiving bonus hearts."));
        return lore;
    }

    @Override
    public ItemStack getIcon() {
        return new ItemBuilder(Material.POTION).durability(8261).build();
    }

    @Override
    public int getCost() {
        return 2000;
    }


    @EventHandler
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event){
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player){
            Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(event.getEntity().getUniqueId());
            Perk currentPerk = SoupPvP.getInstance().getPerksHandler().getPerkByName(profile.getActivePerks().get(2));
            Perk lifeSupportPerk = SoupPvP.getInstance().getPerksHandler().getPerkByName("Life Support");
            if (currentPerk == lifeSupportPerk){
                if (((Player) event.getEntity()).getHealth() < 3.0){
                    if (new Random().nextDouble() <= 0.3){
                        ((Player) event.getEntity()).setHealth(((Player) event.getEntity()).getMaxHealth());
                        event.getEntity().sendMessage(CC.t("&cYour Life Support Perk came in clutch."));
                    }
                }
            }
        }
    }

}
