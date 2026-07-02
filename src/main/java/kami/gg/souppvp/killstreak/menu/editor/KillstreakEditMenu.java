package kami.gg.souppvp.killstreak.menu.editor;

import kami.gg.souppvp.killstreak.ConfigurableKillstreak;
import kami.gg.souppvp.killstreak.menu.editor.button.*;
import kami.gg.souppvp.util.ItemBuilder;
import kami.gg.souppvp.util.menu.Button;
import kami.gg.souppvp.util.menu.Menu;
import kami.gg.souppvp.util.menu.button.BackButton;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class KillstreakEditMenu extends Menu {

    private final ConfigurableKillstreak killstreak;
    private final int listIndex;

    public KillstreakEditMenu(Player player, ConfigurableKillstreak killstreak, int listIndex) {
        super(player, "Edit Killstreak", 45, true);
        this.killstreak = killstreak;
        this.listIndex = listIndex;
    }

    @Override
    public Map<Integer, Button> getButtons() {
        Map<Integer, Button> buttons = new HashMap<>();

        // Display current killstreak info
        buttons.put(4, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                List<String> lore = new ArrayList<>();
                lore.add("&b┃ &fRequired Kills: &d" + killstreak.getRequiredKills());
                lore.add("&b┃ &fReward Type: &e" + killstreak.getRewardType().name());
                lore.add("");
                lore.add("&aConfigure using the buttons below");

                return new ItemBuilder(killstreak.getIcon())
                        .name("&a" + killstreak.getName())
                        .lore(lore)
                        .build();
            }
        });


        buttons.put(19, new EditNameButton(this, killstreak));
        buttons.put(20, new EditRequiredKillsButton(killstreak));
        buttons.put(21, new EditRewardTypeButton(killstreak));

        buttons.put(23, new ConfigureRewardButton(this, killstreak));
        buttons.put(24, new EditIconButton(killstreak));
        buttons.put(25, new EditDescriptionButton(this, killstreak));

        buttons.put(31, new SaveKillstreakButton(this, killstreak));
        buttons.put(44, new BackButton(new KillstreakEditorMenu(player)));

        setFillEnabled(true);
        return buttons;
    }
}
