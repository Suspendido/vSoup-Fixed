package kami.gg.souppvp.shop.items.button;

import kami.gg.souppvp.SoupPvP;
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

import java.util.ArrayList;
import java.util.List;

public class SoupRefillButton extends Button {

    private Integer costCredits;

    public SoupRefillButton(Integer costCredits){
        this.costCredits = costCredits;
    }

    @Override
    public ItemStack getButtonItem(Player player) {
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        List<String> lore = new ArrayList<>();
        lore.add(CC.translate("&7Refills all your empty slots with soup."));
        lore.add("");
        lore.add(CC.translate("&fPrice: &b" + costCredits));
        lore.add("");
        if (profile.getCredits() >= costCredits){
            lore.add(CC.translate("&eClick to purchase!"));
        } else {
            lore.add(CC.translate("&cInsufficient Credits!"));
        }
        return new ItemBuilder(Material.MUSHROOM_SOUP).name(CC.translate("&bSoup Refill")).lore(lore).build();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        if (clickType.isLeftClick()){
            Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
            if (SoupPvP.getInstance().getSpawnHandler().getCuboid().contains(player)){
                PlayerUtil.playSound(player, Sound.DIG_GRASS);
                player.sendMessage(CC.translate("&cYou can't do this in spawn."));
            } else {
                if (profile.getCredits() > costCredits){
                    PlayerUtil.playSound(player, Sound.NOTE_PIANO);
                    profile.setCredits(profile.getCredits() - costCredits);
                    if (player.getInventory().firstEmpty() == -1){
                        playFail(player);
                        player.sendMessage(CC.translate("&cYour inventory is full!"));
                        return;
                    }
                    PlayerUtil.giveSoup(player);
                    player.sendMessage(CC.translate("&aSuccessfully bought the &bSoup Refill&a."));
                } else {
                    PlayerUtil.playSound(player, Sound.DIG_GRASS);
                }
            }
        }
    }

}
