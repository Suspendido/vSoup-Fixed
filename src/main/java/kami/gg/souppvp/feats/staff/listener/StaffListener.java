package kami.gg.souppvp.feats.staff.listener;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.feats.staff.extra.StaffItem;
import kami.gg.souppvp.feats.staff.extra.StaffItemAction;
import kami.gg.souppvp.feats.staff.menu.InspectionMenu;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.Cooldown;
import kami.gg.souppvp.util.TaskUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class StaffListener implements Listener {

    private final SoupPvP instance;
    private final Cooldown interactCooldown;

    public StaffListener() {
        this.instance = SoupPvP.getInstance();
        this.interactCooldown = new Cooldown(100);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCommand(PlayerCommandPreprocessEvent e) {
        Player player = e.getPlayer();
        String message = e.getMessage().toLowerCase();

        if (!instance.getStaffManager().isFrozen(player)) return;
        if (message.startsWith("/msg") || message.startsWith("/tell")) {
            return;
        }

        e.setCancelled(true);
        player.sendMessage(CC.translate("&cYou are not allowed to use commands while frozen."));
    }


    @EventHandler(ignoreCancelled = true)
    public void onTab(PlayerChatTabCompleteEvent e) {
        for (UUID uuid : instance.getStaffManager().getStaffMembers().keySet()) {
            Player staff = Bukkit.getPlayer(uuid);

            if (staff == null) continue;

            e.getTabCompletions().remove(staff.getName());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onDamageFrozen(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player player)) return;

        if (instance.getStaffManager().isFrozen(player)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();

        if (instance.getStaffManager().isFrozen(player)) {
            e.setTo(e.getFrom());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onDropFrozen(PlayerDropItemEvent e) {
        Player player = e.getPlayer();

        if (instance.getStaffManager().isFrozen(player)) {
            e.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPickupFrozen(PlayerPickupItemEvent e) {
        Player player = e.getPlayer();

        if (instance.getStaffManager().isFrozen(player)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onClickFrozen(PlayerInteractEvent e) {
        Player player = e.getPlayer();

        if (instance.getStaffManager().isFrozen(player)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onQuitFrozen(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        if (!instance.getStaffManager().isFrozen(player)) return;
        List<String> message = Arrays.asList(
                "",
                "%prefix%%color%%player% &fhas logged out while &dfrozen&f!",
                ""
        );

        for (String s : message) {
            player.sendMessage(CC.translate(s));
        }

        String rankPrefix = CC.translate(instance.getRankHook().getRankPrefix(player));
        String rankColor = CC.translate(instance.getRankHook().getRankColor(player));

        if (instance.getStaffManager().isStaffEnabled(player)) {
            for (String s : message) {
                player.sendMessage(CC.translate(s
                        .replace("%prefix%", rankPrefix)
                        .replace("%color%", rankColor)
                        .replace("%player%", player.getName())
                ));
            }
        }
    }

    @EventHandler
    public void onClick(PlayerInteractEvent e) {
        if (!e.getAction().name().contains("RIGHT")) return;

        Player player = e.getPlayer();
        Block block = e.getClickedBlock();

        if (instance.getStaffManager().isStaffEnabled(player) && block != null && block.getType().name().contains("OAK_SIGN")) {
            e.setCancelled(true);
        }

        if (instance.getStaffManager().isStaffEnabled(player)) {
            if (!instance.getStaffManager().isStaffBuild(player)) {
                e.setCancelled(true);
            }

            if (interactCooldown.hasCooldown(player)) return;

            interactCooldown.applyCooldownTicks(player, 100); // 0.1s
            handleClick(player);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onHit(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player damager)) return;

        if (instance.getStaffManager().isStaffEnabled(damager) || instance.getStaffManager().isVanished(damager)) {
            e.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onInspect(PlayerInteractEntityEvent e) {
        if (!(e.getRightClicked() instanceof Player clicked)) return;

        Player player = e.getPlayer();

        if (instance.getStaffManager().isStaffEnabled(player)) {
            ItemStack hand = getItemInHand(player);

            if (hand == null) return;

            StaffItem staffItem = instance.getStaffManager().getItem(hand);

            if (staffItem == null) return;
            if (staffItem.getAction() == null) return;
            if (interactCooldown.hasCooldown(player)) return;

            interactCooldown.applyCooldownTicks(player, 100); // 0.1s

            if (staffItem.getAction() == StaffItemAction.INSPECTION) {
                new InspectionMenu(player).openMenu(player);

            } else if (staffItem.getAction() == StaffItemAction.FREEZE) {
                player.chat("/freeze " + clicked.getName());

            } else if (!staffItem.getCommand().isEmpty() && staffItem.getAction() == StaffItemAction.INTERACT_PLAYER) {
                player.chat(staffItem.getCommand()
                        .replace("%player%", clicked.getName())
                );
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onDamageStaff(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player player)) return;

        if (instance.getStaffManager().isStaffEnabled(player) || instance.getStaffManager().isVanished(player)) {
            e.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true) // Deny clicking of blocks (Deny GMC Abuse)
    public void onInventory(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player player)) return;

        if (!instance.getStaffManager().isStaffBuild(player) && instance.getStaffManager().isStaffEnabled(player)) {
            e.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBreak(BlockBreakEvent e) {
        Player player = e.getPlayer();

        if (!instance.getStaffManager().isStaffBuild(player) && instance.getStaffManager().isStaffEnabled(player)) {
            e.setCancelled(true);
            player.sendMessage(CC.translate("&cYou cannot break blocks while on staff mode!"));
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent e) {
        Player player = e.getPlayer();

        if (!instance.getStaffManager().isStaffBuild(player) && instance.getStaffManager().isStaffEnabled(player)) {
            e.setCancelled(true);
            player.sendMessage(CC.translate("&cYou cannot place blocks while on staff mode!"));
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onDrop(PlayerDropItemEvent e) {
        Player player = e.getPlayer();

        if (instance.getStaffManager().isStaffBuild(player)) return;

        if (instance.getStaffManager().isStaffEnabled(player) || instance.getStaffManager().isVanished(player)) {
            e.setCancelled(true);
            player.sendMessage(CC.translate("&cYou cannot drop items while on staff mode!"));
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onHunger(FoodLevelChangeEvent e) {
        if (!(e.getEntity() instanceof Player player)) return;

        if (instance.getStaffManager().isStaffEnabled(player) || instance.getStaffManager().isVanished(player)) {
            e.setCancelled(true);
            e.setFoodLevel(20);
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        Player player = e.getEntity();

        // They somehow died while in staff
        if (instance.getStaffManager().isStaffEnabled(player) || instance.getStaffManager().isVanished(player)) {
            e.getDrops().clear();
            e.setDroppedExp(0);
            player.chat("/staff");
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        if (player.hasPermission("azurite.staff") && instance.getStaffManager().getStaffConfig().getBoolean("STAFF_MODE.STAFF_MODE_ON_JOIN")) {
            TaskUtil.run(() -> instance.getStaffManager().enableStaff(player));
        }
    }

    @EventHandler
    public void onKickStaff(PlayerKickEvent e) {
        Player player = e.getPlayer();

        if (instance.getStaffManager().isStaffEnabled(player)) {
            instance.getStaffManager().disableStaff(player);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();

        if (instance.getStaffManager().isStaffEnabled(player)) {
            instance.getStaffManager().disableStaff(player);
        }
    }

    @EventHandler
    public void onPick(PlayerPickupItemEvent e) {
        Player player = e.getPlayer();

        if (instance.getStaffManager().isStaffEnabled(player)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlate(PlayerInteractEvent e) {
        if (e.getClickedBlock() == null) return;
        if (e.getAction() != Action.PHYSICAL) return;

        Player player = e.getPlayer();

        if (instance.getStaffManager().isStaffEnabled(player) || instance.getStaffManager().isVanished(player)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onKick(PlayerKickEvent e) {
        Player player = e.getPlayer();

        if (instance.getStaffManager().isStaffEnabled(player)) {
            player.chat("/staff");
        }
    }

    @EventHandler
    public void onJoinVanish(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        if (player.hasPermission("azurite.vanish")) return;

        for (UUID uuid : instance.getStaffManager().getVanished()) {
            Player vanished = Bukkit.getPlayer(uuid);

            if (vanished != null) {
                player.hidePlayer(vanished);
            }
        }
    }

    private void handleClick(Player player) {
        ItemStack hand = getItemInHand(player);

        if (hand != null) {
            StaffItem staffItem = instance.getStaffManager().getItem(hand);

            // Not a staff item.
            if (staffItem == null) return;

            // Handle replacing of hand
            if (staffItem.getReplacement() != null) {
                for (StaffItem item : instance.getStaffManager().getStaffItems().values()) {
                    if (!item.getName().equals(staffItem.getReplacement())) continue;
                    setItemInHand(player, item.getItem());
                }
            }

            if (!staffItem.getCommand().isEmpty()) {
                player.chat(staffItem.getCommand());
            }

            if (staffItem.getAction() != null) {
                switch (staffItem.getAction()) {
                    case VANISH_OFF:
                        instance.getStaffManager().disableVanish(player);
                        break;

                    case VANISH_ON:
                        instance.getStaffManager().enableVanish(player);
                        break;
                }
            }
        }
    }
    public ItemStack getItemInHand(Player player) {
        ItemStack hand = player.getInventory().getItemInHand();

        if (hand.getType() == Material.AIR) {
            return null;
        }

        return hand;
    }

    public void setItemInHand(Player player, ItemStack item) {
        player.getInventory().setItemInHand(item);
        player.updateInventory();
    }
}