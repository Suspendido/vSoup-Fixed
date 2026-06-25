package kami.gg.souppvp.util.menu;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class MenuListener implements Listener {

    private final MenuManager menuManager;

    public MenuListener(MenuManager menuManager) {
        this.menuManager = menuManager;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player player)) return;
        if (e.getClickedInventory() == null) return;

        Menu menu = menuManager.getMenus().get(player.getUniqueId());

        if (menu != null) {
            if (!menu.isAllowInteract()) {
                e.setCancelled(true);
            }

            menu.onClick(e);

            if (e.getClickedInventory() != player.getInventory()) {
                Button button = menu.getButtons().get(e.getSlot());
                if (button != null) {
                    button.clicked(player, e.getClick());
                    button.clicked(player, e.getSlot(), e.getClick(), e.getHotbarButton());
                }
            }

            if (e.getClickedInventory() == player.getInventory()) {
                menu.onClickOwn(e);
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        Menu menu = menuManager.getMenus().get(player.getUniqueId());

        if (menu != null) {
            menu.onClose();
            menu.destroy();
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        Player player = (Player) e.getPlayer();
        Menu menu = menuManager.getMenus().remove(player.getUniqueId());

        if (menu != null) {
            menu.onClose();
            menu.destroy();
        }
    }
}