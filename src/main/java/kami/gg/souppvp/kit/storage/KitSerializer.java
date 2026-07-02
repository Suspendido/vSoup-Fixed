package kami.gg.souppvp.kit.storage;

import kami.gg.souppvp.kit.CustomKit;
import kami.gg.souppvp.kit.Kit;
import kami.gg.souppvp.kit.KitRarity;
import kami.gg.souppvp.kit.ability.KitAbility;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;

public class KitSerializer {

    public static void serializeKit(Kit kit, ConfigurationSection section) {
        section.set("name", kit.getName());
        section.set("rarity", kit.getRarityType().name());
        section.set("price", kit.getPrice());
        section.set("icon", kit.getIcon());
        section.set("description", kit.getDescription());
        section.set("enabled", kit.isEnabled());
        section.set("combatEquipments", kit.getCombatEquipments());
        section.set("armor", kit.getArmor());
        section.set("potionEffects", kit.getPotionEffects());

        if (kit.getPrimaryAbility() != null) {
            section.set("primaryAbility", kit.getPrimaryAbility().getName());
        } else {
            section.set("primaryAbility", "None");
        }

        if (kit.getSecondaryAbility() != null) {
            section.set("secondaryAbility", kit.getSecondaryAbility().getName());
        } else {
            section.set("secondaryAbility", "None");
        }
    }

    public static CustomKit deserializeKit(ConfigurationSection section, KitAbility primaryAbility, KitAbility secondaryAbility) {
        String name = section.getString("name");
        KitRarity rarity = KitRarity.valueOf(section.getString("rarity"));
        Integer price = section.getInt("price");
        ItemStack icon = section.getItemStack("icon");
        if (icon != null) {
            icon = icon.clone(); // Clone icon to avoid memory leak
        }

        @SuppressWarnings("unchecked")
        List<String> description = new ArrayList<>((List<String>) section.getList("description", new ArrayList<>()));
        boolean enabled = section.getBoolean("enabled", true);

        @SuppressWarnings("unchecked")
        List<ItemStack> rawCombatEquipments = (List<ItemStack>) section.getList("combatEquipments", new ArrayList<>());
        List<ItemStack> combatEquipments = new ArrayList<>();
        for (ItemStack item : rawCombatEquipments) {
            if (item != null) {
                combatEquipments.add(item.clone()); // Clone each item to avoid memory leak
            }
        }

        @SuppressWarnings("unchecked")
        List<ItemStack> armorList = (List<ItemStack>) section.getList("armor", new ArrayList<>());
        ItemStack[] armor = new ItemStack[4];
        for (int i = 0; i < Math.min(4, armorList.size()); i++) {
            if (armorList.get(i) != null) {
                armor[i] = armorList.get(i).clone(); // Clone each armor piece to avoid memory leak
            }
        }

        @SuppressWarnings("unchecked")
        List<PotionEffect> rawPotionEffects = (List<PotionEffect>) section.getList("potionEffects", new ArrayList<>());
        List<PotionEffect> potionEffects = new ArrayList<>();
        for (PotionEffect effect : rawPotionEffects) {
            if (effect != null) {
                potionEffects.add(new PotionEffect(effect.getType(), effect.getDuration(), effect.getAmplifier(), effect.isAmbient(), effect.hasParticles())); // Clone potion effect
            }
        }

        CustomKit kit = new CustomKit(name, rarity, price, icon, description, combatEquipments, armor, potionEffects, primaryAbility, secondaryAbility);
        kit.setEnabled(enabled);
        return kit;
    }
}
