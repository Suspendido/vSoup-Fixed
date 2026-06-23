package kami.gg.souppvp.command;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.perk.Perk;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.tier.TierCategory;
import kami.gg.souppvp.util.command.Command;
import kami.gg.souppvp.util.command.CommandManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class StatisticsCommand extends Command {

    private final DecimalFormat df;

    public StatisticsCommand(CommandManager manager) {
        super(manager, "statistics");
        this.df = new DecimalFormat("0.00");
    }

    @Override
    public List<String> aliases() {
        return Arrays.asList("statistic", "stats", "stat");
    }

    @Override
    public List<String> usage() {
        return Collections.singletonList("&cUsage: /stats <player>");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 1) {
            sendUsage(sender);
            return;
        }

        String targetName = args[0];
        Player player = (Player) sender;
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByName(targetName);

        if (profile == null) {
            sendMessage(player, "&cCouldn't resolve that player's name.");
            return;
        }

        boolean isSelf = sender.getName().equalsIgnoreCase(profile.getUsername());
        Perk incognito = SoupPvP.getInstance().getPerksHandler().getPerkByName("Incognito");
        String activePerkName = profile.getActivePerks().size() > 2 ? profile.getActivePerks().get(2) : null;
        Perk activePerk = activePerkName != null ? SoupPvP.getInstance().getPerksHandler().getPerkByName(activePerkName) : null;

        sendMessage(player, " ");
        sendMessage(player, isSelf ? "&bYour Statistics:" : "&b" + profile.getUsername() + "'s Statistics:");
        sendMessage(player, " &fKills: &b" + profile.getKills());
        sendMessage(player, " &fDeaths: &b" + profile.getDeaths());

        if (profile.getDeaths() == 0) {
            sendMessage(player, " &fKDR: &6Infinity");
        } else {
            double kdr = (double) profile.getKills() / profile.getDeaths();
            String color = kdr >= 1 ? "&a" : "&c";
            sendMessage(player, " &fKDR: " + color + df.format(kdr));
        }

        if (activePerk != incognito) {
            sendMessage(player, " &fCurrent Killstreak: &b" + profile.getCurrentKillstreak());
        }

        sendMessage(player, " &fHighest Killstreak: &b" + profile.getHighestKillstreak());
        sendMessage(player, " &fCredits: &b" + profile.getCredits());
        TierCategory category = TierCategory.getCategoryByName(profile.getSelectedTierIcon());
        sendMessage(player, " &fTier: &7" + profile.getTier().getTierLevel() + category.getFormattedIcon());

        if (profile.getBounty() > 0) {
            sendMessage(player, " &fBounty: &b" + profile.getBounty());
        }

        sendMessage(player, " ");
    }
}
