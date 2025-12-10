package kami.gg.souppvp.shop.bartender;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.shop.bartender.button.SplashPotionOfHarmingButton;
import kami.gg.souppvp.shop.bartender.button.SplashPotionOfPoisonButton;
import kami.gg.souppvp.shop.bartender.button.SplashPotionOfSlownessButton;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.ItemBuilder;
import kami.gg.souppvp.util.PlayerUtil;
import kami.gg.souppvp.util.menu.Button;
import kami.gg.souppvp.util.menu.Menu;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BartenderMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return CC.translate("Select a potion to buy");
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttonMap = new HashMap<>();
        buttonMap.put(10, new PotionOfFireResistanceButton(250));
        buttonMap.put(12, new SplashPotionOfHarmingButton(500));
        buttonMap.put(14, new SplashPotionOfPoisonButton(750));
        buttonMap.put(16, new SplashPotionOfSlownessButton(1000));
        setPlaceholder(true);
        return buttonMap;
    }

    @Override
    public int size(Map<Integer, Button> buttons) {
        return 27;
    }

    public static class PotionOfFireResistanceButton extends Button {

        private final Integer costCredits;

        public PotionOfFireResistanceButton(Integer costCredits) {
            this.costCredits = costCredits;
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add(CC.translate("&fPrice: &b" + costCredits));
            lore.add("");
            if (profile.getCredits() >= costCredits) {
                lore.add(CC.translate("&eClick to purchase!"));
            } else {
                lore.add(CC.translate("&cInsufficient Credits!"));
            }
            return new ItemBuilder(Material.POTION).name("&bPotion Of Fire Resistance").lore(lore).durability(8227).build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            if (clickType.isLeftClick()) {
                Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
                if (SoupPvP.getInstance().getSpawnHandler().getCuboid().contains(player)) {
                    PlayerUtil.playSound(player, Sound.DIG_GRASS);
                    player.sendMessage(CC.translate("&cYou can't do this in spawn."));
                } else {
                    if (profile.getCredits() >= costCredits) {
                        if (player.getInventory().firstEmpty() == -1) {
                            playFail(player);
                            player.sendMessage(CC.translate("&cYour inventory is full!"));
                            return;
                        }
                        player.getInventory().addItem(new ItemStack(Material.POTION, 1, (short) 8227));
                        PlayerUtil.playSound(player, Sound.NOTE_PIANO);
                        profile.setCredits(profile.getCredits() - costCredits);
                        player.sendMessage(CC.translate("&aSuccessfully bought the &bPotion Of Fire Resistance&a."));
                    } else {
                        PlayerUtil.playSound(player, Sound.DIG_GRASS);
                    }
                }
            }
        }
    }
}
