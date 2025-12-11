package kami.gg.souppvp.kit.kits;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.kit.Kit;
import kami.gg.souppvp.kit.KitRarity;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.profile.ProfileState;
import kami.gg.souppvp.util.ItemBuilder;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class CactusKit extends Kit {

    private static final Random RANDOM = new Random();
    private static final double REFLECT_CHANCE = 0.25;
    private static final double REFLECT_PERCENTAGE = 0.25;

    @Override
    public String getName() {
        return "Cactus";
    }

    @Override
    public KitRarity getRarityType() {
        return KitRarity.MYTHICAL;
    }

    @Override
    public Integer getPrice() {
        return getRarityType().getPrice();
    }

    @Override
    public ItemStack getIcon() {
        return new ItemBuilder(Material.CACTUS).build();
    }

    @Override
    public List<String> getDescription() {
        return Arrays.asList(
                "&7Become a cactus and reflect a percentage of the",
                "&7damage from enemies back to themselves."
        );
    }

    @Override
    public List<ItemStack> getCombatEquipments() {
        return Collections.singletonList(new ItemBuilder(Material.DIAMOND_SWORD).enchantment(Enchantment.DAMAGE_ALL, 1).build()
        );
    }

    @Override
    public ItemStack[] getArmor() {
        return new ItemStack[]{
                armorPiece(Material.LEATHER_BOOTS),
                armorPiece(Material.LEATHER_LEGGINGS),
                armorPiece(Material.LEATHER_CHESTPLATE),
                armorPiece(Material.LEATHER_HELMET)
        };
    }

    private ItemStack armorPiece(Material material) {
        return new ItemBuilder(material).color(Color.GREEN).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2).enchantment(Enchantment.DURABILITY, 10).build();
    }

    @Override
    public List<PotionEffect> getPotionEffects() {
        return Arrays.asList(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0), new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0));
    }

    @Override
    public void onSelect(Player player) {

    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player target)) return;
        if (!(event.getDamager() instanceof Player damager)) return;

        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(target.getUniqueId());
        if (profile.isInEvent() || profile.getProfileState() == ProfileState.SPAWN) return;

        if (SoupPvP.getInstance().getSpawnHandler().getCuboid().contains(target)) {
            event.setCancelled(true);
            return;
        }

        if (!profile.getCurrentKit().equals(getName())) return;

        if (RANDOM.nextDouble() <= REFLECT_CHANCE) {
            double reflected = event.getDamage() * REFLECT_PERCENTAGE;
            damager.damage(reflected);
        }
    }
}