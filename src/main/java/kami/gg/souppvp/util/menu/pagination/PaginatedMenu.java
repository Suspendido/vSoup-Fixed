package kami.gg.souppvp.util.menu.pagination;

import kami.gg.souppvp.util.menu.Button;
import kami.gg.souppvp.util.menu.Menu;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public abstract class PaginatedMenu extends Menu {

    @Getter private int page = 1; {
        setUpdateAfterClick(false);
    }

    @Override
    public String getTitle(Player player) {
        return getPrePaginatedTitle(player);
    }

    public final void modPage(Player player, int mod) {
        page += mod;

        if (page < 1) {
            page = 1;
        }

        if (page > getPages(player)) {
            page = getPages(player);
        }

        getButtons().clear();
        openMenu(player);
    }

    public final int getPages(Player player) {
        int buttonAmount = getAllPagesButtons(player).size();

        if (buttonAmount == 0) {
            return 1;
        }

        return (int) Math.ceil(buttonAmount / (double) getMaxItemsPerPage(player));
    }

    @Override
    public final Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        int minIndex = (page - 1) * getMaxItemsPerPage(player);
        int maxIndex = page * getMaxItemsPerPage(player);

        int pageIndex = 0;
        int[] slots = getPaginatedSlots();

        for (Map.Entry<Integer, Button> entry : getAllPagesButtons(player).entrySet()) {
            int index = entry.getKey();

            if (index >= minIndex && index < maxIndex) {
                if (pageIndex >= slots.length) {
                    break;
                }

                buttons.put(slots[pageIndex], entry.getValue());
                pageIndex++;
            }
        }

        buttons.put(0, new PageButton(-1, this));
        buttons.put(8, new PageButton(1, this));

        for (int i = 1; i < 8; i++) {
            buttons.put(i, getPlaceholderButton());
        }

        Map<Integer, Button> global = getGlobalButtons(player);
        if (global != null) {
            buttons.putAll(global);
        }

        return buttons;
    }

    public int[] getPaginatedSlots() {
        return new int[]{
                10, 11, 12, 13, 14, 15, 16,
                19, 20, 21, 22, 23, 24, 25,
                28, 29, 30, 31, 32, 33, 34,
                37, 38, 39, 40, 41, 42, 43
        };
    }

    public int getMaxItemsPerPage(Player player) {
        return getPaginatedSlots().length;
    }

    public Map<Integer, Button> getGlobalButtons(Player player) {
        return null;
    }

    public abstract String getPrePaginatedTitle(Player player);

    public abstract Map<Integer, Button> getAllPagesButtons(Player player);
}
