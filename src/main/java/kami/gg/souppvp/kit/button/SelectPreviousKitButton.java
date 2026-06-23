package kami.gg.souppvp.kit.button;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.kit.Kit;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.util.ItemBuilder;
import kami.gg.souppvp.util.PlayerUtil;
import kami.gg.souppvp.util.menu.Button;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class SelectPreviousKitButton extends Button {

    private final Profile profile;

    public SelectPreviousKitButton(Profile profile) {
        this.profile = profile;
    }

    @Override
    public ItemStack getButtonItem(Player player) {
        Kit current = SoupPvP.getInstance().getKitsHandler().getKitByName(profile.getCurrentKit());
        Kit previous = SoupPvP.getInstance().getKitsHandler().getKitByName(profile.getPreviousKit());

        List<String> lore = new ArrayList<>();
        lore.add("&7Receive your previous kit!");
        lore.add("");

        lore.add(previous == null ? "&fPrevious Kit: &cNone" : "&fPrevious Kit: &r" + previous.getRarityType().getColor() + previous.getName());

        lore.add("&fCurrent Kit: &r" + current.getRarityType().getColor() + current.getName());
        lore.add("");
        lore.add("&eClick to receive!");

        return new ItemBuilder(Material.WATCH)
                .name("&bSelect Previous Kit")
                .lore(lore)
                .build();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        if (!clickType.isLeftClick()) return;

        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        Kit previous = SoupPvP.getInstance().getKitsHandler().getKitByName(profile.getPreviousKit());

        if (previous == null) {
            playFail(player);
            sendMessage(player, "&cYou don't have a previous kit.");
            return;
        }

        Kit current = SoupPvP.getInstance().getKitsHandler().getKitByName(profile.getCurrentKit());

        PlayerUtil.playSound(player, Sound.CLICK, 1.0);

        // swap
        profile.setCurrentKit(previous.getName());
        profile.setPreviousKit(current.getName());

        sendMessage(player, "&aSuccessfully equipped the &r" + previous.getRarityType().getColor() + previous.getName() + "&a kit.");
    }
}
