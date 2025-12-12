package kami.gg.souppvp.kit.button;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.kit.Kit;
import kami.gg.souppvp.kit.menu.KitViewMenu;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.ItemBuilder;
import kami.gg.souppvp.util.PlayerUtil;
import kami.gg.souppvp.util.TaskUtil;
import kami.gg.souppvp.util.menu.Button;
import kami.gg.souppvp.util.menu.menus.ConfirmMenu;
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
        boolean unlocked = profile.getUnlockedKits().contains(kitName) || player.hasPermission("souppvp." + kitName.toLowerCase());
        boolean freeMode = SoupPvP.getIsFreeKitsMode();

        List<String> lore = new ArrayList<>();
        lore.add(CC.MENU_BAR);

        kit.getDescription().forEach(line -> lore.add(CC.translate(line)));

        lore.add(CC.MENU_BAR);
        lore.add("");

        lore.add(CC.translate("&fStatus: " + (freeMode || unlocked ? "&aUnlocked" : "&cLocked")));

        if (!freeMode && !unlocked) {
            lore.add(CC.translate("&fPrice: &c" + kit.getPrice()));
        }

        lore.add(CC.translate("&fRarity: " + kit.getRarityType().getColor() + kit.getRarityType().getName()));
        lore.add("");

        if (freeMode || unlocked) {
            lore.add(CC.translate("&eClick here to equip this kit."));
        } else {
            lore.add(profile.getCredits() >= kit.getPrice()
                    ? CC.translate("&eClick here to purchase this kit.")
                    : CC.translate("&cInsufficient Credits!"));
        }

        return new ItemBuilder(kit.getIcon())
                .name(CC.translate(kit.getRarityType().getColor() + kitName))
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
            PlayerUtil.playSound(player, Sound.CLICK);
            new KitViewMenu(kit).openMenu(player);
            return;
        }

        if (!clickType.isLeftClick()) return;

        if (freeMode) {
            equip(player, profile, kitName);
            return;
        }

        if (unlocked) {
            equip(player, profile, kitName);
            return;
        }

        if (profile.getCredits() < kit.getPrice()) {
            PlayerUtil.playSound(player, Sound.DIG_GRASS);
            player.sendMessage(CC.translate("&cNot enough credits!"));
            return;
        }

        // ASK CONFIRMATION TO BUY
        PlayerUtil.playSound(player, Sound.NOTE_PIANO);
        new ConfirmMenu("Select a procedure action", confirmed -> {
            if (!confirmed) return;

            TaskUtil.runLater(player::closeInventory, 1L);

            PlayerUtil.playSound(player, Sound.NOTE_PIANO);

            // Purchase
            profile.setCredits(profile.getCredits() - kit.getPrice());
            profile.getUnlockedKits().add(kitName);

            // Equip
            profile.setPreviousKit(profile.getCurrentKit());
            profile.setCurrentKit(kitName);

            player.sendMessage(CC.translate("&aSuccessfully purchased the kit &r" + kit.getRarityType().getColor() + kitName + " &afor &6" + kit.getPrice() + " &acredits."));
            player.sendMessage(CC.translate("&aSuccessfully equipped the &r" + kit.getRarityType().getColor() + kitName + "&a kit."));
        }).openMenu(player);
    }

    private void equip(Player player, Profile profile, String kitName) {
        PlayerUtil.playSound(player, Sound.CLICK);
        profile.setPreviousKit(profile.getCurrentKit());
        profile.setCurrentKit(kitName);
        player.sendMessage(CC.translate("&aSuccessfully equipped the &r" + kit.getRarityType().getColor() + kitName + "&a kit."));
    }
}