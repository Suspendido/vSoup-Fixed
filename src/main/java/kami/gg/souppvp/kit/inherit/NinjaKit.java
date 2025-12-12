package kami.gg.souppvp.kit.inherit;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.kit.Kit;
import kami.gg.souppvp.kit.KitRarity;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.profile.ProfileState;
import kami.gg.souppvp.timer.Timer;
import kami.gg.souppvp.util.*;
import kami.gg.souppvp.util.projectile.event.CustomProjectileHitEvent;
import kami.gg.souppvp.util.projectile.projectile.ItemProjectile;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class NinjaKit extends Kit {

    private final String TIMER_NAME = "Shuriken";
    private final int SHURIKEN_COOLDOWN = 10;
    private final ItemStack SHURIKEN_ITEM = new ItemBuilder(Material.NETHER_STAR).name(CC.translate("&bShuriken")).amount(4).build();

    @Override
    public String getName() {
        return "Ninja";
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
        return new ItemBuilder(Material.NETHER_STAR).build();
    }

    @Override
    public List<String> getDescription() {
        return List.of(
                "&7Throw shurikens, blinding your enemies.",
                "&7Gain +30 armor durability per kill!"
        );
    }

    @Override
    public List<ItemStack> getCombatEquipments() {
        List<ItemStack> items = new ArrayList<>();
        items.add(new ItemBuilder(Material.IRON_SWORD).enchantment(Enchantment.DAMAGE_ALL, 1).build());
        items.add(SHURIKEN_ITEM.clone());
        return items;
    }

    @Override
    public ItemStack[] getArmor() {
        return new ItemStack[]{
                createArmorPiece(Material.LEATHER_BOOTS),
                createArmorPiece(Material.LEATHER_LEGGINGS),
                createArmorPiece(Material.LEATHER_CHESTPLATE),
                createArmorPiece(Material.LEATHER_HELMET)
        };
    }

    private ItemStack createArmorPiece(Material type) {
        return new ItemBuilder(type)
                .color(Color.BLACK)
                .enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                .enchantment(Enchantment.DURABILITY, 10)
                .build();
    }

    @Override
    public List<PotionEffect> getPotionEffects() {
        return List.of(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
    }

    @Override
    public void onSelect(Player player) {

    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        if (killer == null) return;

        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(killer.getUniqueId());

        if (!profile.getCurrentKit().equals(getName()) || profile.isInEvent() || profile.getProfileState() == ProfileState.SPAWN) return;

        for (ItemStack armor : killer.getInventory().getArmorContents()) {
            if (armor != null) {
                armor.setDurability((short) Math.max(0, armor.getDurability() - 30));
            }
        }

        for (ItemStack content : killer.getInventory()) {
            if (content != null && content.getType() == Material.NETHER_STAR) {
                content.setAmount(4);
                break;
            }
        }

        killer.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "JUTSU! " + ChatColor.YELLOW + "You earned an extra shuriken star!");
        killer.updateInventory();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());

        if (!profile.getCurrentKit().equals(getName())) return;
        if (profile.isInEvent() || profile.getProfileState() == ProfileState.SPAWN) return;
        if (item == null || !item.isSimilar(SHURIKEN_ITEM)) return;

        if (!(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK))
            return;

        if (SoupPvP.getInstance().getTimersHandler().hasTimer(player.getUniqueId(), TIMER_NAME, true)) {
            long remaining = SoupPvP.getInstance().getTimersHandler().getRemaining(player.getUniqueId(), TIMER_NAME, true);
            player.sendMessage(ChatColor.RED + "You can't use this for another " + ChatColor.YELLOW + DurationFormatter.getRemaining(remaining, true) + ChatColor.RED + ".");
            return;
        }

        // Launch shuriken
        player.getWorld().playSound(player.getLocation(), Sound.WITHER_SHOOT, 1F, 1F);

        ItemProjectile projectile = new ItemProjectile("SHURIKEN", player, new ItemStack(Material.NETHER_STAR), 2);
        projectile.addTypedRunnable(o -> o.getEntity().getWorld().spigot().playEffect(o.getEntity().getLocation(), Effect.HAPPY_VILLAGER));

        // Remove 1 shuriken
        item.setAmount(item.getAmount() - 1);
        if (item.getAmount() <= 0) player.setItemInHand(null);

        // Cooldown
        SoupPvP.getInstance().getTimersHandler().addPlayerTimer(
                player.getUniqueId(),
                new Timer(TIMER_NAME, TimeUnit.SECONDS.toMillis(SHURIKEN_COOLDOWN)),
                true
        );

        XPBarTimer.runXpBar(player, SHURIKEN_COOLDOWN);
        player.updateInventory();
    }

    @EventHandler
    public void onHit(CustomProjectileHitEvent event) {
        if (!event.getProjectile().getProjectileName().equals("SHURIKEN")) return;
        if (event.getHitType() != CustomProjectileHitEvent.HitType.ENTITY) return;
        if (!(event.getHitEntity() instanceof Player target)) return;
        if (target == event.getProjectile().getShooter()) return;

        if (SoupPvP.getInstance().getSpawnHandler().getCuboid().contains(target)) return;

        // Effects
        target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 1));
        target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 1));

        target.damage(4.0);
        target.playSound(target.getLocation(), Sound.ANVIL_LAND, 1f, 1f);
    }
}
