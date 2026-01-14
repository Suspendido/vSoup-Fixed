package kami.gg.souppvp.kit.kits;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.kit.Kit;
import kami.gg.souppvp.kit.KitRarity;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.profile.ProfileState;
import kami.gg.souppvp.timer.Timer;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.DurationFormatter;
import kami.gg.souppvp.util.ItemBuilder;
import kami.gg.souppvp.util.XPBarTimer;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class RogueKit extends Kit {

    private final ItemStack BACKSTAB_DAGGER = new ItemBuilder(Material.GOLD_SWORD)
            .name(CC.translate("&6Backstab Dagger"))
            .enchantment(Enchantment.DURABILITY, 10)
            .build();

    @Override
    public String getName() {
        return "Rogue";
    }

    @Override
    public KitRarity getRarityType() {
        return KitRarity.ULTIMATE;
    }

    @Override
    public Integer getPrice() {
        return getRarityType().getPrice();
    }

    @Override
    public ItemStack getIcon() {
        return new ItemBuilder(Material.GOLD_SWORD).build();
    }

    @Override
    public List<String> getDescription() {
        return List.of(
                "&7Go rogue and backstab enemies.",
                "&7A precise backstab deals massive damage."
        );
    }

    @Override
    public List<ItemStack> getCombatEquipments() {
        return List.of(
                new ItemBuilder(Material.IRON_SWORD)
                        .enchantment(Enchantment.DAMAGE_ALL, 1)
                        .enchantment(Enchantment.DURABILITY, 3)
                        .build(),

                BACKSTAB_DAGGER
        );
    }

    @Override
    public ItemStack[] getArmor() {
        return new ItemStack[]{
                new ItemBuilder(Material.CHAINMAIL_BOOTS).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1).build(),
                new ItemBuilder(Material.CHAINMAIL_LEGGINGS).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1).build(),
                new ItemBuilder(Material.IRON_CHESTPLATE).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1).build(),
                new ItemBuilder(Material.LEATHER_HELMET).color(Color.BLACK).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1).enchantment(Enchantment.DURABILITY, 3).build()
        };
    }

    @Override
    public List<PotionEffect> getPotionEffects() {
        return List.of(
                new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1),
                new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0)
        );
    }

    @Override
    public void onSelect(Player player) {

    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {

        if (!(event.getDamager() instanceof Player damager)) return;
        if (!(event.getEntity() instanceof Player victim)) return;

        Profile attacker = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(damager.getUniqueId());
        if (attacker == null) return;

        if (attacker.isInEvent() || attacker.getProfileState() == ProfileState.SPAWN) return;
        if (!attacker.getCurrentKit().equals(getName())) return;

        ItemStack hand = damager.getItemInHand();
        if (hand == null || hand.getType() != Material.GOLD_SWORD) return;

        final var timers = SoupPvP.getInstance().getTimersHandler();
        final String timerId = "Back Stabber";

        if (timers.hasTimer(damager.getUniqueId(), timerId, true)) {
            damager.sendMessage(CC.translate("&cYou can't use this for another " + DurationFormatter.getRemaining(timers.getRemaining(damager.getUniqueId(), timerId, true), true) + "&c."));
            return;
        }

        if (SoupPvP.getInstance().getSpawnHandler().getCuboid().contains(damager.getLocation())) {
            damager.sendMessage(CC.translate("&cYou can't do this in spawn."));
            return;
        }

        Vector attackerDir = damager.getLocation().getDirection().setY(0).normalize();
        Vector victimDir = victim.getLocation().getDirection().setY(0).normalize();

        double dot = attackerDir.dot(victimDir);
        boolean isBehind = dot > 0.15;

        if (!isBehind) {
            damager.sendMessage(CC.translate("&cBackstab failed!"));
            return;
        }

        timers.addPlayerTimer(damager.getUniqueId(), new Timer(timerId, TimeUnit.SECONDS.toMillis(30)), true);
        XPBarTimer.runXpBar(damager, 30);

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
