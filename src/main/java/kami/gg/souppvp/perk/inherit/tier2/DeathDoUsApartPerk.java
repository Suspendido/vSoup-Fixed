package kami.gg.souppvp.perk.inherit.tier2;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.perk.Perk;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.util.CC;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DeathDoUsApartPerk extends Perk implements Listener {

    @Override
    public String getName() {
        return "Death Do Us Apart";
    }

    @Override
    public List<String> getDescription() {
        List<String> lore = new ArrayList<>();
        lore.add(CC.t("&7Have a chance to spawn a random debuff"));
        lore.add(CC.t("&7potions at your death point."));
        return lore;
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.SKULL_ITEM);
    }

    @Override
    public int getCost() {
        return 800;
    }

    @Override
    public int getTier() {
        return 2;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeathEvent(PlayerDeathEvent event){
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(event.getEntity().getUniqueId());
        if (profile.isInEvent()) return;
        Perk currentPerk = SoupPvP.getInstance().getPerksHandler().getPerkByName(profile.getActivePerks().get(1));
        if (currentPerk == null) return;
        Perk deathDoUsApartPerk = SoupPvP.getInstance().getPerksHandler().getPerkByName("Death Do Us Apart");
        if (currentPerk == deathDoUsApartPerk){
            if (new Random().nextDouble() >= 0.5){
                for (int i=0; i<2; i++){
                    Random random = new Random();
                    int randomNumber = random.nextInt(SoupPvP.getInstance().getPerksHandler().getDeathDoUsApartDebuffsList().size());
                    ItemStack itemStack = SoupPvP.getInstance().getPerksHandler().getDeathDoUsApartDebuffsList().get(randomNumber);
                    ThrownPotion thrownPotion = (ThrownPotion) event.getEntity().getWorld().spawnEntity(event.getEntity().getLocation().add(0, 5, 0), EntityType.SPLASH_POTION);
                    thrownPotion.setItem(itemStack);
                }
            } else {
                Random random = new Random();
                int randomNumber = random.nextInt(SoupPvP.getInstance().getPerksHandler().getDeathDoUsApartDebuffsList().size());
                ItemStack itemStack = SoupPvP.getInstance().getPerksHandler().getDeathDoUsApartDebuffsList().get(randomNumber);
                ThrownPotion thrownPotion = (ThrownPotion) event.getEntity().getWorld().spawnEntity(event.getEntity().getLocation().add(0, 5, 0), EntityType.SPLASH_POTION);
                thrownPotion.setItem(itemStack);
            }
        }
    }

}
