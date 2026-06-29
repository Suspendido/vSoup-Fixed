package kami.gg.souppvp.kit.ability.impl;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.kit.ability.AbilityItemComparator;
import kami.gg.souppvp.kit.ability.KitAbility;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.profile.ProfileState;
import kami.gg.souppvp.timer.Timer;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.DurationFormatter;
import kami.gg.souppvp.util.ItemBuilder;
import kami.gg.souppvp.util.XPBarTimer;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.concurrent.TimeUnit;

public class RogueAbility implements KitAbility {

    private final Timer backstabTimer;

    public RogueAbility() {
        this.backstabTimer = new Timer(getName(), TimeUnit.SECONDS.toMillis(25));
        SoupPvP.getInstance().getTimerManager().registerTimer(backstabTimer);
    }

    @Override
    public String getName() {
        return "Rogue";
    }

    @Override
    public String getDescription() {
        return "&fBackstab enemies with gold sword for massive damage";
    }

    @Override
    public String getColor() {
        return "&6";
    }

    @Override
    public ItemStack getItem() {
        return new ItemBuilder(Material.GOLD_SWORD)
                .name("&6Backstab Dagger")
                .enchantment(Enchantment.DURABILITY, 10)
                .build();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player damager)) return;
        if (!(event.getEntity() instanceof Player victim)) return;

        Profile attacker = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(damager.getUniqueId());
        if (attacker == null) return;

        if (attacker.isInEvent() || attacker.getProfileState() == ProfileState.SPAWN) return;
        if (!hasAbility(damager, attacker, getName())) return;

        ItemStack hand = damager.getItemInHand();
        if (hand == null || !AbilityItemComparator.isSameAbilityItem(hand, getItem())) return;

        if (backstabTimer.hasTimer(damager)) {
            damager.sendMessage(CC.t("&cYou can't use this for another &e" + DurationFormatter.getRemaining(backstabTimer.getRemaining(damager), true) + "&c."));
            return;
        }

        if (SoupPvP.getInstance().getSpawnHandler().getCuboid().contains(damager.getLocation())) {
            damager.sendMessage(CC.t("&cYou can't do this in spawn."));
            return;
        }

        Vector attackerDir = damager.getLocation().getDirection().setY(0).normalize();
        Vector victimDir = victim.getLocation().getDirection().setY(0).normalize();

        double dot = attackerDir.dot(victimDir);
        boolean isBehind = dot > 0.15;

        if (!isBehind) {
            damager.sendMessage(CC.t("&cBackstab failed!"));
            return;
        }

        backstabTimer.applyTimer(damager);
        XPBarTimer.runXpBar(damager, 25);

        damager.playSound(damager.getLocation(), Sound.ITEM_BREAK, 1F, 1F);
        damager.getWorld().playEffect(victim.getEyeLocation(), Effect.STEP_SOUND, Material.REDSTONE_BLOCK);

        double damage = 8.0;

        if (victim.getHealth() - damage <= 0) {
            victim.damage(damage, damager);
        } else {
            event.setDamage(0);
            victim.setHealth(victim.getHealth() - damage);
        }
        damager.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 40, 1));
    }
}
