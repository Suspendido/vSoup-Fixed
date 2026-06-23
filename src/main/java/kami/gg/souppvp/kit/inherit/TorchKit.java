package kami.gg.souppvp.kit.inherit;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.kit.Kit;
import kami.gg.souppvp.kit.KitRarity;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.util.*;
import kami.gg.souppvp.util.projectile.event.CustomProjectileHitEvent;
import kami.gg.souppvp.util.projectile.projectile.ItemProjectile;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TorchKit extends Kit {

    @Override
    public String getName() {
        return "Torch";
    }

    @Override
    public KitRarity getRarityType() {
        return KitRarity.RARE;
    }

    @Override
    public Integer getPrice() {
        return getRarityType().getPrice();
    }

    @Override
    public ItemStack getIcon() {
        return new ItemBuilder(Material.BLAZE_POWDER).build();
    }

    @Override
    public List<String> getDescription() {
        List<String> description = new ArrayList<>();
        description.add("&7Set enemies standing in front of you fire and burn them to crisps.");
        description.add("&7With your dragon breath, scare off your enemies and set them fleeing.");
        return description;
    }

    @Override
    public List<ItemStack> getCombatEquipments() {
        List<ItemStack> itemStacks = new ArrayList<>();
        itemStacks.add(new ItemBuilder(Material.DIAMOND_SWORD).enchantment(Enchantment.DAMAGE_ALL, 1).build());
        itemStacks.add(new ItemBuilder(Material.BLAZE_POWDER).name("&cDragon Breath").build());
        return itemStacks;
    }

    @Override
    public ItemStack[] getArmor() {
        return new ItemStack[]{
                new ItemBuilder(Material.LEATHER_BOOTS).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1).build(),
                new ItemBuilder(Material.IRON_LEGGINGS).build(),
                new ItemBuilder(Material.IRON_CHESTPLATE).build(),
                new ItemBuilder(Material.LEATHER_HELMET).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1).build()
        };
    }

    @Override
    public List<PotionEffect> getPotionEffects() {
        List<PotionEffect> potionEffects = new ArrayList<>();
        potionEffects.add(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0));
        return potionEffects;
    }

    @Override
    public void onSelect(Player player) {

    }

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event){
        Player player = event.getPlayer();
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        if (!profile.getCurrentKit().equals(getName())) return;

        if (event.getPlayer().getItemInHand().isSimilar(this.getCombatEquipments().get(1)) && (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK))) {
            event.setCancelled(true);
            player.updateInventory();

            if (profile.isInEvent() || isInSpawn(player, profile)) {
                player.sendMessage(CC.t("&cYou can't use this while in spawn."));
                return;
            }

            if (hasTimer(player.getUniqueId())) {
                player.sendMessage(CC.t("&cYou can't use this for another &e" + DurationFormatter.getRemaining(getRemaining(player.getUniqueId()), true) + "&c."));
                return;
            }

            addTimer(player.getUniqueId(), TimeUnit.SECONDS.toMillis(45));
            XPBarTimer.runXpBar(player, 45);
            PlayerUtil.playSound(player, Sound.ENDERDRAGON_GROWL, 1.0);

            for (Entity entity : player.getNearbyEntities(5, 5, 5)) {
                if (entity instanceof Player) {
                    PlayerUtil.playSound((Player) entity, Sound.ENDERDRAGON_GROWL, 1.0);
                }
            }

            new BukkitRunnable() {
                int i = 0;
                @Override
                public void run() {
                    if (i >= 20) {
                        cancel();
                    }
                    ++i;
                    ItemProjectile projectile = new ItemProjectile("DRAGON_BREATH", player, new ItemStack(Material.BLAZE_POWDER), 0.5f);
                }
            }.runTaskTimer(SoupPvP.getInstance(), 2L, 2L);
        }
    }

    @EventHandler
    public void onHit(CustomProjectileHitEvent event) {
        if (SoupPvP.getInstance().getSpawnHandler().getCuboid().contains(event.getHitEntity())) return;
        if (event.getProjectile().getProjectileName().equals("DRAGON_BREATH") && event.getHitEntity() instanceof Player && event.getHitEntity() != event.getProjectile().getShooter()) {
            event.getHitEntity().setFireTicks(40);
        }
    }

}
