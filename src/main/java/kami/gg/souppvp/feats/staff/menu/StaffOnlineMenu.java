package kami.gg.souppvp.feats.staff.menu;

import kami.gg.souppvp.feats.hooks.ranks.IRankHook;
import kami.gg.souppvp.feats.staff.StaffManager;
import kami.gg.souppvp.util.ItemBuilder;
import kami.gg.souppvp.util.ItemUtils;
import kami.gg.souppvp.util.menu.Button;
import kami.gg.souppvp.util.menu.pagination.PaginatedMenu;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public class StaffOnlineMenu extends PaginatedMenu {

    private final StaffManager staffManager;

    public StaffOnlineMenu(Player player) {
        this.staffManager = soupPvP.getStaffManager();
    }

    @Override
    public String getPrePaginatedTitle(Player player) {
        return staffManager.getStaffConfig().getString("STAFF_MODE.STAFF_ONLINE_MENU.TITLE");
    }

    @Override
    public int getSize() {
        return staffManager.getStaffConfig().getInt("STAFF_MODE.STAFF_ONLINE_MENU.SIZE");
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        List<Player> onlineStaff = Bukkit.getOnlinePlayers().stream()
                .filter(online -> online.hasPermission("azurite.staff"))
                .collect(Collectors.toList());

        int index = 0;
        for (Player staff : onlineStaff) {
            buttons.put(index++, createStaffButton(player, staff));
        }

        return buttons;
    }

    private Button createStaffButton(Player viewer, Player staff) {
        return new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                IRankHook rankHook = soupPvP.getRankHook();
                boolean staffMode = staffManager.isStaffEnabled(staff);
                boolean vanish = staffManager.isVanished(staff);

                UnaryOperator<String> replacer = s -> s
                        .replace("%staff%", staffMode ? "&a✔" : "&c✖")
                        .replace("%vanish%", vanish ? "&a✔" : "&c✖")
                        .replace("%player%", staff.getName())
                        .replace("%rank-prefix%", rankHook.getRankPrefix(staff))
                        .replace("%rank-suffix%", rankHook.getRankSuffix(staff))
                        .replace("%rank-name%", rankHook.getRankName(staff))
                        .replace("%color%", rankHook.getRankColor(staff));

                return new ItemBuilder(ItemUtils.getMatItem(staffManager.getStaffConfig().getString("STAFF_MODE.STAFF_ONLINE_MENU.STAFF_HEAD.MATERIAL")))
                        .name(replacer.apply(staffManager.getStaffConfig().getString("STAFF_MODE.STAFF_ONLINE_MENU.STAFF_HEAD.NAME")))
                        .lore(staffManager.getStaffConfig().getStringList("STAFF_MODE.STAFF_ONLINE_MENU.STAFF_HEAD.LORE")
                                .stream()
                                .map(replacer)
                                .collect(Collectors.toList()))
                        .durability(staffManager.getStaffConfig().getInt("STAFF_MODE.STAFF_ONLINE_MENU.STAFF_HEAD.DATA"))
                        .setSkullOwner(staff.getName())
                        .build();
            }
            @Override
            public void clicked(Player player, ClickType clickType) {
                String command = staffManager.getStaffConfig().getString("STAFF_MODE.STAFF_ONLINE_MENU.STAFF_HEAD.COMMAND").replace("%player%", staff.getName());
                viewer.chat(command);
            }
        };
    }
}