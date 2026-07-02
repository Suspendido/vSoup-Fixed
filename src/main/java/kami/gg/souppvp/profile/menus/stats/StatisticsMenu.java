package kami.gg.souppvp.profile.menus.stats;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.kit.Kit;
import kami.gg.souppvp.kit.progress.KitProgress;
import kami.gg.souppvp.perk.Perk;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.tier.TierCategory;
import kami.gg.souppvp.tier.util.TierUtils;
import kami.gg.souppvp.util.Formatter;
import kami.gg.souppvp.util.ItemBuilder;
import kami.gg.souppvp.util.menu.Button;
import kami.gg.souppvp.util.menu.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class StatisticsMenu extends Menu {

    private final Profile profile;
    private final boolean isSelf;

    public StatisticsMenu(Player player, Profile profile) {
        super(player, profile.getUsername() + "'s Statistics", 45, true);
        this.profile = profile;
        this.isSelf = player.getName().equalsIgnoreCase(profile.getUsername());
    }

    @Override
    public Map<Integer, Button> getButtons() {
        Map<Integer, Button> buttons = new HashMap<>();
        double kdr = profile.getDeaths() == 0 ? profile.getKills() : (double) profile.getKills() / profile.getDeaths();
        String color = kdr >= 1 ? "&a" : "&c";
        String kdrText = profile.getDeaths() == 0 ? "Infinity" : String.format("%.2f", kdr);
        String mostUsedKit = getMostUsedKit();
        String mostKillsKit = getMostKillsKit();
        String mostDeathsKit = getMostDeathsKit();
        TierCategory category = TierCategory.getCategoryByName(profile.getSelectedTierIcon());

        buttons.put(4, new PlayerInfoButton());

        buttons.put(12, new StatButton(Material.DIAMOND_SWORD, "&b&lKills", List.of("&fTotal players eliminated", "&f" + Formatter.formatBalance(profile.getKills()))));
        buttons.put(13, new StatButton(Material.SKULL_ITEM, "&b&lDeaths", List.of("&fTotal times eliminated", "&f" + Formatter.formatBalance(profile.getDeaths()))));
        buttons.put(14, new StatButton(Material.GOLD_SWORD, "&b&lKDR", List.of("&fKill to death ratio", color + kdrText)));

        buttons.put(20, new StatButton(Material.BLAZE_POWDER, "&b&lCurrent Killstreak", List.of("&fCurrent killstreak", "&f" + Formatter.formatBalance(profile.getCurrentKillstreak()))));
        buttons.put(21, new StatButton(Material.BLAZE_ROD, "&b&lBest Killstreak", List.of("&fBest killstreak", "&f" + Formatter.formatBalance(profile.getHighestKillstreak()))));
        buttons.put(22, new StatButton(Material.GOLD_INGOT, "&b&lCredits", List.of("&fTotal credits earned", "&f" + Formatter.formatBalance(profile.getCredits()))));
        buttons.put(23, new StatButton(Material.ENCHANTMENT_TABLE, "&b&lCurrent experience", List.of("&fProgress to next level", "&f" + profile.getExperiences() + " &8/ &f" + TierUtils.calculateNextTierXP(profile.getTier()))));
        buttons.put(24, new StatButton(category, profile.getTier()));
        buttons.put(25, new StatButton(Material.EMERALD, "&b&lEvents Won", List.of("&fTotal events won", "&f" + profile.getEventsWon())));

        buttons.put(30, new StatButton(Material.DIAMOND_CHESTPLATE, "&b&lMost Kills Kit", List.of("&fKit with the most kills", mostKillsKit)));
        buttons.put(31, new StatButton(Material.COMPASS, "&b&lMost Used Kit", List.of("&fKit most used", mostUsedKit)));
        buttons.put(32, new StatButton(Material.BONE, "&b&lMost Deaths Kit", List.of("&fKit with the most deaths", mostDeathsKit)));

        setFillEnabled(true);
        return buttons;
    }

    private String getMostUsedKit() {
        String mostUsed = "None";
        String mostUsedColor = "&c";
        int maxUses = 0;

        for (Map.Entry<String, KitProgress> entry : profile.getKitProgress().entrySet()) {
            KitProgress progress = entry.getValue();
            if (progress.getTimesUsed() > maxUses) {
                maxUses = progress.getTimesUsed();
                mostUsed = capitalize(entry.getKey());
                mostUsedColor = getKitColor(entry.getKey());
            }
        }

        return mostUsedColor + mostUsed;
    }

    private String getMostKillsKit() {
        String mostKills = "None";
        String mostKillsColor = "&c";
        int maxKills = 0;

        for (Map.Entry<String, KitProgress> entry : profile.getKitProgress().entrySet()) {
            KitProgress progress = entry.getValue();
            if (progress.getKills() > maxKills) {
                maxKills = progress.getKills();
                mostKills = capitalize(entry.getKey());
                mostKillsColor = getKitColor(entry.getKey());
            }
        }

        return mostKillsColor + mostKills;
    }

    private String getMostDeathsKit() {
        String mostDeaths = "None";
        String mostDeathsColor = "&c";
        int maxDeaths = 0;

        for (Map.Entry<String, KitProgress> entry : profile.getKitProgress().entrySet()) {
            KitProgress progress = entry.getValue();
            if (progress.getDeaths() > maxDeaths) {
                maxDeaths = progress.getDeaths();
                mostDeaths = capitalize(entry.getKey());
                mostDeathsColor = getKitColor(entry.getKey());
            }
        }

        return mostDeathsColor + mostDeaths;
    }

    private String getKitColor(String kitName) {
        Kit kit = SoupPvP.getInstance().getKitsHandler().getKitByName(kitName);
        if (kit != null && kit.getRarityType() != null) {
            return "&" + kit.getRarityType().getColor().getChar();
        }
        return "&c";
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return "None";
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    private class PlayerInfoButton extends Button {
        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> lore = new ArrayList<>();
            double kdr = profile.getDeaths() == 0 ? profile.getKills() : (double) profile.getKills() / profile.getDeaths();
            String color = kdr >= 1 ? "&a" : "&c";
            String kdrText = profile.getDeaths() == 0 ? "Infinity" : String.format("%.2f", kdr);
            Perk incognito = SoupPvP.getInstance().getPerksHandler().getPerkByName("Incognito");
            String activePerkName = profile.getActivePerks().size() > 2 ? profile.getActivePerks().get(2) : null;
            Perk activePerk = activePerkName != null ? SoupPvP.getInstance().getPerksHandler().getPerkByName(activePerkName) : null;
            TierCategory category = TierCategory.getCategoryByName(profile.getSelectedTierIcon());

            lore.add("");
            lore.add("&b┃ &fKills: &b" + profile.getKills());
            lore.add("&b┃ &fDeaths: &b" + profile.getDeaths());
            lore.add("&b┃ &fKDR: " + color + kdrText);

            lore.add("&b┃ &fCredits: &b" + Formatter.formatBalance(profile.getCredits()));

            if (activePerk != incognito) {
                lore.add("&b┃ &fCurrent Killstreak: &b" + profile.getCurrentKillstreak());
            }

            lore.add("&b┃ &fHighest Killstreak: &b" + profile.getHighestKillstreak());

            lore.add("&b┃ &fTier: &7" + profile.getTier() + category.getFormattedIcon());

            if (profile.getBounty() > 0) {
                lore.add("&b┃ &fBounty: &b" + profile.getBounty());
            }
            
            lore.add("");

            return new ItemBuilder(Material.SKULL_ITEM)
                    .durability(3)
                    .setSkullOwner(profile.getUsername())
                    .name(isSelf ? "&b&lYour Statistics" : "&b&l" + profile.getUsername() + "'s Statistics")
                    .lore(lore)
                    .build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            playNeutral(player);
        }
    }

    private static class StatButton extends Button {
        private final Material material;
        private final String name;
        private final List<String> value;
        private final TierCategory category;
        private final Integer tier;

        public StatButton(Material material, String name, List<String> value) {
            this.material = material;
            this.name = name;
            this.value = value;
            this.category = null;
            this.tier = null;
        }

        public StatButton(TierCategory category, int tier) {
            this.material = category.getMaterial();
            this.name = "&b&lTier";
            this.value = List.of("&fCurrent Tier", category.getColor() + "[" + tier + category.getFormattedIcon() + "] " + category.getName());
            this.category = category;
            this.tier = tier;
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> lore = new ArrayList<>(value);

            ItemBuilder builder = new ItemBuilder(material)
                    .name(name)
                    .lore(lore);

            if (category != null && category.getMaterial() == Material.SKULL_ITEM && category.getTexture() != null) {
                builder.durability(3).setHeadTexture(category.getTexture());
            }

            return builder.build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            playNeutral(player);
        }
    }
}
