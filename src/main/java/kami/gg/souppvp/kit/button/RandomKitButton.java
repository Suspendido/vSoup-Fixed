package kami.gg.souppvp.kit.button;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.kit.Kit;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.ItemBuilder;
import kami.gg.souppvp.util.PlayerUtil;
import kami.gg.souppvp.util.menu.Button;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RandomKitButton extends Button {

    @Override
    public ItemStack getButtonItem(Player player) {
        return new ItemBuilder(Material.JUKEBOX)
                .name(CC.translate("&bRandomise A Kit"))
                .lore(
                        CC.translate("&7Receive a random kit you own!"),
                        "",
                        CC.translate("&eClick to randomize!")
                )
                .build();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {

        if (!clickType.isLeftClick()) return;

        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        List<String> unlocked = profile.getUnlockedKits();

        if (unlocked == null || unlocked.isEmpty()) {
            player.sendMessage(CC.translate("&cYou don't own any kits to randomize."));
            return;
        }

        String kitName = unlocked.get(ThreadLocalRandom.current().nextInt(unlocked.size()));
        Kit kit = SoupPvP.getInstance().getKitsHandler().getKitByName(kitName);

        if (kit == null) {
            player.sendMessage(CC.translate("&cThis kit no longer exists in the system."));
            return;
        }

        profile.setPreviousKit(profile.getCurrentKit());
        profile.setCurrentKit(kit.getName());

        PlayerUtil.playSound(player, Sound.CLICK);
        player.sendMessage(CC.translate("&aSuccessfully equipped the &r" + kit.getRarityType().getColor() + kit.getName() + "&a kit."));
    }
}
