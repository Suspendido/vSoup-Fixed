package kami.gg.souppvp.kit.kits;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.kit.Kit;
import kami.gg.souppvp.kit.KitRarity;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.profile.ProfileState;
import kami.gg.souppvp.util.*;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class MelonKit extends Kit {

    // Pre-cached items to avoid recreating objects every click
    private final ItemStack melonTossItem =
            new ItemBuilder(Material.SPECKLED_MELON).name("&2Melon Toss").build();

    private final List<ItemStack> combatItems = Arrays.asList(
            new ItemBuilder(Material.MELON).enchantment(Enchantment.DAMAGE_ALL, 4).build(),
            melonTossItem
    );

    @Override
    public String getName() {
        return "Melon";
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
        return new ItemBuilder(Material.MELON).build();
    }

    @Override
    public List<String> getDescription() {
        return Arrays.asList(
                "&7Chop your enemies up with your melon and shoot",
                "&7your melon tosser and toss your enemies into",
                "&7the air."
        );
    }

    @Override
    public List<ItemStack> getCombatEquipments() {
        return combatItems;
    }

    @Override
    public ItemStack[] getArmor() {
        return new ItemStack[]{
                new ItemBuilder(Material.IRON_BOOTS).build(),
                new ItemBuilder(Material.IRON_LEGGINGS).build(),
                new ItemBuilder(Material.LEATHER_CHESTPLATE).color(Color.GREEN)
                        .enchantment(Enchantment.DURABILITY, 10).build(),
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
    public void onSelect(Player player) {
        // nothing
    }

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());

        if (profile.isInEvent() || profile.getProfileState() == ProfileState.SPAWN) return;
        if (!profile.getCurrentKit().equals(getName())) return;

        ItemStack item = event.getItem();
        if (item == null || !item.isSimilar(melonTossItem)) return;

        if (!(event.getAction() == Action.RIGHT_CLICK_AIR ||
                event.getAction() == Action.RIGHT_CLICK_BLOCK)) return;

        event.setCancelled(true);
        player.updateInventory();

        if (hasTimer(player.getUniqueId())) {
            player.sendMessage(CC.t("&cYou can't use this for another &e" + DurationFormatter.getRemaining(getRemaining(player.getUniqueId()), true) + "&c."));
            return;
        }

        if (SoupPvP.getInstance().getSpawnHandler().getCuboid().contains(BlockUtil.getTargetBlock(player, 20).getLocation())) {
            player.sendMessage(CC.t("&cYou can't do this in spawn."));
            return;
        }

        addTimer(player.getUniqueId(), TimeUnit.SECONDS.toMillis(30));
        XPBarTimer.runXpBar(player, 30);
        PlayerUtil.playSound(player, Sound.EXPLODE, 1.0);
        FallingBlock block = player.getWorld().spawnFallingBlock(player.getEyeLocation(), Material.MELON_BLOCK, (byte) 0);

        block.setDropItem(false);
        block.setMetadata("melon_tosser", new FixedMetadataValue(SoupPvP.getInstance(), player.getUniqueId()));

        block.setVelocity(player.getEyeLocation().getDirection().multiply(2.5).add(new Vector(0, 0.3, 0)));

        // search for players hit
        new BukkitRunnable() {
            @Override
            public void run() {
                if (block.isDead() || !block.isValid() || !player.isOnline()) {
                    cancel();
                    return;
                }

                for (Entity entity : block.getNearbyEntities(3, 3, 3)) {
                    if (!(entity instanceof Player other)) continue;

                    if (other.getUniqueId().equals(player.getUniqueId())) continue;

                    Profile targetProfile = SoupPvP.getInstance().getProfilesHandler()
                            .getProfileByUUID(other.getUniqueId());

                    if (targetProfile.getProfileState() == ProfileState.SPAWN) continue;

                    block.remove();
                    cancel();

                    Vector velocity = other.getLocation().toVector()
                            .subtract(player.getLocation().toVector())
                            .multiply(0.3)
                            .setY(1.5);

                    other.setVelocity(velocity);
                    break;
                }
            }
        }.runTaskTimer(SoupPvP.getInstance(), 2L, 2L);
    }

    @EventHandler
    public void onEntityChangeBlockEvent(EntityChangeBlockEvent event) {
        if (event.getEntityType() == EntityType.FALLING_BLOCK && event.getEntity().hasMetadata("melon_tosser")) {
            event.setCancelled(true);
            event.getEntity().remove();
        }
    }
}