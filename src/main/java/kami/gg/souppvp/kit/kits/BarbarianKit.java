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
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class BarbarianKit extends Kit {

    private static final String TIMER_NAME = "Silverfish Swarm";
    private static final int SILVERFISH_AMOUNT = 4;
    private static final int RADIUS = 10;

    private final ItemStack swarmItem =
            new ItemBuilder(Material.INK_SACK)
                    .name(CC.translate("&9Silverfish Swarm"))
                    .durability(6)
                    .build();

    private final ItemStack sword =
            new ItemBuilder(Material.STONE_SWORD)
                    .enchantment(Enchantment.DAMAGE_ALL, 2)
                    .enchantment(Enchantment.DURABILITY, 10)
                    .build();

    @Override
    public String getName() {
        return "Barbarian";
    }

    @Override
    public KitRarity getRarityType() {
        return KitRarity.LEGENDARY;
    }

    @Override
    public Integer getPrice() {
        return getRarityType().getPrice();
    }

    @Override
    public ItemStack getIcon() {
        return new ItemBuilder(Material.INK_SACK).durability(6).build();
    }

    @Override
    public List<String> getDescription() {
        return Arrays.asList(
                "&7Although you may be seen as vulnerable without",
                "&7armour, spawn swarms of silverfish to torture enemies",
                "&7and deal large heaps of damage."
        );
    }

    @Override
    public List<ItemStack> getCombatEquipments() {
        return Arrays.asList(
                sword,
                swarmItem
        );
    }

    @Override
    public ItemStack[] getArmor() {
        return new ItemStack[4];
    }

    @Override
    public List<PotionEffect> getPotionEffects() {
        return Collections.singletonList(
                new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 2)
        );
    }

    @Override
    public void onSelect(Player player) {}

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {

        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item == null) return;
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        if (profile.isInEvent() || profile.getProfileState() == ProfileState.SPAWN) return;

        // Must be Barbarian and right-clicking the swarm item
        if (!item.isSimilar(swarmItem)) return;
        if (!profile.getCurrentKit().equals(getName())) return;

        // Spawn check
        if (SoupPvP.getInstance().getSpawnHandler().getCuboid().contains(player)) {
            player.sendMessage(CC.translate("&cYou can't do this in Spawn."));
            return;
        }

        // Cooldown check
        if (SoupPvP.getInstance().getTimersHandler().hasTimer(player.getUniqueId(), TIMER_NAME, true)) {
            long remaining = SoupPvP.getInstance().getTimersHandler().getRemaining(player.getUniqueId(), TIMER_NAME, true);
            player.sendMessage(CC.translate("&cYou can't use this for another &e" + DurationFormatter.getRemaining(remaining, true) + "&c."));
            return;
        }

        // Slow effect
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * 6, 0), true);

        // Find closest target
        Player closest = null;
        double closestDistance = Double.MAX_VALUE;

        for (Entity entity : player.getNearbyEntities(RADIUS, RADIUS, RADIUS)) {
            if (!(entity instanceof Player p)) continue;

            if (p.getUniqueId().equals(player.getUniqueId())) continue;

            Profile pProfile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(p.getUniqueId());
            if (pProfile.getProfileState() == ProfileState.SPAWN) continue;

            double distance = player.getLocation().distance(p.getLocation());
            if (distance < closestDistance) {
                closestDistance = distance;
                closest = p;
            }
        }

        // Spawn silverfish swarm
        for (int i = 0; i < SILVERFISH_AMOUNT; i++) {
            Silverfish sf = (Silverfish) player.getWorld().spawnEntity(player.getLocation(), EntityType.SILVERFISH);

            sf.setMetadata("owner", new FixedMetadataValue(SoupPvP.getInstance(), player.getUniqueId().toString()));
            sf.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 10, 3));

            if (closest != null) sf.setTarget(closest);

            // Remove after 10 seconds
            Bukkit.getScheduler().runTaskLater(SoupPvP.getInstance(), () -> {
                if (sf.isValid()) sf.remove();
            }, 20L * 10);
        }

        player.playSound(player.getLocation(), Sound.SLIME_WALK2, 1F, 1F);

        // Add cooldown
        SoupPvP.getInstance().getTimersHandler().addPlayerTimer(
                player.getUniqueId(),
                new Timer(TIMER_NAME, TimeUnit.SECONDS.toMillis(35)),
                true
        );

        XPBarTimer.runXpBar(player, 35);
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        Entity victim = event.getEntity();

        if (victim instanceof Silverfish sf && damager instanceof Player player) {
            if (sf.hasMetadata("owner") && UUID.fromString(sf.getMetadata("owner").getFirst().asString()).equals(player.getUniqueId())) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "You cannot damage your own silverfish.");
            }
            return;
        }

        if (victim instanceof Player damaged && damager instanceof Silverfish sf) {
            event.setCancelled(true);
            if (sf.hasMetadata("owner")) {
                Player owner = Bukkit.getPlayer(UUID.fromString(sf.getMetadata("owner").getFirst().asString()));
                if (owner != null) damaged.damage(4, owner);
            }
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        event.setDroppedExp(0);
    }

    @EventHandler
    public void onEntityTarget(EntityTargetEvent event) {
        if (event.getEntity() instanceof Silverfish && event.getTarget() instanceof Player && event.getEntity().hasMetadata("owner") && UUID.fromString(event.getEntity().getMetadata("owner").getFirst().asString()).equals(event.getTarget().getUniqueId())) {
            event.setCancelled(true);
        }
    }
}
