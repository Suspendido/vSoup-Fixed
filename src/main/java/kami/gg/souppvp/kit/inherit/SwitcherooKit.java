package kami.gg.souppvp.kit.inherit;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.kit.Kit;
import kami.gg.souppvp.kit.KitRarity;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.profile.ProfileState;
import kami.gg.souppvp.timer.Timer;
import kami.gg.souppvp.util.*;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SwitcherooKit extends Kit {

    @Override
    public String getName() {
        return "Switcheroo";
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
        return new ItemBuilder(Material.SNOW_BALL).build();
    }

    @Override
    public List<String> getDescription() {
        List<String> description = new ArrayList<>();
        description.add("&7Your main ability is being extremely sneaky. Catch your enemies off");
        description.add("&7guard and swap locations with them to give yourself a positional advantage");
        return description;
    }

    @Override
    public List<ItemStack> getCombatEquipments() {
        List<ItemStack> itemStacks = new ArrayList<>();
        itemStacks.add(new ItemBuilder(Material.DIAMOND_SWORD).enchantment(Enchantment.DAMAGE_ALL, 1).build());
        itemStacks.add(new ItemBuilder(Material.SNOW_BALL).name(CC.translate("&9Switcheroo")).amount(3).build());
        return itemStacks;
    }

    @Override
    public ItemStack[] getArmor() {
        return new ItemStack[]{
                new ItemBuilder(Material.CHAINMAIL_BOOTS).build(),
                new ItemBuilder(Material.CHAINMAIL_BOOTS).build(),
                new ItemBuilder(Material.DIAMOND_CHESTPLATE).build(),
                new ItemBuilder(Material.IRON_HELMET).build()
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
    public void execute(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        if (profile.isInEvent() || profile.getProfileState() == ProfileState.SPAWN) return;
        if (profile.getCurrentKit().equals(getName())){
            if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
                if (event.getPlayer().getItemInHand().isSimilar(this.getCombatEquipments().get(1))) {
                    if (profile.getProfileState() == ProfileState.SPAWN) {
                        player.sendMessage(CC.translate("&cYou can't do this in Spawn."));
                    }
                    if (SoupPvP.getInstance().getTimersHandler().hasTimer(player.getUniqueId(), "Switcheroo", true)) {
                        player.sendMessage(ChatColor.RED + "You can't use this for another " + ChatColor.YELLOW + DurationFormatter.getRemaining(SoupPvP.getInstance().getTimersHandler().getRemaining(player.getUniqueId(), "Switcheroo", true), true) + ChatColor.RED + ".");
                        event.setCancelled(true);
                        event.setUseItemInHand(Event.Result.DENY);
                        player.updateInventory();
                        return;
                    }
                    PlayerUtil.playSound(player, Sound.NOTE_PLING);
                } else {
                    return;
                }
                player.updateInventory();
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player damaged && event.getDamager() instanceof Snowball) {
            Entity damager = event.getDamager();
            Snowball snowball = (Snowball) damager;
            if (!(snowball.getShooter() instanceof Player shooter)) return;

            Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(shooter.getUniqueId());

            if (!profile.isInEvent()){
                if (profile.getCurrentKit().equals(getName())){
                    if (SoupPvP.getInstance().getSpawnHandler().getCuboid().contains(damaged)){
                        shooter.sendMessage(CC.translate("&cYou cannot switch players into spawn."));
                    }
                    SoupPvP.getInstance().getTimersHandler().addPlayerTimer(shooter.getUniqueId(), new Timer("Switcheroo", TimeUnit.SECONDS.toMillis(45)), true);
                    XPBarTimer.runXpBar(shooter, 45);
                    Location location = damaged.getLocation();
                    damaged.teleport(shooter);
                    shooter.teleport(location);
                    PlayerUtil.playSound(shooter, Sound.CHICKEN_EGG_POP);
                    PlayerUtil.playSound(damaged, Sound.CHICKEN_EGG_POP);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDeathEvent(PlayerDeathEvent event){
        Player killer = event.getEntity().getKiller();
        if (killer == null){
            return;
        }
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(killer.getUniqueId());
        if (!profile.isInEvent()){
            if (profile.getCurrentKit().equals(getName())){
                for (ItemStack itemStack : killer.getInventory().getContents()){
                    if (itemStack.isSimilar(this.getCombatEquipments().get(1))){
                        if (itemStack.getAmount() == 3){
                            return;
                        }
                    } else {
                        killer.getInventory().setItem(1, this.getCombatEquipments().get(1));
                    }
                }
            }
        }
    }

}
