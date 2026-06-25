package kami.gg.souppvp.kit.menu.editor;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.kit.CustomKit;
import kami.gg.souppvp.kit.Kit;
import kami.gg.souppvp.kit.storage.KitStorage;
import kami.gg.souppvp.util.ItemBuilder;
import kami.gg.souppvp.util.menu.Button;
import kami.gg.souppvp.util.menu.Menu;
import kami.gg.souppvp.util.menu.button.BackButton;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class KitArmorEditMenu extends Menu {

    private final Kit kit;
    private final KitStorage kitStorage;
    private static final String[] ARMOR_NAMES = {"Boots", "Leggings", "Chestplate", "Helmet"};
    private static final Material[] ARMOR_MATERIALS = {Material.LEATHER_BOOTS, Material.LEATHER_LEGGINGS, Material.LEATHER_CHESTPLATE, Material.LEATHER_HELMET};

    public KitArmorEditMenu(Kit kit, Player player) {
        super(player, "Editing " + kit.getRarityType().getColor() + kit.getName() + " &rSet", 27, false);
        this.kit = kit;
        this.kitStorage = SoupPvP.getInstance().getKitStorage();
    }

    @Override
    public Map<Integer, Button> getButtons() {
        Map<Integer, Button> buttons = new HashMap<>();

        int[] armorSlots = {15, 14, 12, 11};
        for (int i = 0; i < 4; i++) {
            final int armorIndex = i;
            ItemStack armorPiece = kit.getArmor()[i];
            
            buttons.put(armorSlots[i], new Button() {
                @Override
                public ItemStack getButtonItem(Player player) {
                    if (armorPiece == null || armorPiece.getType() == Material.AIR) {
                        return new ItemBuilder(ARMOR_MATERIALS[armorIndex])
                                .name("&c" + ARMOR_NAMES[armorIndex] + " (Empty)")
                                .lore("&7Wear the armor piece and click to set")
                                .build();
                    }
                    ItemStack display = armorPiece.clone();
                    return new ItemBuilder(display)
                            .name("&e" + ARMOR_NAMES[armorIndex])
                            .lore("&7Wear the armor piece and click to change")
                            .build();
                }

                @Override
                public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                    ItemStack[] armor = player.getInventory().getArmorContents();
                    ItemStack worn = armor[armorIndex];

                    if (worn == null || worn.getType() == Material.AIR) {
                        sendMessage(player, "&cYou're not wearing any " + ARMOR_NAMES[armorIndex] + "!");
                        playFail(player);
                        return;
                    }

                    kit.getArmor()[armorIndex] = worn.clone();
                    saveKit();
                    playSuccess(player);
                    sendMessage(player, "&a" + ARMOR_NAMES[armorIndex] + " set to &f" + worn.getType().name() + "&a!");
                    update();
                }
            });
        }

        buttons.put(13, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(Material.BARRIER)
                        .name("&c&lClear All Armor")
                        .lore(
                                "&cClick to clear all",
                                "&carmor pieces"
                        )
                        .build();
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                for (int i = 0; i < 4; i++) {
                    kit.getArmor()[i] = null;
                }
                saveKit();
                sendMessage(player, "&aAll armor cleared!");
                playFail(player);
                update();
            }
        });

        buttons.put(26, new BackButton(new KitEditorMenu(kit, player)));

        return buttons;
    }

    private void saveKit() {
        // Convert to CustomKit if not already
        if (!(kit instanceof CustomKit)) {
            CustomKit customKit = new CustomKit(
                    kit.getName(),
                    kit.getRarityType(),
                    kit.getPrice(),
                    kit.getIcon(),
                    kit.getDescription(),
                    kit.getCombatEquipments(),
                    kit.getArmor(),
                    kit.getPotionEffects(),
                    kit.getPrimaryAbility(),
                    kit.getSecondaryAbility()
            );
            customKit.setEnabled(kit.isEnabled());
            
            // Replace in handler
            SoupPvP.getInstance().getKitsHandler().getKits().remove(kit);
            SoupPvP.getInstance().getKitsHandler().getKits().add(customKit);
            
            kitStorage.saveKit(customKit);
        } else {
            kitStorage.saveKit(kit);
        }
    }
}
