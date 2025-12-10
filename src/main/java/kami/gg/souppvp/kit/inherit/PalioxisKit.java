package kami.gg.souppvp.kit.inherit;

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
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class PalioxisKit extends Kit {

    private final ItemStack ENDER_PEARL = new ItemStack(Material.ENDER_PEARL);

    @Override
    public String getName() {
        return "Palioxis";
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
        return new ItemBuilder(Material.ENDER_PEARL).build();
    }

    @Override
    public List<String> getDescription() {
        return List.of(
                "&7Like an enderman, teleport fast",
                "&7and efficiently to your destination."
        );
    }

    @Override
    public List<ItemStack> getCombatEquipments() {
        return List.of(
                new ItemBuilder(Material.DIAMOND_SWORD)
                        .enchantment(Enchantment.DAMAGE_ALL, 1)
                        .build(),
                new ItemBuilder(Material.ENDER_PEARL)
                        .amount(1)
                        .build()
        );
    }

    @Override
    public ItemStack[] getArmor() {
        return new ItemStack[]{
                new ItemBuilder(Material.IRON_BOOTS).build(),
                new ItemBuilder(Material.IRON_LEGGINGS).build(),
                new ItemBuilder(Material.IRON_CHESTPLATE).build(),
                new ItemBuilder(Material.LEATHER_HELMET)
                        .color(Color.BLACK)
                        .enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1)
                        .enchantment(Enchantment.DURABILITY, 10)
                        .build()
        };
    }

    @Override
    public List<PotionEffect> getPotionEffects() {
        return List.of(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
    }

    @Override
    public void onSelect(Player player) { }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();

        if (killer == null) return;

        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(killer.getUniqueId());
        if (profile.isInEvent() || profile.getProfileState() == ProfileState.SPAWN) return;

        Kit kit = SoupPvP.getInstance().getKitsHandler().getKitByName("Palioxis");
        Kit current = SoupPvP.getInstance().getKitsHandler().getKitByName(profile.getCurrentKit());

        if (current != kit) return;

        givePearl(killer);

        for (ItemStack armor : killer.getInventory().getArmorContents()) {
            if (armor != null) {
                short newDurability = (short) Math.max(0, armor.getDurability() - 10);
                armor.setDurability(newDurability);
            }
        }

        applySpeedBoost(killer);
        killer.updateInventory();
    }

    private void givePearl(Player killer) {
        if (killer.getInventory().firstEmpty() != -1) {
            killer.getInventory().addItem(ENDER_PEARL.clone());
            return;
        }

        for (int slot = 0; slot < killer.getInventory().getSize(); slot++) {
            ItemStack item = killer.getInventory().getItem(slot);
            if (item == null) continue;

            Material type = item.getType();

            if (type == Material.BOWL || type == Material.MUSHROOM_SOUP || type == Material.ENDER_PEARL) {
                killer.getInventory().setItem(slot, ENDER_PEARL.clone());
                return;
            }
        }
    }

    private void applySpeedBoost(Player killer) {
        int extra = 20 * 10; // 10 seconds

        PotionEffect current = killer.getActivePotionEffects().stream()
                .filter(pe -> pe.getType() == PotionEffectType.SPEED && pe.getAmplifier() == 2)
                .findFirst()
                .orElse(null);

        int duration = current != null ? current.getDuration() + extra : extra;

        killer.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, duration, 2), true);
    }
}
