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

public class MilkBucketButton extends Button {

    private final int costCredits;

    public MilkBucketButton(Integer costCredits) {
        this.costCredits = costCredits;
    }

    @Override
    public ItemStack getButtonItem(Player player) {
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        List<String> lore = new ArrayList<>();
        lore.add("&7Receive 1x milk bucket. Drinking this");
        lore.add("&7clears out all your debuffed effects");
        lore.add("");
        lore.add("&fPrice: &b" + costCredits);
        lore.add("");
        lore.add(profile.getCredits() >= costCredits
                ? "&eClick to purchase!"
                : "&cInsufficient Credits!"
        );

        return new ItemBuilder(Material.MILK_BUCKET)
                .name("&bMilk Bucket")
                .lore(lore)
                .build();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());

        if (SoupPvP.getInstance().getSpawnHandler().getCuboid().contains(player)) {
            PlayerUtil.playSound(player, Sound.DIG_GRASS);
            player.sendMessage(CC.translate("&cYou can't do this in spawn."));
            return;
        }

        if (profile.getCredits() < costCredits) {
            PlayerUtil.playSound(player, Sound.DIG_GRASS);
            sendMessage(player, "&cInsufficient credits.");
            return;
        }

        if (player.getInventory().firstEmpty() == -1) {
            playFail(player);
            sendMessage(player, "&cYour inventory is full!");
            return;
        }

        player.sendMessage(CC.translate("&aSuccessfully bought the &7Milk Bucket&a."));
        player.getInventory().addItem(new ItemBuilder(Material.MILK_BUCKET).amount(1).build());
        profile.setCredits(profile.getCredits() - costCredits);
        playSuccess(player);
    }

}
