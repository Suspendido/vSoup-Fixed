package kami.gg.souppvp.feats.leaderboard.menu;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.feats.leaderboard.LeaderboardManager;
import kami.gg.souppvp.feats.leaderboard.LeaderboardType;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.util.ItemBuilder;
import kami.gg.souppvp.util.menu.Button;
import kami.gg.souppvp.util.menu.Menu;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class LeaderboardMenu extends Menu {

    public LeaderboardMenu(Player player) {
        super(player, "Server Leaderboard", 27, true);
    }

    @Override
    public Map<Integer, Button> getButtons() {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(10, new PlayerStatsButton());
        buttons.put(12, new LeaderboardCategoryButton(LeaderboardType.KILLS, Material.DIAMOND_SWORD));
        buttons.put(13, new LeaderboardCategoryButton(LeaderboardType.DEATHS, Material.SKULL_ITEM));
        buttons.put(14, new LeaderboardCategoryButton(LeaderboardType.KDR, Material.GOLD_SWORD));
        buttons.put(15, new LeaderboardCategoryButton(LeaderboardType.KILLSTREAK, Material.BLAZE_POWDER));
        buttons.put(16, new LeaderboardCategoryButton(LeaderboardType.CREDITS, Material.GOLD_NUGGET));

        setFillEnabled(true);
        return buttons;
    }

    private static class PlayerStatsButton extends Button {
        @Override
        public void clicked(Player player, ClickType clickType) {
            playNeutral(player);
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
            if (profile == null) {
                return new ItemBuilder(Material.BARRIER)
                        .name("&c&lError")
                        .lore("&cCould not load your profile")
                        .build();
            }

            LeaderboardManager lbManager = SoupPvP.getInstance().getLeaderboardManager();

            int killsPos = lbManager.getPosition(LeaderboardType.KILLS, player.getUniqueId());
            int deathsPos = lbManager.getPosition(LeaderboardType.DEATHS, player.getUniqueId());
            int kdrPos = lbManager.getPosition(LeaderboardType.KDR, player.getUniqueId());
            int streakPos = lbManager.getPosition(LeaderboardType.KILLSTREAK, player.getUniqueId());
            int creditsPos = lbManager.getPosition(LeaderboardType.CREDITS, player.getUniqueId());

            double kdr = profile.getDeaths() == 0 ? profile.getKills() : (double) profile.getKills() / profile.getDeaths();

            return new ItemBuilder(Material.SKULL_ITEM)
                    .durability(3)
                    .setSkullOwner(player.getName())
                    .name("&b&lYour Statistics")
                    .lore(
                            "",
                            "&fYour Rankings&7:",
                            "&b┃ &fKills: &a#" + killsPos + " &7(" + profile.getKills() + ")",
                            "&b┃ &fDeaths: &a#" + deathsPos + " &7(" + profile.getDeaths() + ")",
                            "&b┃ &fKDR: &a#" + kdrPos + " &7(" + String.format("%.2f", kdr) + ")",
                            "&b┃ &fKillstreak: &a#" + streakPos + " &7(" + profile.getCurrentKillstreak() + ")",
                            "&b┃ &fCredits: &a#" + creditsPos + " &7(" + profile.getCredits() + ")",
                            ""
                    )
                    .build();
        }
    }

    @AllArgsConstructor
    private static class LeaderboardCategoryButton extends Button {
        private final LeaderboardType type;
        private final Material icon;

        @Override
        public void clicked(Player player, ClickType clickType) {
            playNeutral(player);
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            LeaderboardManager lbManager = SoupPvP.getInstance().getLeaderboardManager();
            List<LeaderboardManager.LeaderboardEntry> top10 = lbManager.getTop(type, 10);

            List<String> lore = new ArrayList<>();
            lore.add("&8Displaying top 10 players based on " + type.getDisplayName() + ".");
            lore.add("");

            if (top10.isEmpty()) {
                lore.add("&c&oNo data available");
            } else {
                for (int i = 0; i < top10.size(); i++) {
                    LeaderboardManager.LeaderboardEntry entry = top10.get(i);
                    String color = i == 0 ? "&6" : "&7";
                    String position = color + (i + 1) + ".";
                    String name = "&f" + entry.getName();

                    lore.add(" " + position + " " + name + " &8- " + type.getColor() + entry.getFormattedValue(type));
                }
            }

            lore.add("");
            lore.add("&7&oLeaderboards are live updated.");

            ItemBuilder builder = new ItemBuilder(icon)
                    .name("&b&l" + type.getDisplayName())
                    .lore(lore);

            // Configuración especial para skulls
            if (icon == Material.SKULL_ITEM) {
                builder.durability(1);
            }

            return builder.build();
        }
    }
}