package kami.gg.souppvp.kit.kits;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.kit.Kit;
import kami.gg.souppvp.kit.KitRarity;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.profile.ProfileState;
import kami.gg.souppvp.timer.Timer;
import kami.gg.souppvp.util.*;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class EskimoKit extends Kit {

    private final ItemStack ICE_ITEM = new ItemBuilder(Material.PACKED_ICE).name(CC.translate("&5Ice Dome")).build();

    @Override
    public String getName() {
        return "Eskimo";
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
        return new ItemBuilder(Material.PACKED_ICE).build();
    }

    @Override
    public List<String> getDescription() {
        return List.of(
                "&7Whenever possible, spawn in an ice dome like an igloo to",
                "&7trap enemies and gain a higher chance of killing them."
        );
    }

    @Override
    public List<ItemStack> getCombatEquipments() {
        return List.of(
                new ItemBuilder(Material.IRON_SWORD)
                        .enchantment(Enchantment.DAMAGE_ALL, 1)
                        .enchantment(Enchantment.DURABILITY, 3)
                        .build(),

                ICE_ITEM
        );
    }

    @Override
    public ItemStack[] getArmor() {
        return new ItemStack[]{
                new ItemBuilder(Material.LEATHER_BOOTS)
                        .enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                        .enchantment(Enchantment.DURABILITY, 10).build(),

                new ItemBuilder(Material.LEATHER_LEGGINGS)
                        .enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                        .enchantment(Enchantment.DURABILITY, 10).build(),

                new ItemBuilder(Material.LEATHER_CHESTPLATE)
                        .enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                        .enchantment(Enchantment.DURABILITY, 10).build(),

                new ItemBuilder(Material.LEATHER_HELMET)
                        .enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                        .enchantment(Enchantment.DURABILITY, 10).build()
        };
    }

    @Override
    public List<PotionEffect> getPotionEffects() {
        return List.of(
                new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0),
                new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0)
        );
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

        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) return;

        ItemStack hand = player.getItemInHand();
        if (!hand.isSimilar(ICE_ITEM)) return;

        event.setCancelled(true);

        if (SoupPvP.getInstance().getSpawnHandler().getCuboid().contains(player.getLocation())) {
            player.sendMessage(CC.translate("&cYou can't do this in spawn."));
            return;
        }

        if (SoupPvP.getInstance().getTimersHandler().hasTimer(player.getUniqueId(), "Ice Dome", true)) {
            long remaining = SoupPvP.getInstance().getTimersHandler().getRemaining(player.getUniqueId(), "Ice Dome", true);
            player.sendMessage(CC.translate("&cYou can't use this for another &e" + DurationFormatter.getRemaining(remaining, true) + "&c."));
            return;
        }

        SoupPvP.getInstance().getTimersHandler().addPlayerTimer(
                player.getUniqueId(),
                new Timer("Ice Dome", TimeUnit.SECONDS.toMillis(30)),
                true
        );

        XPBarTimer.runXpBar(player, 30);
        player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 5 * 20, 1));
        BlockUtil.generateTemporarySphere(player.getLocation().add(0, -1, 0), 5, true, Material.ICE, 5);
    }
}
