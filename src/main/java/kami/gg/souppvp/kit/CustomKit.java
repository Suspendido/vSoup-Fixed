package kami.gg.souppvp.kit;

import kami.gg.souppvp.kit.ability.KitAbility;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.List;

public class CustomKit extends Kit {

    public CustomKit(String name, KitRarity rarity, Integer price, ItemStack icon, List<String> description, List<ItemStack> combatEquipments, ItemStack[] armor, List<PotionEffect> potionEffects, KitAbility primaryAbility, KitAbility secondaryAbility) {
        this.name = name;
        this.rarityType = rarity;
        this.price = price;
        this.icon = icon;
        this.description = description;
        this.combatEquipments = combatEquipments;
        this.armor = armor;
        this.potionEffects = potionEffects;
        this.primaryAbility = primaryAbility;
        this.secondaryAbility = secondaryAbility;
    }

    @Override
    public void onSelect(Player player) {
        // Custom kits no tienen lógica específica en onSelect
        // La lógica está en la ability si existe
    }
}
