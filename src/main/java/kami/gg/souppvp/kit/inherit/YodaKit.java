package kami.gg.souppvp.kit.inherit;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.kit.Kit;
import kami.gg.souppvp.kit.KitRarity;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.profile.ProfileState;
import kami.gg.souppvp.util.*;
import org.bukkit.*;
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
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class YodaKit extends Kit {

    @Override
    public String getName() {
        return "Yoda";
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
        return new ItemBuilder(Material.INK_SACK).durability(2).build();
    }

    @Override
    public List<String> getDescription() {
        List<String> description = new ArrayList<>();
        description.add("&7With the capabilities of Yoda, the jet master,");
        description.add("&7create a pulling force to pull enemies towards you.");
        return description;
    }

    @Override
    public List<ItemStack> getCombatEquipments() {
        List<ItemStack> itemStacks = new ArrayList<>();
        itemStacks.add(new ItemBuilder(Material.DIAMOND_SWORD).build());
        itemStacks.add(new ItemBuilder(Material.INK_SACK).durability(2).name("&aThe Force").build());
        return itemStacks;
    }

    @Override
    public ItemStack[] getArmor() {
        return new ItemStack[]{
                new ItemBuilder(Material.IRON_BOOTS).build(),
                new ItemBuilder(Material.IRON_LEGGINGS).build(),
                new ItemBuilder(Material.IRON_CHESTPLATE).build(),
                new ItemBuilder(Material.LEATHER_HELMET).color(Color.GREEN).enchantment(Enchantment.DURABILITY, 3).build()
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
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
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
            PlayerUtil.playSound(player, Sound.ENDERMAN_STARE, 1.0);

            for (Entity entity : player.getNearbyEntities(10, 10, 10)) {
                if (entity instanceof Player) {
                    PlayerUtil.playSound((Player) entity, Sound.ENDERMAN_STARE, 1.0);
                    if (SoupPvP.getInstance().getSpawnHandler().getCuboid().contains(entity)) return;
                    entity.sendMessage(CC.t("&cYou are being pulled by The Force."));
                }
            }

            Location location = player.getLocation();
            new BukkitRunnable() {
                int i = 0;
                @Override
                public void run() {
                    if(i >= 50) {
                        cancel();
                    }
                    ++i;
                    for (Player targets : Bukkit.getOnlinePlayers()) {
                        if (targets.getLocation().distance(location) <= 10 && targets != player) {
                            Profile targetsprofile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(targets.getUniqueId());
                            if (targetsprofile.getProfileState() == ProfileState.SPAWN) return;
                            moveToward(targets, location);
                        }
                    }
                }
            }.runTaskTimer(SoupPvP.getInstance(), 2L, 2L);
        }
    }


    private void moveToward(Entity entity, Location to) {
        Location loc = entity.getLocation();
        double x = loc.getX() - to.getX();
        double y = loc.getY() - to.getY();
        double z = loc.getZ() - to.getZ();
        Vector velocity = new Vector(x, y, z).normalize().multiply(-0.5);
        entity.setVelocity(velocity);
    }

}
