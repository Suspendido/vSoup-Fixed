package kami.gg.souppvp.killstreak.inherit;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.killstreak.Killstreak;
import kami.gg.souppvp.perk.Perk;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

public class ThirtyFiveKillstreak extends Killstreak implements Listener {

    @Override
    public String getName() {
        return "Extra Credits";
    }

    @Override
    public int getRequired() {
        return 35;
    }

    @Override
    public ItemStack getIcon() {
        return new ItemBuilder(Material.GOLD_INGOT)
                .name("&a" + getName())
                .lore(
                        CC.MENU_BAR,
                        "&7Receive an additional 1000",
                        "&7extra credits.",
                        CC.MENU_BAR,
                        "",
                        "&fKillstreak Required: &d" + getRequired(),
                        ""
                ).build();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeathEvent(PlayerDeathEvent event) {
        if (event.getEntity().getKiller() == null) return;

        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(event.getEntity().getKiller().getUniqueId());
        if (profile == null) return;
        Perk hardline = SoupPvP.getInstance().getPerksHandler().getPerkByName("Hardline");
        boolean hasHardline = false;

        if (profile.getActivePerks().size() > 1) {
            Perk active = SoupPvP.getInstance().getPerksHandler().getPerkByName(profile.getActivePerks().get(1));
            hasHardline = hardline.equals(active);
        }

        int requiredKills = hasHardline ? getRequired() - 1 : getRequired();
        if (profile.getCurrentKillstreak() != requiredKills) return;

        profile.setCredits(profile.getCredits() + 1000);
        event.getEntity().getKiller().sendMessage(CC.t("&aYou've received the &d" + getName() + " &aperk for reaching a &d" + getRequired() + " &akillstreak!"));
    }

}
