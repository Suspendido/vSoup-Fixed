package kami.gg.souppvp.feats.staff.menu;

import kami.gg.souppvp.util.ItemBuilder;
import kami.gg.souppvp.util.menu.Button;
import kami.gg.souppvp.util.menu.Menu;
import kami.gg.souppvp.util.CC;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;

import java.util.HashMap;
import java.util.Map;

public class InspectionMenu extends Menu {

    private final Player target;
    private final ItemStack filler;

    public InspectionMenu(Player target) {
        this.target = target;
        this.filler = Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 15, " ").getButtonItem(null);
        setPlaceholder(false);
    }

    @Override
    public String getTitle(Player player) {
        return CC.t("&cInspecting &f" + target.getName());
    }

    @Override
    public int getSize() {
        return 54;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        PlayerInventory inventory = target.getInventory();

        for (int i = 0; i < inventory.getContents().length; i++) {
            int slot = i;

            buttons.put(i + 1, new Button() {
                @Override
                public ItemStack getButtonItem(Player player) {
                    return inventory.getItem(slot);
                }
            });
        }


        buttons.put(45, armorButton(inventory::getHelmet));
        buttons.put(46, armorButton(inventory::getChestplate));
        buttons.put(47, armorButton(inventory::getLeggings));
        buttons.put(48, armorButton(inventory::getBoots));
        buttons.put(50, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                ItemBuilder builder = new ItemBuilder(Material.POTION).name("&dPotion Effects");

                for (PotionEffect effect : target.getActivePotionEffects()) {
                    builder.lore("&7" + effect.getType().getName() + " &fx" + (effect.getAmplifier() + 1));
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
                return filler;
            }
        };
    }

    private Button armorButton(java.util.function.Supplier<ItemStack> supplier) {
        return new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return supplier.get();
            }
        };
    }
}
