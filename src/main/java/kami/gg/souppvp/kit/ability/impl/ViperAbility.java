package kami.gg.souppvp.kit.ability.impl;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.kit.ability.KitAbility;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.profile.ProfileState;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;

public class ViperAbility implements KitAbility {

    private static final Random RANDOM = new Random();

    @Override
    public String getName() {
        return "Viper";
    }

    @Override
    public String getDescription() {
        return "&f10% chance to poison enemies on hit for 10 seconds";
    }

    @Override
    public String getColor() {
        return "&a";
    }

    @Override
    public ItemStack getItem() {
        return new ItemBuilder(Material.POTION).lore("Dont Display").durability(8196).build();
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player)) return;

        Profile damagerProfile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(event.getDamager().getUniqueId());

        if (damagerProfile.isInEvent() || damagerProfile.getProfileState() == ProfileState.SPAWN) return;

        if (!hasAbility((Player) event.getDamager(), damagerProfile, getName())) return;

        if (RANDOM.nextInt(100) <= 10) {
            ((Player) event.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.POISON, 10 * 20, 0));
            event.getEntity().sendMessage(CC.t("&cYou have been poisoned by &e" + event.getDamager().getName() + "&c."));
        }
    }
}
