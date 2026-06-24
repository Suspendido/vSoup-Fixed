package kami.gg.souppvp.perk.inherit;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.perk.Perk;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.util.CC;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SteadyHandsPerk extends Perk implements Listener {

    @Override
    public String getName() {
        return "Steady Hands";
    }

    @Override
    public String getColor() {
        return "&2";
    }

    @Override
    public List<String> getDescription() {
        List<String> lore = new ArrayList<>();
        lore.add(CC.t("&7A chance to prevent yourself"));
        lore.add(CC.t("&7from dropping soup on the ground."));
        return lore;
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.MUSHROOM_SOUP);
    }

    @Override
    public int getCost() {
        return 475  ;
    }

    @Override
    public int getTier() {
        return 2;
    }

    @EventHandler
    public void onPlayerDropItemEvent(PlayerDropItemEvent event){
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(event.getPlayer().getUniqueId());
        if (profile.isInEvent()) return;
        Perk currentPerk = SoupPvP.getInstance().getPerksHandler().getPerkByName(profile.getActivePerks().get(1));
        if (currentPerk == null) return;
        Perk steadyHandsPerk = SoupPvP.getInstance().getPerksHandler().getPerkByName("Steady Hands");
        if (currentPerk == steadyHandsPerk){
            if (event.getItemDrop().getItemStack().getType() == Material.MUSHROOM_SOUP){
                if (new Random().nextDouble() <= 0.5){
                    event.setCancelled(true);
                }
            }
        }
    }

}
