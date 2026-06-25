package kami.gg.souppvp.kit.menu.editor;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.kit.CustomKit;
import kami.gg.souppvp.kit.Kit;
import kami.gg.souppvp.kit.KitRarity;
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

public class KitRaritySelectMenu extends Menu {

    private final Kit kit;
    private final KitStorage kitStorage;

    public KitRaritySelectMenu(Kit kit, Player player) {
        super(player, "Select Rarity", 27, false);
        this.kit = kit;
        this.kitStorage = SoupPvP.getInstance().getKitStorage();
    }

    @Override
    public Map<Integer, Button> getButtons() {
        Map<Integer, Button> buttons = new HashMap<>();

        int slot = 10;
        for (KitRarity rarity : KitRarity.values()) {
            final byte woolData = getWoolDataForRarity(rarity);
            buttons.put(slot, new Button() {
                @Override
                public ItemStack getButtonItem(Player player) {
                    boolean selected = kit.getRarityType() == rarity;
                    return new ItemBuilder(Material.WOOL)
                            .data(woolData)
                            .name(rarity.getColor() + rarity.getName())
                            .lore(
                                    "&b┃ &fChanging rarities will cause",
                                    "&b┃ &fto the prices changing!",
                                    "",
                                    "&b┃ &fDefault Rarity Price: " + rarity.getColor() + rarity.getPrice(),
                                    "",
                                    selected ? "&aCurrently selected" : "&eClick to select"
                            )
                            .build();
                }

                @Override
                public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                    kit.setRarityType(rarity);
                    kit.setPrice(rarity.getPrice());
                    saveKit();
                    sendMessage(player, "&aRarity set to: " + rarity.name());
                    playSuccess(player);
                    new KitEditorMenu(kit, player).open();
                }
            });
            slot++;
            if (slot == 17 || slot == 26) slot += 2;
        }

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

    private byte getWoolDataForRarity(KitRarity rarity) {
        return switch (rarity) {
            case COMMON -> 8;
            case UNCOMMON -> 5;
            case RARE -> 11;
            case ULTIMATE -> 9;
            case LEGENDARY -> 1;
            case MYTHICAL -> 10;
        };
    }
}
