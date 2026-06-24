package kami.gg.souppvp.perk.inherit;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.perk.Perk;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class MartydomPerk extends Perk implements Listener {

    @Override
    public String getName() {
        return "Martydom";
    }

    @Override
    public String getColor() {
        return "&a";
    }

    @Override
    public List<String> getDescription() {
        List<String> lore = new ArrayList<>();
        lore.add(CC.t("&7Have a small chance of"));
        lore.add(CC.t("&7spawning a creeper where you die."));
        return lore;
    }

    @Override
    public ItemStack getIcon() {
        return new ItemBuilder(Material.MONSTER_EGG).durability(50).build();
    }

    @Override
    public int getCost() {
        return 150;
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
        Perk soreLoserPerk = SoupPvP.getInstance().getPerksHandler().getPerkByName("Martydom");
        if (currentPerk == soreLoserPerk){
            Creeper creeper = (Creeper) event.getEntity().getWorld().spawnEntity(event.getEntity().getEyeLocation(), EntityType.CREEPER);
            creeper.setPowered(true);
            creeper.setCustomName(CC.t("&b&lMartydom"));
        }
    }

}
