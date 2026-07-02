package kami.gg.souppvp.feats.staff.menu;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.util.ItemBuilder;
import kami.gg.souppvp.util.Formatter;
import kami.gg.souppvp.util.menu.Button;
import kami.gg.souppvp.util.menu.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class InspectionMenu extends Menu {

    private final Player target;

    public InspectionMenu(Player player) {
        super(player, "&cInspecting &f" + player.getName(), 54, true);
        this.target = player;
    }

    @Override
    public Map<Integer, Button> getButtons() {
        Map<Integer, Button> buttons = new HashMap<>();
        PlayerInventory inventory = target.getInventory();
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(target.getUniqueId());

        for (int i = 0; i < inventory.getContents().length; i++) {
            int slot = i;

            buttons.put(i, new Button() {
                @Override
                public ItemStack getButtonItem(Player player) {
                    ItemStack item = inventory.getItem(slot);
                    if (item == null || item.getType() == Material.AIR) {
                        return new ItemStack(Material.AIR);
                    }
                    return item.clone();
                }
            });
        }


        buttons.put(45, armorButton(inventory::getHelmet));
        buttons.put(46, armorButton(inventory::getChestplate));
        buttons.put(47, armorButton(inventory::getLeggings));
        buttons.put(48, armorButton(inventory::getBoots));

        buttons.put(49, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(Material.SKULL_ITEM)
                        .name(SoupPvP.getInstance().getRankHook().getRankColor(player) + player.getName())
                        .setSkullOwner(player.getName())
                        .lore(
                                "&b┃ &fKills: &b" + profile.getKills(),
                                "&b┃ &fDeaths: &b" + profile.getDeaths(),
                                "&b┃ &fCredits: &b" + profile.getCredits()
                        )
                        .data(3)
                        .build();
            }
        });

        buttons.put(50, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                ItemBuilder builder = new ItemBuilder(Material.BREWING_STAND_ITEM).name("&dPotion Effects");

                for (PotionEffect effect : target.getActivePotionEffects()) {
                    int duration = effect.getDuration();
                    int amplifier = effect.getAmplifier() + 1;
                    long durationLong = (duration / 20L) * 1000L; // Convert to second and then long.

                    builder.lore("&e" + convertName(effect.getType()) + " &7(&b" + (duration > 1000000 ? "Permanent" : Formatter.millisToTimer(durationLong)) + " LVL: " + amplifier + "&7)");
                }

                return builder.build();
            }
        });

        buttons.put(53, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(Material.BARRIER)
                        .name("&cClose")
                        .build();
            }
        });

        for (int i = 36; i <= 44; i++) {
            buttons.put(i, fillerButton());
        }

        for (int i = 51; i <= 52; i++) {
            buttons.put(i, fillerButton());
        }

        return buttons;
    }

    private Button fillerButton() {
        return new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(Material.STAINED_GLASS_PANE).name(" ").data(15).build();
            }
        };
    }

    private Button armorButton(Supplier<ItemStack> supplier) {
        return new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                ItemStack item = supplier.get();
                if (item == null || item.getType() == Material.AIR) {
                    return new ItemStack(Material.AIR);
                }
                return item.clone();
            }
        };
    }

    public static String convertName(PotionEffectType potion) {
        return switch (potion.getName()) {
            case "INCREASE_DAMAGE" -> "Strength";
            case "DAMAGE_RESISTANCE" -> "Resistance";
            case "SLOW" -> "Slowness";
            case "FAST_DIGGING" -> "Haste";
            case "SLOW_DIGGING" -> "Mining Fatigue";
            case "CONFUSION" -> "Nausea";
            case "FIRE_RESISTANCE" -> "Fire Resistance";
            case "WEAKNESS" -> "Weakness";
            case "ABSORPTION" -> "Absorption";
            case "NIGHT_VISION" -> "Night Vision";
            default -> capitalize(potion.getName().toLowerCase().replace("_", " "));
        };

    }

    public static String capitalize(String name) {
        char[] array = name.toCharArray();
        array[0] = Character.toUpperCase(array[0]);

        for (int i = 1; i < array.length; i++) {
            if (Character.isWhitespace(array[i - 1])) {
                array[i] = Character.toUpperCase(array[i]);
            }
        }

        return new String(array);
    }
}
