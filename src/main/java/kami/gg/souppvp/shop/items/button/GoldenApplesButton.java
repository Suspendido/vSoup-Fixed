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

public class GoldenApplesButton extends Button {

    private final Integer costCredits;

    public GoldenApplesButton(Integer costCredits){
        this.costCredits = costCredits;
    }

    @Override
    public ItemStack getButtonItem(Player player) {
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        List<String> lore = new ArrayList<>();
        lore.add(CC.translate("&7Receive 3x golden apples."));
        lore.add("");
        lore.add(CC.translate("&fPrice: &b" + costCredits));
        lore.add("");
        if (profile.getCredits() >= costCredits){
            lore.add(CC.translate("&eClick to purchase!"));
        } else {
            lore.add(CC.translate("&cInsufficient Credits!"));
        }
        return new ItemBuilder(Material.GOLDEN_APPLE).name(CC.translate("&bGolden Apples")).lore(lore).build();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        if (clickType.isLeftClick()){
            Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
            if (SoupPvP.getInstance().getSpawnHandler().getCuboid().contains(player)){
                PlayerUtil.playSound(player, Sound.DIG_GRASS);
                player.sendMessage(CC.translate("&cYou can't do this in spawn."));
            } else {
                if (profile.getCredits() >= costCredits){
                    if (player.getInventory().firstEmpty() == -1){
                        playFail(player);
                        player.sendMessage(CC.translate("&cYour inventory is full, have one slot empty!"));
                        return;
                    }
                    player.getInventory().addItem(new ItemBuilder(Material.GOLDEN_APPLE).amount(3).build());
                    PlayerUtil.playSound(player, Sound.NOTE_PIANO);
                    profile.setCredits(profile.getCredits() - costCredits);
                    player.sendMessage(CC.translate("&aSuccessfully bought the &bGolden Apples&a."));
                } else {
                    PlayerUtil.playSound(player, Sound.DIG_GRASS);
                }
            }
        }
    }

}
