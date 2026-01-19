package kami.gg.souppvp.shop.bartender;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.shop.items.ItemsMenu;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.ItemBuilder;
import kami.gg.souppvp.util.PlayerUtil;
import kami.gg.souppvp.util.menu.Button;
import kami.gg.souppvp.util.menu.Menu;
import kami.gg.souppvp.util.menu.button.BackButton;
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
        return "Select a potion to buy";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttonMap = new HashMap<>();
        buttonMap.put(10, new PotionOfFireResistanceButton(250));
        buttonMap.put(12, new SplashPotionOfHarmingButton(500));
        buttonMap.put(14, new SplashPotionOfPoisonButton(750));
        buttonMap.put(16, new SplashPotionOfSlownessButton(1000));
        buttonMap.put(26, new BackButton(new ItemsMenu()));
        setPlaceholder(true);
        return buttonMap;
    }

    @Override
    public int size(Map<Integer, Button> buttons) {
        return 27;
    }

    public static class PotionOfFireResistanceButton extends Button {

        private final int costCredits;

        public PotionOfFireResistanceButton(int costCredits) {
            this.costCredits = costCredits;
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add("&fPrice: &b" + costCredits);
            lore.add("");
            lore.add(profile.getCredits() >= costCredits
                    ? "&eClick to purchase!"
                    : "&cInsufficient Credits!"
            );

            return new ItemBuilder(Material.POTION).name("&bPotion Of Fire Resistance").lore(lore).durability(8259).build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());

            if (SoupPvP.getInstance().getSpawnHandler().getCuboid().contains(player)) {
                PlayerUtil.playSound(player, Sound.DIG_GRASS);
                sendMessage(player, "&cYou can't do this in spawn.");
                return;
            }

            if (player.getInventory().firstEmpty() == -1) {
                playFail(player);
                sendMessage(player, "&cYour inventory is full!");
                return;
            }

            sendMessage(player, "&aSuccessfully bought the &bPotion Of Fire Resistance&a.");
            player.getInventory().addItem(new ItemStack(Material.POTION, 1, (short) 8227));
            profile.setCredits(profile.getCredits() - costCredits);
            playSuccess(player);
        }
    }

    public static class SplashPotionOfHarmingButton extends Button {

        private final int costCredits;

        public SplashPotionOfHarmingButton(int costCredits) {
            this.costCredits = costCredits;
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add("&fPrice: &b" + costCredits);
            lore.add("");
            lore.add(profile.getCredits() >= costCredits
                    ? "&eClick to purchase!"
                    : "&cInsufficient Credits!"
            );

            return new ItemBuilder(Material.POTION).name("&bSplash Potion Of Harming").lore(lore).durability(16428).build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());

            if (SoupPvP.getInstance().getSpawnHandler().getCuboid().contains(player)) {
                PlayerUtil.playSound(player, Sound.DIG_GRASS);
                sendMessage(player, "&cYou can't do this in spawn.");
                return;
            }

            if (player.getInventory().firstEmpty() == -1) {
                playFail(player);
                sendMessage(player, "&cYour inventory is full!");
                return;
            }

            sendMessage(player, "&aSuccessfully bought the &bSplash Potion Of Harming&a.");
            player.getInventory().addItem(new ItemStack(Material.POTION, 1, (short) 16428));
            profile.setCredits(profile.getCredits() - costCredits);
            PlayerUtil.playSound(player, Sound.NOTE_PIANO);
        }
    }

    public static class SplashPotionOfPoisonButton extends Button {

        private final int costCredits;

        public SplashPotionOfPoisonButton(int costCredits) {
            this.costCredits = costCredits;
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add("&fPrice: &b" + costCredits);
            lore.add("");
            lore.add(profile.getCredits() >= costCredits
                    ? "&eClick to purchase!"
                    : "&cInsufficient Credits!"
            );

            return new ItemBuilder(Material.POTION).name("&bSplash Potion Of Poison").lore(lore).durability(16420).build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());

            if (SoupPvP.getInstance().getSpawnHandler().getCuboid().contains(player)) {
                PlayerUtil.playSound(player, Sound.DIG_GRASS);
                sendMessage(player, "&cYou can't do this in spawn.");
                return;
            }

            if (player.getInventory().firstEmpty() == -1) {
                playFail(player);
                sendMessage(player, "&cYour inventory is full!");
                return;
            }

            sendMessage(player, "&aSuccessfully bought the &bSplash Potion Of Poison&a.");
            player.getInventory().addItem(new ItemStack(Material.POTION, 1, (short) 16420));
            profile.setCredits(profile.getCredits() - costCredits);
            playSuccess(player);
        }
    }

    public static class SplashPotionOfSlownessButton extends Button {

        private final int costCredits;

        public SplashPotionOfSlownessButton(int costCredits) {
            this.costCredits = costCredits;
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add("&fPrice: &b" + costCredits);
            lore.add("");
            lore.add(profile.getCredits() >= costCredits
                    ? "&eClick to purchase!"
                    : "&cInsufficient Credits!"
            );

            return new ItemBuilder(Material.POTION).name("&bSplash Potion Of Slowness").lore(lore).durability(16426).build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());

            if (SoupPvP.getInstance().getSpawnHandler().getCuboid().contains(player)) {
                PlayerUtil.playSound(player, Sound.DIG_GRASS);
                player.sendMessage(CC.translate("&cYou can't do this in spawn."));
                return;
            }

            if (player.getInventory().firstEmpty() == -1) {
                playFail(player);
                player.sendMessage(CC.translate("&cYour inventory is full!"));
                return;
            }

            player.sendMessage(CC.translate("&aSuccessfully bought the &bSplash Potion Of Slowness&a."));
            player.getInventory().addItem(new ItemStack(Material.POTION, 1, (short) 16426));
            profile.setCredits(profile.getCredits() - costCredits);
            playSuccess(player);
        }
    }
}
