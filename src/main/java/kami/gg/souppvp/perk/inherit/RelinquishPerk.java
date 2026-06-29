package kami.gg.souppvp.perk.inherit;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.perk.Perk;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.util.CC;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RelinquishPerk extends Perk implements Listener {

    @Override
    public String getName() {
        return "Relinquish";
    }

    @Override
    public String getColor() {
        return "&5";
    }

    @Override
    public List<String> getDescription() {
        List<String> lore = new ArrayList<>();
        lore.add(CC.t("&7Every attack, have a 20% chance"));
        lore.add(CC.t("&7of removing a bowl in your inventory."));
        return lore;
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.BOWL);
    }

    @Override
    public int getCost() {
        return 250;
    }


    @EventHandler
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event){
        if (!(event.getDamager() instanceof Player)) return;
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(event.getDamager().getUniqueId());
        if (profile.isInEvent()) return;
        Perk currentPerk = SoupPvP.getInstance().getPerksHandler().getPerkByName(profile.getActivePerks().get(0));
        if (currentPerk == null) return;
        Perk relinquishPerk = SoupPvP.getInstance().getPerksHandler().getPerkByName("Relinquish");
        if (currentPerk == relinquishPerk){
            if (new Random().nextDouble() <= 0.2){
                for (int i=0; i<((Player) event.getDamager()).getInventory().getSize(); i++){
                    ItemStack itemstack = ((Player) event.getDamager()).getInventory().getItem(i);
                    if (itemstack != null) {
                        if (itemstack.getType() == Material.BOWL) {
                            if (itemstack.getAmount() > 1) {
                                itemstack.setAmount(itemstack.getAmount() - 1);
                            } else {
                                ((Player) event.getDamager()).getInventory().setItem(i, null);
                            }
                            ((Player) event.getDamager()).updateInventory();
                            return;
                        }
                    }
                }
            }
        }
    }

}
