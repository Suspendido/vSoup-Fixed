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

public class AquamanPerk extends Perk implements Listener {

    @Override
    public String getName() {
        return "Aquaman";
    }

    @Override
    public String getColor() {
        return "&b";
    }

    @Override
    public List<String> getDescription() {
        List<String> lore = new ArrayList<>();
        lore.add(CC.t("&7While in water, you deal 10%"));
        lore.add(CC.t("&7more melee damage."));
        return lore;
    }

    @Override
    public ItemStack getIcon() {
        return new ItemBuilder(Material.RAW_FISH).durability(3).build();
    }

    @Override
    public int getCost() {
        return 750;
    }

    @Override
    public int getTier() {
        return 1;
    }

    @EventHandler
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event){
        if (!(event.getDamager() instanceof Player)) return;
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(event.getDamager().getUniqueId());
        if (profile.isInEvent()) return;
        Perk currentPerk = SoupPvP.getInstance().getPerksHandler().getPerkByName(profile.getActivePerks().get(0));
        if (currentPerk == null) return;
        Perk aquamanPerk = SoupPvP.getInstance().getPerksHandler().getPerkByName("Aquaman");
        if (currentPerk == aquamanPerk){
            if (event.getDamager().getLocation().getBlock().getType() == Material.WATER || event.getDamager().getLocation().getBlock().getType() == Material.STATIONARY_WATER){
                event.setDamage(event.getDamage() * 1.1);
            }
        }
    }

}
