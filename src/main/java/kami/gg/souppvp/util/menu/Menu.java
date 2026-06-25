package kami.gg.souppvp.util.menu;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.util.CC;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
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

	private Map<Integer, Button> buttons = new HashMap<>();
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
		this.buttons = this.getButtons();
		this.inventory.clear();

		// Set items
		for (Map.Entry<Integer, Button> entry : this.buttons.entrySet()) {
			this.inventory.setItem(entry.getKey(), createItemStack(entry.getValue()));
		}

		// Fill empty slots if enabled
		if (this.fillEnabled) {
			for (int i = 0; i < this.size; i++) {
				if (this.buttons.get(i) == null) {
					this.inventory.setItem(i, this.placeholderButton.getButtonItem(this.player));
				}
			}
		}

		this.player.openInventory(this.inventory);
		this.onOpen();
	}

	public void close() {
		this.updater = null;
		this.onClose();
	}

	public void update() {
		this.buttons = this.getButtons();
		this.inventory.clear();

		// Update items
		for (Map.Entry<Integer, Button> entry : this.buttons.entrySet()) {
			this.inventory.setItem(entry.getKey(), createItemStack(entry.getValue()));
		}

		// Fill empty slots if enabled
		if (this.fillEnabled) {
			for (int i = 0; i < this.size; i++) {
				if (this.buttons.get(i) == null) {
					this.inventory.setItem(i, this.placeholderButton.getButtonItem(this.player));
				}
			}
		}

		this.player.updateInventory();
	}

	private ItemStack createItemStack(Button button) {
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

	public void handleClick(InventoryClickEvent event) {
		int slot = event.getSlot();
		if (this.buttons.containsKey(slot)) {
			Button button = this.buttons.get(slot);
			button.clicked(this.player, event.getClick());
			button.clicked(this.player, slot, event.getClick(), event.getHotbarButton());
		}
	}

	public abstract Map<Integer, Button> getButtons();

	public void onOpen() {
	}

	public void onClose() {
	}

	public void onInventoryClick(InventoryClickEvent event) {
	}
}
