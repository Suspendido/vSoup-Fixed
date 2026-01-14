package kami.gg.souppvp.util;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ItemBuilder implements Listener {

    private final ItemStack is;

    public ItemBuilder(Material mat) {
        is = new ItemStack(mat);
    }

    public ItemBuilder(ItemStack is) {
        this.is = is;
    }

    public ItemBuilder amount(int amount) {
        is.setAmount(amount);
        return this;
    }

    public ItemBuilder name(String name) {
        ItemMeta meta = is.getItemMeta();
        meta.setDisplayName(CC.translate(name));
        is.setItemMeta(meta);
        return this;
    }

    public ItemBuilder lore(String name) {
        ItemMeta meta = is.getItemMeta();
        List<String> lore = meta.getLore();

        if (lore == null) {
            lore = new ArrayList<>();
        }

        lore.add(CC.translate(name));
        meta.setLore(lore);

        is.setItemMeta(meta);

        return this;
    }

    public ItemBuilder setHeadTexture(String texture) {
        if (is.getType() != Material.SKULL_ITEM && is.getDurability() != (short) 3) return this;

        SkullMeta im = (SkullMeta) is.getItemMeta();
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        PropertyMap properties = profile.getProperties();
        properties.put("textures", new Property("texture", texture));

        try {
            Field field = im.getClass().getDeclaredField("profile");
            field.setAccessible(true);
            field.set(im, profile);
        }catch (Exception e) {
            e.printStackTrace();
        }

        is.setItemMeta(im);
        return this;
    }

    public ItemBuilder setSkullOwner(String owner) {
        try {
            SkullMeta im = (SkullMeta) is.getItemMeta();
            im.setOwner(owner);
            is.setItemMeta(im);
        } catch (ClassCastException ignored) {}
        return this;
    }

    public ItemBuilder lore(String... lore) {
        List<String> toSet = new ArrayList<>();
        ItemMeta meta = is.getItemMeta();

        for (String string : lore) {
            toSet.add(CC.translate(string));
        }

        meta.setLore(toSet);
        is.setItemMeta(meta);

        return this;
    }

    public ItemBuilder lore(List<String> lore) {
        List<String> toSet = new ArrayList<>();
        ItemMeta meta = is.getItemMeta();

        for (String string : lore) {
            toSet.add(CC.translate(string));
        }

        meta.setLore(toSet);
        is.setItemMeta(meta);

        return this;
    }

    public ItemBuilder durability(int durability) {
        is.setDurability((short) durability);
        return this;
    }

    public ItemBuilder data(int data) {
        is.setDurability((short) data);
        return this;
    }

    public ItemBuilder enchantment(Enchantment enchantment, int level) {
        is.addUnsafeEnchantment(enchantment, level);
        return this;
    }

    public ItemBuilder enchantment(Enchantment enchantment) {
        is.addUnsafeEnchantment(enchantment, 1);
        return this;
    }

    public ItemBuilder color(Color color) {
        if (is.getType() == Material.LEATHER_BOOTS || is.getType() == Material.LEATHER_CHESTPLATE || is.getType() == Material.LEATHER_HELMET || is.getType() == Material.LEATHER_LEGGINGS) {
            LeatherArmorMeta meta = (LeatherArmorMeta) is.getItemMeta();
            meta.setColor(color);
            is.setItemMeta(meta);

            return this;
        } else {
            throw new IllegalArgumentException("color() only applicable for leather armor!");
        }
    }

    public ItemBuilder type(Material material) {
        is.setType(material);
        return this;
    }

    public ItemBuilder clearLore() {
        ItemMeta meta = is.getItemMeta();

        meta.setLore(new ArrayList<>());
        is.setItemMeta(meta);

        return this;
    }

    public ItemBuilder clearEnchantments() {
        for (Enchantment e : is.getEnchantments().keySet()) {
            is.removeEnchantment(e);
        }

        return this;
    }

    public ItemStack build() {
        return is;
    }

}