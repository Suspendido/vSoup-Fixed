package kami.gg.souppvp.options.button;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.tier.Tiers;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.ItemBuilder;
import kami.gg.souppvp.util.PlayerUtil;
import kami.gg.souppvp.util.TaskUtil;
import kami.gg.souppvp.util.menu.Button;
import kami.gg.souppvp.util.menu.menus.ConfirmMenu;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ResetStatisticsButton extends Button {

    private final int price;

    private static final List<String> DEFAULT_KITS = Collections.singletonList("Default");
    private static final List<String> DEFAULT_PERKS = Arrays.asList("None", "None", "None");

    public ResetStatisticsButton(int price) {
        this.price = price;
    }

    @Override
    public ItemStack getButtonItem(Player player) {

        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        boolean affordable = profile.getCredits() >= price;

        return new ItemBuilder(Material.REDSTONE_COMPARATOR)
                .name(CC.translate("&bReset Statistics"))
                .lore(
                        CC.translate("&7This procedure will reset all SoupPvP statistics."),
                        CC.translate("&7This does NOT affect your network rank or punishments."),
                        "",
                        CC.translate("&fPrice: &b" + price),
                        "",
                        affordable ? CC.translate("&eClick to reset!") : CC.translate("&cInsufficient Credits!")
                )
                .build();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        if (!clickType.isLeftClick()) return;

        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        SoupPvP instance = SoupPvP.getInstance();

        // Must be in spawn
        if (!instance.getSpawnHandler().getCuboid().contains(player)) {
            player.sendMessage(CC.translate("&cYou can only do this in spawn."));
            return;
        }

        // Must have money
        if (profile.getCredits() < price) {
            PlayerUtil.playSound(player, Sound.DIG_GRASS);
            player.sendMessage(CC.translate("&cInsufficient credits!"));
            return;
        }

        playNeutral(player);
        TaskUtil.runLater(player::closeInventory, 1L);
        PlayerUtil.playSound(player, Sound.NOTE_PIANO);

        // Reset kit
        profile.setCurrentKit(instance.getKitsHandler().getKitByName("Default").getName());
        profile.setPreviousKit(profile.getCurrentKit());

        // Reset unlocked kits
        profile.setUnlockedKits(DEFAULT_KITS);

        // Reset stats
        profile.setKills(0);
        profile.setDeaths(0);
        profile.setCredits(0);
        profile.setExperiences(0);
        profile.setTier(Tiers.ZERO);

        profile.setCurrentKillstreak(0);
        profile.setHighestKillstreak(0);

        // Perks
        profile.setActivePerks(DEFAULT_PERKS);
        profile.setUnlockedPerks(Collections.emptyList());

        // Wagers
        profile.setTotalWagerGames(0);
        profile.setWagersWon(0);
        profile.setWagersLost(0);

        player.sendMessage(CC.translate("&aSuccessfully reset your statistics."));
    }
}
