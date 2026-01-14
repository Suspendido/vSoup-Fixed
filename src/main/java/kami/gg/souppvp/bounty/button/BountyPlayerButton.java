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
        String perk0 = profile.getActivePerks().getFirst();

        if (perk0 != null && perk0.equalsIgnoreCase("Trickster")) {
            lore.add(CC.translate(" &b▪ &fBounty: &b" + ThreadLocalRandom.current().nextInt(0, 1001)));
        } else {
            lore.add(CC.translate(" &b▪ &fBounty: &b" + profile.getBounty()));
        }

        return new ItemBuilder(Material.SKULL_ITEM)
                .name(CC.translate(target.getDisplayName()))
                .lore(lore)
                .durability(3)
                .setSkullOwner(target.getName())
                .build();
    }
}
