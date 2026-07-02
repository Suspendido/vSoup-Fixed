package kami.gg.souppvp.killstreak;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.killstreak.special.SpecialKillstreak;
import kami.gg.souppvp.killstreak.special.SpecialTypeKillstreak;
import kami.gg.souppvp.perk.Perk;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.ItemBuilder;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class ConfigurableKillstreak extends Killstreak implements Listener {
    private int id;
    private String name;
    private int requiredKills;
    private RewardType rewardType;
    private RewardData rewardData;
    private Material iconMaterial;
    private List<String> lore;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getRequired() {
        return requiredKills;
    }

    @Override
    public ItemStack getIcon() {
        List<String> xd = new ArrayList<>(lore);
        xd.add("");
        xd.add("&fKillstreak Required: &b" + getRequired());
        return new ItemBuilder(iconMaterial).name("&a" + name).lore(xd).build();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        if (killer == null) return;

        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(killer.getUniqueId());
        if (profile == null) return;

        boolean hasHardline = hasHardline(profile);
        int neededKS = hasHardline ? requiredKills - 1 : requiredKills;
        if (profile.getCurrentKillstreak() != neededKS) return;

        applyReward(killer);
    }

    private void applyReward(Player player) {
        player.sendMessage(CC.t("&aYou've received the &d" + name + " &aperk for reaching a &d" + requiredKills + " &akillstreak!"));

        switch (rewardType) {
            case ITEMS:
                giveItems(player);
                break;
            case EFFECTS:
                giveEffects(player);
                break;
            case SPECIAL:
                applySpecial(player);
                break;
        }
    }

    private void giveItems(Player player) {
        for (ItemStack item : rewardData.getItems()) {
            giveItemReplacingSoup(player, item);
        }
    }

    private void giveEffects(Player player) {
        for (Map<String, Object> effect : rewardData.getEffects()) {
            String type = (String) effect.get("type");
            int duration = (int) effect.get("duration");
            int amplifier = (int) effect.get("amplifier");

            PotionEffectType potionType = PotionEffectType.getByName(type);
            if (potionType != null) {
                player.addPotionEffect(new PotionEffect(potionType, duration, amplifier));
            }
        }
    }

    private void applySpecial(Player player) {
        String specialType = rewardData.getSpecialType();
        if (specialType == null) return;

        SpecialTypeKillstreak type;
        try {
            type = SpecialTypeKillstreak.valueOf(specialType.toUpperCase());
        } catch (IllegalArgumentException e) {
            return;
        }

        SpecialKillstreak special = SoupPvP.getInstance().getKillstreaksHandler().getSpecialKillstreaks().get(type);
        
        if (special != null) {
            special.apply(player, rewardData);
        }
    }

    private void giveItemReplacingSoup(Player player, ItemStack reward) {
        if (player.getInventory().firstEmpty() != -1) {
            player.getInventory().addItem(reward);
            return;
        }

        for (int i = 0; i < player.getInventory().getSize(); i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (item == null) continue;

            if (item.getType() == Material.BOWL || item.getType() == Material.MUSHROOM_SOUP) {
                player.getInventory().setItem(i, reward);
                return;
            }
        }
    }

    private boolean hasHardline(Profile profile) {
        List<String> perks = profile.getActivePerks();
        if (perks == null || perks.isEmpty()) return false;

        Perk hardline = SoupPvP.getInstance().getPerksHandler().getPerkByName("Hardline");
        if (hardline == null) return false;

        return perks.stream().anyMatch(name -> hardline.equals(SoupPvP.getInstance().getPerksHandler().getPerkByName(name)));
    }

    public enum RewardType {
        ITEMS, EFFECTS, SPECIAL
    }

    @Data
    public static class RewardData {
        private List<ItemStack> items;
        private List<Map<String, Object>> effects;
        private String specialType;
        private Map<String, Object> specialParams;
    }

    public static List<String> getDefaultLore(RewardType rewardType, String specialType) {
        return switch (rewardType) {
            case ITEMS -> List.of(
                "&7Receive custom items",
                "&7as a reward."
            );
            case EFFECTS -> List.of(
                "&7Receive potion effects",
                "&7as a reward."
            );
            case SPECIAL -> {
                if (specialType != null) {
                    try {
                        SpecialTypeKillstreak type = SpecialTypeKillstreak.valueOf(specialType.toUpperCase());
                        yield type.getDescription();
                    } catch (IllegalArgumentException e) {
                        yield List.of("&7Special killstreak reward.");
                    }
                }
                yield List.of("&7Special killstreak reward.");
            }
        };
    }
}
