package kami.gg.souppvp.bounty.button;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.ItemBuilder;
import kami.gg.souppvp.util.menu.Button;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class BountyPlayerButton extends Button {

    private final Player target;

    public BountyPlayerButton(Player target) {
        this.target = target;
    }

    @Override
    public ItemStack getButtonItem(Player viewer) {

        SoupPvP plugin = SoupPvP.getInstance();
        Profile profile = plugin.getProfilesHandler().getProfileByUUID(target.getUniqueId());

        List<String> lore = new ArrayList<>();
        lore.add("");

        String perk0 = profile.getActivePerks().get(0);
        String perk2 = profile.getActivePerks().get(2);

        boolean hasTrickster = perk0 != null && perk0.equalsIgnoreCase("Trickster");
        boolean hasIncognito = perk2 != null && perk2.equalsIgnoreCase("Incognito");

        if (hasTrickster) {
            lore.add(CC.translate("&fBounty: &b" + ThreadLocalRandom.current().nextInt(0, 1001)));
        } else {
            lore.add(CC.translate("&fBounty: &b" + profile.getBounty()));
        }

        if (!hasIncognito) {
            lore.add(CC.translate("&fCurrent Killstreak: &b" + profile.getCurrentKillstreak()));
        }

        lore.add(CC.translate("&fCurrent Kit: &b" + profile.getCurrentKit()));

        if (hasTrickster) {
            lore.add(CC.translate("&fCurrent Health: &f" + ThreadLocalRandom.current().nextInt(0, 11) + "&4❤"));
        } else {
            lore.add(CC.translate("&fCurrent Health: &f" + (int) (target.getHealth() / 2) + "&4❤"));
        }

        double distance = viewer.getLocation().distance(target.getLocation());
        lore.add(CC.translate("&fDistance: &b" + (int) distance));

        lore.add("");

        return new ItemBuilder(Material.SKULL_ITEM)
                .name(CC.translate("&b" + target.getName()))
                .lore(lore)
                .durability(3)
                .build();
    }
}
