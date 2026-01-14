package kami.gg.souppvp.util.menu;

import kami.gg.souppvp.SoupPvP;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class MenuListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onButtonPress(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        Menu openMenu = Menu.currentlyOpenedMenus.get(player.getName());

        if (openMenu == null) return;

        if (event.getSlot() != event.getRawSlot()) {
            if (event.getClick().isShiftClick()) {
                event.setCancelled(true);
            }
            return;
        }

        Button button = openMenu.getButtons().get(event.getSlot());

        if (button == null) {
            if (event.getCurrentItem() != null || event.getClick().isShiftClick()) {
                event.setCancelled(true);
            }
            return;
        }

        boolean cancel = button.shouldCancel(player, event.getClick());
        event.setCancelled(cancel || event.getClick().isShiftClick());

        button.clicked(player, event.getClick());
        button.clicked(player, event.getSlot(), event.getClick(), event.getHotbarButton());

        if (button.closesMenu(player, event.getClick())) {
            Menu.currentlyOpenedMenus.remove(player.getName());
            return;
        }

        if (button.shouldUpdate(player, event.getClick())) {
            openMenu.setClosedByMenu(true);
            openMenu.openMenu(player);
        }

        if (event.isCancelled()) {
            Bukkit.getScheduler().runTaskLater(SoupPvP.getInstance(), player::updateInventory, 1L);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;

        Menu openMenu = Menu.currentlyOpenedMenus.get(player.getName());

        if (openMenu == null) return;

        if (openMenu.isClosedByMenu()) {
            openMenu.setClosedByMenu(false);
            return;
        }

        openMenu.onClose(player);
        Menu.currentlyOpenedMenus.remove(player.getName());
    }
}
