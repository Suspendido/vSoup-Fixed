package kami.gg.souppvp.perk.inherit.tier1;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.perk.Perk;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.util.CC;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NourishmentPerk extends Perk implements Listener {

    @Override
    public String getName() {
        return "Nourishment";
    }

    @Override
    public List<String> getDescription() {
        List<String> lore = new ArrayList<>();
        lore.add(CC.t("&7Every kill has a 25% chance to"));
        lore.add(CC.t("&7replenish your hotbar."));
        return lore;
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.BREAD);
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
    public void onPlayerDeathEvent(PlayerDeathEvent event){
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(event.getEntity().getUniqueId());
        Perk currentPerk = SoupPvP.getInstance().getPerksHandler().getPerkByName(profile.getActivePerks().get(0));
        if (profile.isInEvent()) return;
        if (currentPerk == null) return;
        Perk nourishmentPerk = SoupPvP.getInstance().getPerksHandler().getPerkByName("Nourishment");
        if (currentPerk == nourishmentPerk){
            if (new Random().nextInt(100) <= 25){
                for (int i=0; i<9; i++){
                    ItemStack itemStack = event.getEntity().getKiller().getInventory().getItem(i);
                    if (itemStack == null){
                        event.getEntity().getKiller().getInventory().setItem(i, new ItemStack(Material.MUSHROOM_SOUP));
                        return;
                    } else {
                        if (itemStack.getType() == Material.BOWL){
                            event.getEntity().getKiller().getInventory().setItem(i, new ItemStack(Material.MUSHROOM_SOUP));
                            return;
                        }
                    }
                }
            }
        }
    }

}
