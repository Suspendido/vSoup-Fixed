package kami.gg.souppvp.kit.ability.impl;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.kit.ability.KitAbility;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.profile.ProfileState;
import kami.gg.souppvp.util.*;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Setter
public class ZeusAbility implements KitAbility {

    private List<ItemStack> combatEquipments;

    @Override
    public String getName() {
        return "Zeus";
    }

    @Override
    public String getDescription() {
        return "Strike lightning on nearby players within 10 blocks";
    }

    @Override
    public String getColor() {
        return "&6";
    }

    @Override
    public ItemStack getItem() {
        return new ItemBuilder(Material.BLAZE_ROD)
                .name("&6&lZeus Lightning")
                .lore(
                        "&7Right-click to strike lightning",
                        "&7on nearby enemies"
                )
                .build();
    }

    @Override
    public void onKitSelect(Player player) {
        // Nothing special
    }

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());

        if (profile.isInEvent() || profile.getProfileState() == ProfileState.SPAWN) return;
        if (!event.getAction().equals(Action.RIGHT_CLICK_AIR) && !event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
        
        if (combatEquipments == null || combatEquipments.size() < 2) return;
        if (!player.getItemInHand().isSimilar(combatEquipments.get(1))) return;

        event.setCancelled(true);
        player.updateInventory();

        if (hasTimer(player.getUniqueId(), getName())) {
            player.sendMessage(CC.t("&cYou can't use this for another &e" + DurationFormatter.getRemaining(getRemaining(player.getUniqueId(), getName()), true) + "&c."));
            return;
        }

        if (isInSpawn(player, profile)) {
            player.sendMessage(CC.t("&cYou can't do this in spawn."));
            return;
        }

        // Apply cooldown immediately
        addTimer(player.getUniqueId(), getName(), TimeUnit.SECONDS.toMillis(45));
        XPBarTimer.runXpBar(player, 45);

        boolean hit = false;
        for (Player target : player.getWorld().getPlayers()) {
            if (target == player) continue;
            Profile tProfile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(target.getUniqueId());

            if (tProfile.getProfileState() == ProfileState.SPAWN) continue;
            if (target.getLocation().distance(player.getLocation()) > 10) continue;

            hit = true;

            target.damage(8, player);
            player.getWorld().strikeLightningEffect(target.getLocation());
        }

        if (!hit) {
            player.sendMessage(CC.t("&cNo players nearby."));
            // Refund cooldown if no players nearby
            SoupPvP.getInstance().getTimersHandler().removePlayerTimer(player.getUniqueId(), true);
            return;
        }

        PlayerUtil.playSound(player, Sound.AMBIENCE_THUNDER, 1.0);
    }

    private boolean isInSpawn(Player player, Profile profile) {
        return profile.getProfileState() == ProfileState.SPAWN && SoupPvP.getInstance().getSpawnHandler().getCuboid().contains(player);
    }

    private boolean hasTimer(UUID uuid, String name) {
        return SoupPvP.getInstance().getTimersHandler().hasTimer(uuid, name + " Charge", true);
    }

    private long getRemaining(UUID uuid, String name) {
        return SoupPvP.getInstance().getTimersHandler().getRemaining(uuid, name + " Charge", true);
    }

    private void addTimer(UUID uuid, String name, long cooldown) {
        SoupPvP.getInstance().getTimersHandler().addPlayerTimer(uuid, new kami.gg.souppvp.timer.Timer(name + " Charge", cooldown), true);
    }
}
