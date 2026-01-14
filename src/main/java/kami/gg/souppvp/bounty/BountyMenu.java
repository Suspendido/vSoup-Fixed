package kami.gg.souppvp.bounty;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.bounty.button.BountyPlayerButton;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.menu.Button;
import kami.gg.souppvp.util.menu.pagination.PaginatedMenu;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class BountyMenu extends PaginatedMenu {

    private static final int[] CORNERS = {
            1, 2, 3, 4, 5, 6, 7, 9, 17, 18, 26, 27, 35, 36, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53
    };

    @Override
    public String getPrePaginatedTitle(Player player) {
        return CC.translate("Available Bounties");
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        Bukkit.getOnlinePlayers().stream()
                .map(target -> SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(target.getUniqueId()))
                .filter(targetProfile -> targetProfile != null && targetProfile.getBounty() > 0)
                .sorted(Comparator.comparingInt(Profile::getBounty).reversed())
                .forEach(targetProfile -> {
                    Player targetPlayer = Bukkit.getPlayer(targetProfile.getUuid());
                    if (targetPlayer != null && targetPlayer.isOnline()) {
                        buttons.put(buttons.size(), new BountyPlayerButton(targetPlayer));
                    }
                });

        return buttons;
    }

    @Override
    public Map<Integer, Button> getGlobalButtons(Player player) {
        Map<Integer, Button> global = new HashMap<>();
        Button filler = getPlaceholderButton();

        for (int slot : CORNERS) {
            global.put(slot, filler);
        }

        return global;
    }
}