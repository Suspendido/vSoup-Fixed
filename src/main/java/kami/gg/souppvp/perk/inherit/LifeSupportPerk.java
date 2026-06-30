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
        return List.of("&7Chance to be saved from death by", "&7receiving bonus hearts.");
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
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Player player)) return;
        if (!(e.getDamager() instanceof Player)) return;

        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        if (profile == null) return;
        if (profile.isInEvent()) return;
        if (!profile.getActivePerks().contains(getName())) return;

        if (player.getHealth() < 3.0 && new Random().nextDouble() <= 0.3) {
            player.setHealth(player.getMaxHealth());
            player.sendMessage(CC.t("&cYour Life Support Perk came in clutch."));
        }
    }

}
