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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DistortionPerk extends Perk implements Listener {

    @Override
    public String getName() {
        return "Distortion";
    }

    @Override
    public String getColor() {
        return "&8";
    }

    @Override
    public List<String> getDescription() {
        List<String> lore = new ArrayList<>();
        lore.add(CC.t("&7Attacking players has a 5% chance of"));
        lore.add(CC.t("&7blinding your enemies."));
        return lore;
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.COAL);
    }

    @Override
    public int getCost() {
        return 750;
    }


    @EventHandler
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event){
        if (!(event.getDamager() instanceof Player)) return;
        if (!(event.getEntity() instanceof Player)) return;
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(event.getDamager().getUniqueId());
        if (profile.isInEvent()) return;
        Perk currentPerk = SoupPvP.getInstance().getPerksHandler().getPerkByName(profile.getActivePerks().get(1));
        if (currentPerk == null) return;
        Perk distortionPerk = SoupPvP.getInstance().getPerksHandler().getPerkByName("Distortion");
        if (currentPerk == distortionPerk){
            if (new Random().nextInt(100) <= 5){
                ((Player) event.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 1));
            }
        }
    }

}
