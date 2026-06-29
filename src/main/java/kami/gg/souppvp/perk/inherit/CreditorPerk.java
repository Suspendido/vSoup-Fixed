package kami.gg.souppvp.perk.inherit;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.feats.actionbar.type.GeneralActionBar;
import kami.gg.souppvp.perk.Perk;
import kami.gg.souppvp.profile.Profile;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class CreditorPerk extends Perk implements Listener {

    @Override
    public String getName() {
        return "Creditor";
    }

    @Override
    public String getColor() {
        return "&b";
    }

    @Override
    public List<String> getDescription() {
        return List.of("&7Gain an additional 5 credits for", "&7every kill, but every death will", "&7result in 10 credits lost");
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.EMERALD);
    }

    @Override
    public int getCost() {
        return 1250;
    }

    @EventHandler
    public void onPlayerDeathEvent(PlayerDeathEvent event) {
        if (!(event.getEntity() instanceof Player victim)) return;

        Profile victimProfile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(victim.getUniqueId());
        if (victimProfile == null) return;
        if (victimProfile.isInEvent()) return;
        if (!victimProfile.getActivePerks().contains(getName())) return;

        victimProfile.setCredits(Math.max(0, victimProfile.getCredits() - 10));
        GeneralActionBar.sendMessage(victim, "&bCreditor: &c-10 Credits");

        Player killer = victim.getKiller();
        if (killer == null) return;

        Profile killerProfile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(killer.getUniqueId());
        if (killerProfile == null) return;
        if (killerProfile.isInEvent()) return;
        if (!killerProfile.getActivePerks().contains(getName())) return;

        killerProfile.setCredits(killerProfile.getCredits() + 5);
        GeneralActionBar.sendMessage(killer, "&bCreditor: &a+5 Credits");
    }

}
