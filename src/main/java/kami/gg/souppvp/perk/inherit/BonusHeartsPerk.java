package kami.gg.souppvp.perk.inherit;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.perk.Perk;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class BonusHeartsPerk extends Perk implements Listener {

    @Override
    public String getName() {
        return "Bonus Hearts";
    }

    @Override
    public String getColor() {
        return "&c";
    }

    @Override
    public List<String> getDescription() {
        List<String> lore = new ArrayList<>();
        lore.add(CC.t("&7Every kill, you will"));
        lore.add(CC.t("&7gain five additional hearts."));
        return lore;
    }

    @Override
    public ItemStack getIcon() {
        return new ItemBuilder(Material.INK_SACK).durability(1).build();
    }

    @Override
    public int getCost() {
        return 1275;
    }

    @Override
    public int getTier() {
        return 3;
    }

    @EventHandler
    public void onPlayerDeathEvent(PlayerDeathEvent event){
        if (event.getEntity().getKiller() == null) return;
        Perk bonusHeartsPerk = SoupPvP.getInstance().getPerksHandler().getPerkByName("Bonus Hearts");
        Profile killerProfile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(event.getEntity().getKiller().getUniqueId());
        if (killerProfile.isInEvent()) return;
        if (SoupPvP.getInstance().getPerksHandler().getPerkByName(killerProfile.getActivePerks().get(2)) == bonusHeartsPerk){
            double killerHealth = event.getEntity().getKiller().getHealth();
            if (killerHealth < 10){
                event.getEntity().getKiller().setHealth(killerHealth + 10);
            } else {
                event.getEntity().getKiller().setHealth(event.getEntity().getKiller().getMaxHealth());
            }
        }
    }

}
