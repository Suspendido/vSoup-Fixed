package kami.gg.souppvp.kit.button;

import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.ItemBuilder;
import kami.gg.souppvp.util.menu.Button;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class YourStatisticsButton extends Button {

    private final Profile profile;
    private static final DecimalFormat df = new DecimalFormat("0.00");

    public YourStatisticsButton(Profile profile){
        this.profile = profile;
    }

    @Override
    public ItemStack getButtonItem(Player player) {
        List<String> lore = new ArrayList<>();
        lore.add(CC.MENU_BAR);
        lore.add(CC.translate("&bYour Statistics"));
        lore.add(CC.translate(" &fKills: &b" + profile.getKills()));
        lore.add(CC.translate(" &fDeaths: &b" + profile.getDeaths()));
        if (profile.getDeaths() == 0){
            lore.add(CC.translate(" &fKDR: &6Infinity"));
        } else {
            double kdr = (double) profile.getKills() / (double) profile.getDeaths();
            String context = kdr >= 1 ? "&a" : "&c";
            lore.add(CC.translate(" &fKDR: " + context + df.format(kdr)));
        }
        lore.add(CC.translate(" &fCurrent Killstreak: &b" + profile.getCurrentKillstreak()));
        lore.add(CC.translate(" &fHighest Killstreak: &b" + profile.getHighestKillstreak()));
        lore.add(CC.translate(" &fCredits: &b" + profile.getCredits()));
        lore.add(CC.translate(" &fTier: &7" + profile.getTier().getDisplay()) + "✫");
        if (profile.getBounty() > 0){
            lore.add(CC.translate(" &fBounty: &b" + profile.getBounty()));
        }
        lore.add(CC.MENU_BAR);
        return new ItemBuilder(Material.SKULL_ITEM).durability(3).name(" ").lore(lore).build();
    }
}
