package kami.gg.souppvp.kit.inherit;

import com.google.common.collect.Sets;
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
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class KangarooKit extends Kit {

    private final Set<UUID> jumpingUsers = Sets.newHashSet();

    @Override
    public String getName() {
        return "Kangaroo";
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
        return new ItemBuilder(Material.FIREWORK).build();
    }

    @Override
    public List<String> getDescription() {
        return Arrays.asList(
                "&7Similarly to wild life kangaroos, use your kangaroo boost ability",
                "&7to jump over enemies and escape dangerous situations easily."
        );
    }

    @Override
    public List<ItemStack> getCombatEquipments() {
        return Arrays.asList(
                new ItemBuilder(Material.DIAMOND_SWORD).build(),
                new ItemBuilder(Material.FIREWORK).name(CC.translate("&cKangaroo Boost")).build()
        );
    }

    @Override
    public ItemStack[] getArmor() {
        return new ItemStack[]{
                new ItemBuilder(Material.IRON_BOOTS).build(),
                new ItemBuilder(Material.GOLD_LEGGINGS).enchantment(Enchantment.DURABILITY, 4).build(),
                new ItemBuilder(Material.IRON_CHESTPLATE).build(),
                new ItemBuilder(Material.IRON_HELMET).build()
        };
    }

    @Override
    public List<PotionEffect> getPotionEffects() {
        return Collections.singletonList(
                new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1)
        );
    }

    @Override
    public void onSelect(Player player) { }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (event.getTo().getBlockX() == event.getFrom().getBlockX()
                && event.getTo().getBlockY() == event.getFrom().getBlockY()
                && event.getTo().getBlockZ() == event.getFrom().getBlockZ()) {
            return;
        }

        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        if (profile == null || !this.equals(profile.getCurrentKit())) return;

        if (player.isOnGround()) {
            jumpingUsers.remove(player.getUniqueId());
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        if (event.getCause() == EntityDamageEvent.DamageCause.FALL && jumpingUsers.contains(player.getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());

        if (profile == null || profile.isInEvent() || profile.getProfileState() == ProfileState.SPAWN || !this.equals(profile.getCurrentKit())) {
            return;
        }

        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) return;

        ItemStack item = event.getItem();
        ItemStack boostItem = getCombatEquipments().get(1);

        if (item == null || !item.isSimilar(boostItem)) return;

        event.setCancelled(true);

        UUID uuid = player.getUniqueId();

        if (SoupPvP.getInstance().getTimersHandler().hasTimer(uuid, "Kangaroo", true)) {
            long remain = SoupPvP.getInstance().getTimersHandler().getRemaining(uuid, "Kangaroo", true);
            player.sendMessage(CC.translate("&cYou can't use this for another &e" + DurationFormatter.getRemaining(remain, true) + "&c."));
            return;
        }

        player.setVelocity(player.getEyeLocation().getDirection().multiply(1.5).setY(1.25));

        jumpingUsers.add(uuid);
        player.playSound(player.getLocation(), Sound.BAT_TAKEOFF, 1F, 1F);

        SoupPvP.getInstance().getTimersHandler().addPlayerTimer(
                uuid,
                new Timer("Kangaroo", TimeUnit.SECONDS.toMillis(10)),
                true
        );

        XPBarTimer.runXpBar(player, 10);
    }
}
