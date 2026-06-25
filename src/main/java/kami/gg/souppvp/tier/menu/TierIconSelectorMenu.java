package kami.gg.souppvp.tier.menu;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.tier.TierCategory;
import kami.gg.souppvp.tier.button.TierIconButton;
import kami.gg.souppvp.util.menu.Button;
import kami.gg.souppvp.util.menu.Menu;
import kami.gg.souppvp.util.menu.button.BackButton;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class TierIconSelectorMenu extends Menu {

    public TierIconSelectorMenu(Player player) {
        super(player, "Select Your Tier Icon", 36, true);
    }

    @Override
    public Map<Integer, Button> getButtons() {
        Map<Integer, Button> buttons = new HashMap<>();
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        int currentLevel = profile.getTier();

        int slot = 9;
        for (TierCategory category : TierCategory.values()) {
            buttons.put(slot++, new TierIconButton(category, currentLevel, profile));
        }

        buttons.put(4, new BackButton(new TiersProgressMenu(player)));

        for (int i = 0; i < 9; i++) {
            buttons.putIfAbsent(i, Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 15, " "));
        }

        return buttons;
    }
}
