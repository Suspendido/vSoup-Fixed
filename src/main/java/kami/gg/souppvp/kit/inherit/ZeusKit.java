package kami.gg.souppvp.kit.inherit;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.kit.Kit;
import kami.gg.souppvp.kit.KitRarity;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.profile.ProfileState;
import kami.gg.souppvp.timer.Timer;
import kami.gg.souppvp.util.*;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ZeusKit extends Kit {

    @Override
    public String getName() {
        return "Zeus";
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
        return new ItemBuilder(Material.GLOWSTONE_DUST).build();
    }

    @Override
    public List<String> getDescription() {
        List<String> description = new ArrayList<>();
        description.add("&7With the immense ability of a god, you possess control over lightning,");
        description.add("&7striking anyone who stands in front of you.");
        return description;
    }

    @Override
    public List<ItemStack> getCombatEquipments() {
        List<ItemStack> itemStacks = new ArrayList<>();
        itemStacks.add(new ItemBuilder(Material.DIAMOND_SWORD).build());
        itemStacks.add(new ItemBuilder(Material.GLOWSTONE_DUST).name(CC.translate("&6Lightning Bolt")).build());
        return itemStacks;
    }

    @Override
    public ItemStack[] getArmor() {
        return new ItemStack[]{
                new ItemBuilder(Material.LEATHER_BOOTS).color(Color.YELLOW).enchantment(Enchantment.DURABILITY, 20).build(),
                new ItemBuilder(Material.IRON_LEGGINGS).build(),
                new ItemBuilder(Material.IRON_CHESTPLATE).build(),
                new ItemBuilder(Material.LEATHER_HELMET).color(Color.YELLOW).enchantment(Enchantment.PROTECTION_FIRE, 1).build()
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

        if (profile.isInEvent() || profile.getProfileState() == ProfileState.SPAWN) return;
        if (!profile.getCurrentKit().equals(getName())) return;
        if (!event.getAction().equals(Action.RIGHT_CLICK_AIR) && !event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
        if (!player.getItemInHand().isSimilar(this.getCombatEquipments().get(1))) return;

        event.setCancelled(true);
        player.updateInventory();

        if (SoupPvP.getInstance().getTimersHandler().hasTimer(player.getUniqueId(), "Lightning Bolt", true)) {
            player.sendMessage(CC.translate("&cYou can't use this for another &e" + DurationFormatter.getRemaining(SoupPvP.getInstance().getTimersHandler().getRemaining(player.getUniqueId(), "Lightning Bolt", true), true) + "&c."));
            return;
        }

        if (SoupPvP.getInstance().getSpawnHandler().getCuboid().contains(player.getLocation())) {
            player.sendMessage(CC.translate("&cYou can't do this in spawn."));
            return;
        }

        boolean hit = false;
        for (Player target : player.getWorld().getPlayers()) {
            if (target == player) continue;
            Profile tProfile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(target.getUniqueId());

            if (tProfile.getProfileState() == ProfileState.SPAWN) continue;
            if (target.getLocation().distance(player.getLocation()) > 10) continue;

            hit = true;

            target.damage(8, player);
            player.getWorld().strikeLightningEffect(target.getLocation());
        }

        if (!hit) {
            player.sendMessage(CC.translate("&cNo players nearby."));
        }

        SoupPvP.getInstance().getTimersHandler().addPlayerTimer(
                player.getUniqueId(),
                new Timer("Lightning Bolt", TimeUnit.SECONDS.toMillis(45)),
                true
        );

        XPBarTimer.runXpBar(player, 45);
        PlayerUtil.playSound(player, Sound.AMBIENCE_THUNDER);
    }
}
