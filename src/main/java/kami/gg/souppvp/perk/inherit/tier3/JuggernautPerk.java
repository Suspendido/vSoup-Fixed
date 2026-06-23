package kami.gg.souppvp.perk.inherit.tier3;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.perk.Perk;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.util.CC;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class JuggernautPerk extends Perk implements Listener {

    @Override
    public String getName() {
        return "Juggernaut";
    }

    @Override
    public List<String> getDescription() {
        List<String> lore = new ArrayList<>();
        lore.add(CC.t("&7Enemy kills give you Regeneration I"));
        lore.add(CC.t("&7for up to 10 seconds."));
        return lore;
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.DIAMOND_CHESTPLATE);
    }

    @Override
    public int getCost() {
        return 2000;
    }

    @Override
    public int getTier() {
        return 3;
    }

    @EventHandler
    public void onPlayerDeathEvent(PlayerDeathEvent event){
        if (event.getEntity().getKiller() == null) return;
        Perk juggernautPerk = SoupPvP.getInstance().getPerksHandler().getPerkByName("Juggernaut");
        Profile killerProfile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(event.getEntity().getKiller().getUniqueId());
        if (killerProfile.isInEvent()) return;
        if (SoupPvP.getInstance().getPerksHandler().getPerkByName(killerProfile.getActivePerks().get(2)) == juggernautPerk){
            int number = new Random().nextInt(11);
            event.getEntity().getKiller().addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, number * 20, 0));
        }
    }

}
