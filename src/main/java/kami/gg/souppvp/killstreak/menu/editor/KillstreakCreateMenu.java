package kami.gg.souppvp.killstreak.menu.editor;

import kami.gg.souppvp.killstreak.ConfigurableKillstreak;
import kami.gg.souppvp.killstreak.menu.editor.button.*;
import kami.gg.souppvp.util.ItemBuilder;
import kami.gg.souppvp.util.menu.Button;
import kami.gg.souppvp.util.menu.Menu;
import kami.gg.souppvp.util.menu.button.BackButton;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter @Setter
public class KillstreakCreateMenu extends Menu {

    private String name = "";
    private int requiredKills = 5;
    private ConfigurableKillstreak.RewardType rewardType = ConfigurableKillstreak.RewardType.ITEMS;
    private ConfigurableKillstreak.RewardData rewardData = new ConfigurableKillstreak.RewardData();
    private ConfigurableKillstreak tempKillstreak;
    private Material iconMaterial = Material.DIAMOND;
    private final List<String> lore = new ArrayList<>();

    public KillstreakCreateMenu(Player player) {
        super(player, "Create Killstreak", 45, true);
        updateDefaultLore();
    }

    private void updateDefaultLore() {
        lore.clear();
        lore.addAll(ConfigurableKillstreak.getDefaultLore(rewardType, rewardData.getSpecialType()));
    }

    public void setRewardType(ConfigurableKillstreak.RewardType rewardType) {
        this.rewardType = rewardType;
        updateDefaultLore();
    }

    public void setRewardData(ConfigurableKillstreak.RewardData rewardData) {
        this.rewardData = rewardData;
        if (rewardType == ConfigurableKillstreak.RewardType.SPECIAL) {
            updateDefaultLore();
        }
    }

    @Override
    public Map<Integer, Button> getButtons() {
        Map<Integer, Button> buttons = new HashMap<>();

        // Display current config
        buttons.put(4, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                List<String> lore = new ArrayList<>();
                lore.add("&b┃ &fName: " + (name.isEmpty() ? "&cNot set" : "&a" + name));
                lore.add("&b┃ &fRequired Kills: &d" + requiredKills);
                lore.add("&b┃ &fReward Type: &e" + rewardType.name());
                lore.add("&b┃ &fIcon: " + iconMaterial.name());
                lore.add("");
                lore.add("&aConfigure using the buttons below");

                return new ItemBuilder(iconMaterial)
                        .name("&aNew Killstreak")
                        .lore(lore)
                        .build();
            }
        });

        buttons.put(19, new EditNameButton(this));
        buttons.put(20, new EditRequiredKillsButton(this));
        buttons.put(21, new EditRewardTypeButton(this));

        buttons.put(23, new ConfigureRewardButton(this));
        buttons.put(24, new EditIconButton(this));
        buttons.put(25, new EditDescriptionButton(this));

        buttons.put(31, new CreateKillstreakButton(this));
        buttons.put(44, new BackButton(new KillstreakEditorMenu(player)));

        setFillEnabled(true);
        return buttons;
    }
}
