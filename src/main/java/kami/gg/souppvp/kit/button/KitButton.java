package kami.gg.souppvp.kit.button;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.kit.Kit;
import kami.gg.souppvp.kit.progress.KitProgress;
import kami.gg.souppvp.kit.menu.KitViewMenu;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.ItemBuilder;
import kami.gg.souppvp.util.PlayerUtil;
import kami.gg.souppvp.util.menu.Button;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class KitButton extends Button {

    private final Kit kit;

    public KitButton(Kit kit) {
        this.kit = kit;
    }

    @Override
    public ItemStack getButtonItem(Player player) {
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        String kitName = kit.getName();
        boolean unlocked = profile.getUnlockedKits().contains(kitName) || hasExactPermission(player, "souppvp." + kitName.toLowerCase());
        boolean freeMode = SoupPvP.getIsFreeKitsMode();
        KitProgress progress = profile.getKitProgress(kitName);

        List<String> lore = new ArrayList<>();

        lore.add("&b┃ &fRarity: " + kit.getRarityType().getColor() + kit.getRarityType().getName());
        lore.add("&b┃ &fStatus: " + (freeMode || unlocked ? "&aUnlocked" : "&cLocked"));

        if (freeMode || unlocked) {
            lore.add("");
            lore.add("&bKit Stats:");
            lore.add("&b┃ &fKills: &b" + progress.getKills());
            lore.add("&b┃ &fDeaths: &b" + progress.getDeaths());
            lore.add("&b┃ &fTimes Equipped: &b" + progress.getTimesUsed());
            lore.add("&b┃ &fLevel: &b" + progress.getLevel());

            if (progress.getRebirths() > 0) {
                lore.add("&b┃ &fRebirth: &d" + progress.getRebirths());
            }
        }

        if (!freeMode && !unlocked) {
            lore.add("");
            lore.add("&b┃ &fPrice: &c" + kit.getPrice());
        }

        lore.add("");

        lore.add(freeMode || unlocked ? "&aClick here to equip this kit." : (profile.getCredits() >= kit.getPrice() ? "&aClick here to purchase this kit." : "&cInsufficient Credits!"));
        lore.add("&eRight-Click to preview");

        return new ItemBuilder(kit.getIcon())
                .name(kit.getRarityType().getColor() + kitName)
                .lore(lore)
                .build();
    }


    @Override
    public void clicked(Player player, ClickType clickType) {
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        String kitName = kit.getName();
        boolean freeMode = SoupPvP.getIsFreeKitsMode();
        boolean unlocked = profile.getUnlockedKits().contains(kitName);

        if (clickType.isRightClick()) {
            PlayerUtil.playSound(player, Sound.CLICK, 1.0);
            new KitViewMenu(kit).openMenu(player);
            return;
        }

        if (!clickType.isLeftClick()) return;

        if (freeMode || unlocked) {
            kit.equipKit(player);
            PlayerUtil.playSound(player, Sound.CLICK, 1.0);
            profile.setPreviousKit(profile.getCurrentKit());
            profile.setCurrentKit(kitName);
            player.closeInventory();
            sendMessage(player, "&aSuccessfully equipped the &r" + kit.getRarityType().getColor() + kitName + "&a kit.");
            return;
        }

        if (profile.getCredits() < kit.getPrice()) {
            PlayerUtil.playSound(player, Sound.DIG_GRASS, 1.0);
            sendMessage(player, "&cNot enough credits!");
            return;
        }

        PlayerUtil.playSound(player, Sound.NOTE_PIANO, 1.0);
        profile.setCredits(profile.getCredits() - kit.getPrice());
        profile.getUnlockedKits().add(kitName);
        PlayerUtil.playSound(player, Sound.VILLAGER_YES, 1.0);
        player.sendMessage(CC.t("&aSuccessfully purchased the kit &r" + kit.getRarityType().getColor() + kitName + " &afor &6" + kit.getPrice() + " &acredits."));
    }

    private boolean hasExactPermission(Player player, String permission) {
        return player.getEffectivePermissions().stream().anyMatch(info -> info.getPermission().equalsIgnoreCase(permission) && info.getValue());
    }
}