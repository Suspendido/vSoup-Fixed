package kami.gg.souppvp.killstreak;

import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class KillstreakReward {

    public static ItemStack GOLDEN_APPLES = new ItemBuilder(Material.GOLDEN_APPLE).amount(8).build();
    public static ItemStack FIRE_RESISTANCE_POTION = new ItemBuilder(Material.POTION).durability(8259).build();
    public static ItemStack GRANDMA_SOUP = new ItemBuilder(Material.MUSHROOM_SOUP).enchantment(Enchantment.DURABILITY, 1).name(CC.t("&aGrandma Soup")).build();

}
