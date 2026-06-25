package kami.gg.souppvp.kit.ability.impl;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.kit.ability.KitAbility;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.profile.ProfileState;
import kami.gg.souppvp.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

public class ChemistAbility implements KitAbility {

    private static final short HARMING_POTION = 16428;
    private static final short POISON_POTION = 16420;

    @Override
    public String getName() {
        return "Chemist";
    }

    @Override
    public String getDescription() {
        return "&fRefill splash potions on every kill";
    }

    @Override
    public String getColor() {
        return "&d";
    }

    @Override
    public ItemStack getItem() {
        return new ItemBuilder(Material.POTION)
                .durability(HARMING_POTION)
                .name("&d&lPotion Refill")
                .lore(
                        "&7Refill splash potions on kill",
                        "&7- Instant Damage",
                        "&7- Poison"
                )
                .build();
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        if (killer == null) return;

        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(killer.getUniqueId());

        if (profile == null) return;
        if (profile.isInEvent() || profile.getProfileState() == ProfileState.SPAWN) return;

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
            if (item != null && item.getType() == Material.POTION && item.getDurability() == POISON_POTION) {
                return true;
            }
        }
        return false;
    }

    private int countPotions(Player player) {
        int count = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == Material.POTION && item.getDurability() == HARMING_POTION) {
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
