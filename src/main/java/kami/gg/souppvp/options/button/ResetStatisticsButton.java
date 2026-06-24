package kami.gg.souppvp.options.button;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.util.ItemBuilder;
import kami.gg.souppvp.util.PlayerUtil;
import kami.gg.souppvp.util.TaskUtil;
import kami.gg.souppvp.util.menu.Button;
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

        return new ItemBuilder(Material.REDSTONE_COMPARATOR)
                .name("&bReset Statistics")
                .lore(
                        "&fThis procedure will reset all SoupPvP statistics.",
                        "&fThis does NOT affect your network rank or punishments.",
                        "",
                        "&fPrice: &b" + price,
                        "",
                        profile.getCredits() >= price ? "&eClick to reset!" : "&cInsufficient Credits!"
                ).build();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        SoupPvP instance = SoupPvP.getInstance();
        Profile profile = instance.getProfilesHandler().getProfileByUUID(player.getUniqueId());

        // Must be in spawn
        if (!instance.getSpawnHandler().getCuboid().contains(player)) {
            playFail(player);
            sendMessage(player, "&cYou can only do this in spawn.");
            return;
        }

        // Must have money
        if (profile.getCredits() < price) {
            playFail(player);
            sendMessage(player, "&cInsufficient credits!");
            return;
        }

        playSuccess(player);
        TaskUtil.runLater(player::closeInventory, 1L);
        PlayerUtil.playSound(player, Sound.NOTE_PIANO, 1.0);

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
        profile.setTier(0);

        profile.setCurrentKillstreak(0);
        profile.setHighestKillstreak(0);

        // Perks
        profile.setActivePerks(DEFAULT_PERKS);
        profile.setUnlockedPerks(Collections.emptyList());

        // Wagers
        profile.setTotalWagerGames(0);
        profile.setWagersWon(0);
        profile.setWagersLost(0);

        sendMessage(player, "&aSuccessfully reset your statistics.");
    }
}
