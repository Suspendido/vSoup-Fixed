package kami.gg.souppvp.kit.ability.impl;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.kit.Kit;
import kami.gg.souppvp.kit.ability.KitAbility;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.profile.ProfileState;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.List;

public class CopyCatAbility implements KitAbility {

    @Override
    public String getName() {
        return "CopyCat";
    }

    @Override
    public String getDescription() {
        return "&fCopy your victim's kit on kill";
    }

    @Override
    public String getColor() {
        return "&e";
    }

    @Override
    public ItemStack getItem() {
        return new ItemBuilder(Material.MONSTER_EGG)
                .durability((short) 98)
                .name("&e&lCopy Cat")
                .lore(
                        "&7Start with Default kit",
                        "&7Copy victim's kit on kill"
                )
                .build();
    }

    @Override
    public void onKitSelect(Player player) {
        player.setMetadata("CopyCat", new FixedMetadataValue(SoupPvP.getInstance(), true));
    }

    @Override
    public void onKitDeselect(Player player) {
        try {
            player.removeMetadata("CopyCat", SoupPvP.getInstance());
        } catch (Exception ignore) {}
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();

        if (killer == null) return;

        SoupPvP plugin = SoupPvP.getInstance();

        Profile killerProfile = plugin.getProfilesHandler().getProfileByUUID(killer.getUniqueId());
        Profile victimProfile = plugin.getProfilesHandler().getProfileByUUID(victim.getUniqueId());

        if (killerProfile == null || victimProfile == null) return;
        if (killerProfile.isInEvent() || killerProfile.getProfileState() == ProfileState.SPAWN) return;

        Kit victimKit = plugin.getKitsHandler().getKitByName(victimProfile.getCurrentKit());

        if (victimKit == null) {
            killer.sendMessage(CC.t("&cFailed to CopyCat " + victim.getName() + "'s kit (invalid kit)."));
            killer.playSound(killer.getLocation(), Sound.DIG_GRASS, 1F, 1F);
            return;
        }

        if (!isKitUnlocked(killerProfile, victimKit)) {
            killer.sendMessage(CC.t("&cYou don't have " + victim.getName() + "'s &f" + victimKit.getName() + " &ckit unlocked!"));
            killer.playSound(killer.getLocation(), Sound.DIG_GRASS, 1F, 1F);
            return;
        }

        copyVictimKit(killer, killerProfile, victimKit);
        killer.playSound(killer.getLocation(), Sound.CAT_MEOW, 1F, 1F);

        Bukkit.getScheduler().runTaskLater(plugin, killerProfile::saveProfile, 10L);

        if (victim.hasMetadata("CopyCat")) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> victim.playSound(victim.getLocation(), Sound.CAT_HISS, 1F, 1F), 40L);
        }
    }

    private boolean isKitUnlocked(Profile killerProfile, Kit victimKit) {
        List<String> unlockedKits = killerProfile.getUnlockedKits();
        if (unlockedKits == null) return false;

        for (String unlockedKit : unlockedKits) {
            if (unlockedKit.equalsIgnoreCase(victimKit.getName())) {
                return true;
            }
        }

        return false;
    }

    private void copyVictimKit(Player killer, Profile killerProfile, Kit victimKit) {
        killer.getInventory().clear();
        killer.getInventory().setArmorContents(null);
        killerProfile.setPreviousKit(this.getName());
        killerProfile.setCurrentKit(victimKit.getName());
        victimKit.equipKit(killer);
    }
}
