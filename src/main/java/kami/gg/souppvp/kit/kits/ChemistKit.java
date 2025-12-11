package kami.gg.souppvp.kit.kits;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.kit.Kit;
import kami.gg.souppvp.kit.KitRarity;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.profile.ProfileState;
import kami.gg.souppvp.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class ChemistKit extends Kit {

    private static final short HARMING_POTION = 16428;
    private static final short POISON_POTION = 16420;

    @Override
    public String getName() {
        return "Chemist";
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
        return new ItemBuilder(Material.POTION).durability(HARMING_POTION).build();
    }

    @Override
    public List<String> getDescription() {
        List<String> description = new ArrayList<>();
        description.add("&7Begin with splash potions of instant damage and");
        description.add("&7poisons. After each kill, get a refill of splash");
        description.add("&7potions to continue irritating enemies.");

        return description;
    }

    @Override
    public List<ItemStack> getCombatEquipments() {
        List<ItemStack> itemStacks = new ArrayList<>();
        itemStacks.add(new ItemBuilder(Material.IRON_SWORD)
                .enchantment(Enchantment.DAMAGE_ALL, 1)
                .enchantment(Enchantment.DURABILITY, 3)
                .build());
        itemStacks.add(new ItemBuilder(Material.POTION)
                .amount(3)
                .durability(HARMING_POTION)
                .build());
        itemStacks.add(new ItemBuilder(Material.POTION)
                .amount(1)
                .durability(POISON_POTION)
                .build());
        return itemStacks;
    }

    @Override
    public ItemStack[] getArmor() {
        return new ItemStack[]{
                new ItemBuilder(Material.CHAINMAIL_BOOTS)
                        .enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1)
                        .build(),
                new ItemBuilder(Material.CHAINMAIL_LEGGINGS)
                        .enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1)
                        .build(),
                new ItemBuilder(Material.IRON_CHESTPLATE)
                        .enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1)
                        .build(),
                new ItemBuilder(Material.CHAINMAIL_HELMET)
                        .enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1)
                        .build()
        };
    }

    @Override
    public List<PotionEffect> getPotionEffects() {
        List<PotionEffect> potionEffects = new ArrayList<>();
        potionEffects.add(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
        return potionEffects;
    }

    @Override
    public void onSelect(Player player) {
        // Empty
    }

    @EventHandler
    public void onPlayerDeathEvent(PlayerDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        if (killer == null) return;

        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(killer.getUniqueId());

        if (profile == null) return;
        if (profile.isInEvent() || profile.getProfileState() == ProfileState.SPAWN) return;
        if (!profile.getCurrentKit().equalsIgnoreCase(this.getName())) return;

        giveRefillPotions(killer);
    }

    private void giveRefillPotions(Player killer) {
        boolean hasPoison = hasPotion(killer);

        if (!hasPoison) {
            ItemStack poisonPotion = new ItemBuilder(Material.POTION)
                    .amount(1)
                    .durability(POISON_POTION)
                    .build();

            if (!tryStackPotion(killer, poisonPotion, 1)) {
                killer.getWorld().dropItemNaturally(killer.getLocation(), poisonPotion);
            }
        }

        int harmingCount = countPotions(killer);

        if (harmingCount == 0) {
            ItemStack harmingPotions = new ItemBuilder(Material.POTION)
                    .amount(2)
                    .durability(HARMING_POTION)
                    .build();

            if (!tryStackPotion(killer, harmingPotions, 2)) {
                killer.getWorld().dropItemNaturally(killer.getLocation(), harmingPotions);
            }
            return;
        }

        if (harmingCount == 1) {
            for (ItemStack item : killer.getInventory().getContents()) {
                if (item != null && item.getDurability() == HARMING_POTION && item.getType() == Material.POTION) {
                    item.setAmount(2);
                    return;
                }
            }
        }
    }


    private boolean hasPotion(Player player) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == Material.POTION && item.getDurability() == ChemistKit.POISON_POTION) {
                return true;
            }
        }
        return false;
    }
    private int countPotions(Player player) {
        int count = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == Material.POTION && item.getDurability() == ChemistKit.HARMING_POTION) {
                count += item.getAmount();
            }
        }
        return count;
    }

    private boolean tryStackPotion(Player player, ItemStack item, int maxAdd) {
        for (ItemStack slot : player.getInventory().getContents()) {
            if (slot != null && slot.isSimilar(item)) {
                int newAmount = slot.getAmount() + item.getAmount();
                if (newAmount <= 64) {
                    slot.setAmount(newAmount);
                    return true;
                }
            }
        }

        if (player.getInventory().firstEmpty() != -1) {
            player.getInventory().addItem(item);
            return true;
        }

        return false;
    }

}