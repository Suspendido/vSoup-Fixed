package kami.gg.souppvp.killstreak.inherit;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.killstreak.Killstreak;
import kami.gg.souppvp.perk.Perk;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.ItemBuilder;
import kami.gg.souppvp.util.PlayerUtil;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

public class FiveKillstreak extends Killstreak implements Listener {

    @Override
    public String getName() {
        return "Full Repair I";
    }

    @Override
    public int getRequired() {
        return 5;
    }

    @Override
    public ItemStack getIcon() {
        return new ItemBuilder(Material.IRON_INGOT)
                .name("&a" + getName())
                .lore(
                        CC.MENU_BAR,
                        "&7Fully repairs your armor, giving",
                        "&7them maximum durability.",
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

        event.getEntity().getKiller().sendMessage(CC.t("&aYou've received the &d" + getName() + " &aperk for reaching a &d" + getRequired() + " &akillstreak!"));
        PlayerUtil.repairPlayer(event.getEntity().getKiller());
    }

}
