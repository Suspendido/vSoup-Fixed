package kami.gg.souppvp.feats.staff;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.feats.staff.extra.StaffItem;
import kami.gg.souppvp.feats.staff.extra.StaffItemAction;
import kami.gg.souppvp.feats.staff.listener.StaffListener;
import kami.gg.souppvp.util.ItemBuilder;
import kami.gg.souppvp.util.ItemUtils;
import kami.gg.souppvp.util.Pair;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class StaffManager {

    private final Map<UUID, Staff> staffMembers;
    private final Map<Pair<String, List<String>>, StaffItem> staffItems;
    private final SoupPvP instance;

    private final Set<UUID> vanished;
    private final Set<UUID> frozen;
    private final Set<UUID> staffBuild;
    private final Set<UUID> hideStaff;

    private FileConfiguration staffConfig;
    private static final String CONFIG_FILE = "staff.yml";

    public StaffManager(SoupPvP instance) {
        super();
        this.instance = instance;

        this.staffMembers = new ConcurrentHashMap<>();
        this.staffItems = new HashMap<>();

        this.vanished = new HashSet<>();
        this.frozen = new HashSet<>();
        this.staffBuild = new HashSet<>();
        this.hideStaff = new HashSet<>();

        this.loadConfig();
        this.load();

        new StaffListener();
    }

    private void loadConfig() {
        try {
            File configFile = new File(instance.getDataFolder(), CONFIG_FILE);

            if (!configFile.exists()) {
                instance.saveResource(CONFIG_FILE, false);
            }

            staffConfig = YamlConfiguration.loadConfiguration(configFile);

        } catch (Exception e) {
            instance.getLogger().severe("Failed to load staff configuration: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void reload() {
        staffItems.clear();
        this.loadConfig();
        this.load();
    }

    public void disable() {
        for (Staff staff : staffMembers.values()) {
            Player player = staff.getPlayer();
            PlayerInventory inventory = player.getInventory();

            for (PotionEffect effect : staff.getEffects()) {
                player.addPotionEffect(effect);
            }

            inventory.setContents(staff.getContents());
            inventory.setArmorContents(staff.getArmorContents());
            player.updateInventory();
            player.setGameMode(staff.getGameMode());
        }
    }

    private void load() {
        for (String key : getStaffConfig().getConfigurationSection("STAFF_MODE.STAFF_ITEMS").getKeys(false)) {
            String path = "STAFF_MODE.STAFF_ITEMS." + key + ".";
            String action = getStaffConfig().getString(path + "ACTION");
            String replace = getStaffConfig().getString(path + "REPLACE");
            String name = getStaffConfig().getString(path + "NAME");
            List<String> list = getStaffConfig().getStringList(path + "LORE");
            ItemStack item = new ItemBuilder(ItemUtils.getMatItem(getStaffConfig().getString(path + "MATERIAL")))
                    .name(name).lore(list)
                    .durability(getStaffConfig().getInt(path + "DATA"))
                    .setSkullOwner(getStaffConfig().getString(path + "TEXTURE"))
                    .build();

            staffItems.put(new Pair<>(name, list), new StaffItem(
                    this, key,
                    action.isEmpty() ? null : StaffItemAction.valueOf(action),
                    replace.isEmpty() ? null : replace,
                    getStaffConfig().getString(path + "COMMAND"), item,
                    getStaffConfig().getInt(path + "SLOT"))
            );
        }
    }

    public void enableStaff(Player player) {
        PlayerInventory inventory = player.getInventory();
        Staff staff = new Staff(player, player.getGameMode());

        inventory.clear();
        inventory.setArmorContents(null);

        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
            staff.getEffects().add(effect);
        }

        for (StaffItem item : staffItems.values()) {
            // They are always vanished when u enable staff mode.
            if (item.getAction() == StaffItemAction.VANISH_ON) continue;
            player.getInventory().setItem(item.getSlot() - 1, item.getItem());
        }

        player.updateInventory();
        player.setGameMode(GameMode.CREATIVE);

        enableVanish(player);
        staffMembers.put(player.getUniqueId(), staff);
        this.getHideStaff().add(player.getUniqueId());
    }

    private void setItemInConfiguredSlot(Player player, ItemStack item, int slot) {
        if (player == null || item == null) return;
        PlayerInventory inv = player.getInventory();

        if (slot == -1) {
            inv.setLeggings(item);
            return;
        }

        if (slot >= 1 && slot <= 36) {
            inv.setItem(slot - 1, item);
        }
    }

    public void disableStaff(Player player) {
        Staff staff = staffMembers.get(player.getUniqueId());

        if (staff != null) {
            PlayerInventory inventory = player.getInventory();

            inventory.setContents(staff.getContents());
            inventory.setArmorContents(staff.getArmorContents());

            for (PotionEffect effect : staff.getEffects()) {
                player.addPotionEffect(effect);
            }

            player.updateInventory();
            player.setGameMode(staff.getGameMode());

            staffMembers.remove(player.getUniqueId());
            staffBuild.remove(player.getUniqueId());
            disableVanish(player);
            this.getHideStaff().remove(player.getUniqueId());
        }
    }

    public void enableVanish(Player player) {
        vanished.add(player.getUniqueId());

        if (isStaffEnabled(player)) {
            for (StaffItem item : staffItems.values()) {
                if (item.getAction() == StaffItemAction.VANISH_OFF) {
                    player.getInventory().setItem(item.getSlot() - 1, item.getItem());
                }
            }
        }

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            // Add hide staff
            if (onlinePlayer.hasPermission("azurite.vanish") &&
                    !hideStaff.contains(onlinePlayer.getUniqueId())) continue;
            onlinePlayer.hidePlayer(player);
        }
    }

    public void disableVanish(Player player) {
        vanished.remove(player.getUniqueId());

        if (isStaffEnabled(player)) {
            for (StaffItem item : staffItems.values()) {
                if (item.getAction() == StaffItemAction.VANISH_ON) {
                    player.getInventory().setItem(item.getSlot() - 1, item.getItem());
                }
            }
        }

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.showPlayer(player);
        }
    }

    public void freezePlayer(Player player) {
        player.setWalkSpeed(0F);
        player.setFoodLevel(0);
        player.setSprinting(false);
        player.setAllowFlight(false);
        player.setFlying(false);
        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 128));
        frozen.add(player.getUniqueId());
    }

    public void unfreezePlayer(Player player) {
        player.setWalkSpeed(0.2F);
        player.setFoodLevel(20);
        player.setSprinting(false);
        player.setAllowFlight(false);
        player.setFlying(false);
        player.removePotionEffect(PotionEffectType.JUMP);
        frozen.remove(player.getUniqueId());
    }

    public StaffItem getItem(ItemStack item) {
        List<String> lore = Collections.emptyList();
        String name = "";

        if (item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();

            if (meta.hasLore()) {
                lore = meta.getLore();
            }

            if (meta.hasDisplayName()) {
                name = meta.getDisplayName();
            }
        }

        return staffItems.get(new Pair<>(name, lore));
    }

    public boolean isVanished(Player player) {
        return vanished.contains(player.getUniqueId());
    }

    public boolean isStaffEnabled(Player player) {
        return staffMembers.containsKey(player.getUniqueId());
    }

    public boolean isStaffBuild(Player player) {
        return staffBuild.contains(player.getUniqueId());
    }

    public boolean isHideStaff(Player player) {
        return hideStaff.contains(player.getUniqueId());
    }

    public boolean isFrozen(Player player) {
        return frozen.contains(player.getUniqueId());
    }
}