package kami.gg.souppvp.util.menu;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.util.CC;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;

@Getter
@Setter
public abstract class Menu {

	protected SoupPvP plugin = SoupPvP.getInstance();
	protected Player player;
	protected String title;
	protected int size;
	protected Inventory inventory;
	protected BukkitTask updater;
	protected boolean fillEnabled;
	protected boolean allowInteract;
	protected boolean autoUpdate;
	protected long updateInterval;

	protected Map<Integer, Button> buttons;
	private Button placeholderButton = Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 15, " ");

	public Menu(Player player, String title, int size, boolean autoUpdate) {
		this.player = player;
		this.title = CC.t(title);
		this.size = size;
		this.autoUpdate = autoUpdate;
		this.updateInterval = 20L; // Default 1 second
		this.fillEnabled = false;
		this.allowInteract = false;
		this.inventory = Bukkit.createInventory(null, size, this.title);
	}

	public Menu(Player player, String title, int size, boolean autoUpdate, long updateInterval) {
		this.player = player;
		this.title = CC.t(title);
		this.size = size;
		this.autoUpdate = autoUpdate;
		this.updateInterval = updateInterval;
		this.fillEnabled = false;
		this.allowInteract = false;
		this.inventory = Bukkit.createInventory(null, size, this.title);
	}

	public void open() {
		this.buttons = getButtons();

		// Set the first items (0-based indexing)
		for (Map.Entry<Integer, Button> entry : buttons.entrySet()) {
			inventory.setItem(entry.getKey(), createItemStack(entry.getValue()));
		}

		// Fill null/air spots with our filler
		if (fillEnabled) {
			for (int i = 0; i < inventory.getSize(); i++) {
				ItemStack item = inventory.getItem(i);
				if (item == null || item.getType() == Material.AIR) {
					inventory.setItem(i, placeholderButton.getButtonItem(player));
				}
			}
		}

		// Open and add to map
		player.openInventory(inventory);
		SoupPvP.getInstance().getMenuManager().getMenus().put(player.getUniqueId(), this);

		// Start updater if auto-update is enabled
		if (autoUpdate) {
			updater = Bukkit.getScheduler().runTaskTimer(plugin, this::update, 0L, updateInterval);
		}

		onOpen();
	}

	public void update() {
		if (!player.isOnline()) {
			destroy();
			return;
		}

		// Clear inventory to remove ghost items from previous state
		inventory.clear();

		// Re-fetch the buttons and cache them again
		buttons = getButtons();

		// Update the items in our inventory (0-based indexing)
		for (Map.Entry<Integer, Button> entry : buttons.entrySet()) {
			inventory.setItem(entry.getKey(), createItemStack(entry.getValue()));
		}

		// Update filler
		if (fillEnabled) {
			for (int i = 0; i < inventory.getSize(); i++) {
				ItemStack item = inventory.getItem(i);
				if (item == null || item.getType() == Material.AIR) {
					inventory.setItem(i, placeholderButton.getButtonItem(player));
				}
			}
		}

		player.updateInventory();
	}

	public void destroy() {
		// Lower ram usage
		if (buttons != null) {
			buttons.clear();
		}
		inventory.clear();

		// Remove from map
		SoupPvP.getInstance().getMenuManager().getMenus().remove(player.getUniqueId());

		// Cancel updater
		if (updater != null) {
			updater.cancel();
			updater = null;
		}

		onClose();
	}

	public void onClick(InventoryClickEvent e) {
		if (!allowInteract) {
			e.setCancelled(true);
		}
	}

	public void onClickOwn(InventoryClickEvent e) {
	}

	public ItemStack createItemStack(Button button) {
		ItemStack item = button.getButtonItem(this.player);

		if (item.getType() != Material.SKULL_ITEM) {
			ItemMeta meta = item.getItemMeta();

			if (meta != null && meta.hasDisplayName()) {
				meta.setDisplayName(meta.getDisplayName() + "§b§c§d§e");
			}

			item.setItemMeta(meta);
		}

		return item;
	}

	public abstract Map<Integer, Button> getButtons();

	public void onOpen() {
	}

	public void onClose() {
	}

}
