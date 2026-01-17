package kami.gg.souppvp.killstreak.inherit;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.killstreak.Killstreak;
import kami.gg.souppvp.perk.Perk;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.ItemBuilder;
import kami.gg.souppvp.util.PlayerUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class TwentyFiveKillstreak extends Killstreak implements Listener {

    private final SoupPvP plugin = SoupPvP.getInstance();

    @Override
    public String getName() {
        return "Full Repair II";
    }

    @Override
    public int getRequired() {
        return 25;
    }

    @Override
    public ItemStack getIcon() {
        return new ItemBuilder(Material.IRON_INGOT)
                .name(CC.translate("&a" + getName()))
                .lore(Arrays.asList(
                        CC.MENU_BAR,
                        "&7Fully repairs your armor, giving",
                        "&7them maximum durability.",
                        CC.MENU_BAR,
                        "",
                        "&fKillstreak Required: &d" + getRequired()
                )).build();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        if (killer == null) return;

        Profile profile = plugin.getProfilesHandler().getProfileByUUID(killer.getUniqueId());
        int required = getRequiredKillstreak(profile);

        if (profile.getCurrentKillstreak() != required) return;

        killer.sendMessage(CC.translate("&aYou've received the &d" + getName() + " &aperk for reaching a &d" + required + " &akillstreak!"));
        PlayerUtil.repairPlayer(killer);
    }

    private int getRequiredKillstreak(Profile profile) {
        Perk hardline = plugin.getPerksHandler().getPerkByName("Hardline");
        return (profile.getActivePerks().size() > 1 && plugin.getPerksHandler().getPerkByName(profile.getActivePerks().get(1)) == hardline) ? getRequired() - 1 : getRequired();
    }
}
