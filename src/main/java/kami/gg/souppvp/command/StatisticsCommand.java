package kami.gg.souppvp.command;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.perk.Perk;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.command.Command;
import kami.gg.souppvp.util.command.CommandManager;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;

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
        return Collections.singletonList(CC.translate("&cUsage: /stats <player>"));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 1) {
            sendUsage(sender);
            return;
        }

        String targetName = args[0];
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByName(targetName);

        if (profile == null) {
            sender.sendMessage(CC.translate("&cCouldn't resolve that player's name."));
            return;
        }

        boolean isSelf = sender.getName().equalsIgnoreCase(profile.getUsername());
        Perk incognito = SoupPvP.getInstance().getPerksHandler().getPerkByName("Incognito");
        String activePerkName = profile.getActivePerks().size() > 2 ? profile.getActivePerks().get(2) : null;
        Perk activePerk = activePerkName != null ? SoupPvP.getInstance().getPerksHandler().getPerkByName(activePerkName) : null;

        sender.sendMessage(CC.translate(StringUtils.repeat("&7&m-", 53)));
        sender.sendMessage(CC.translate(isSelf ? "&bYour Statistics:" : "&b" + profile.getUsername() + "'s Statistics:"));
        sender.sendMessage(CC.translate(" &fKills: &b" + profile.getKills()));
        sender.sendMessage(CC.translate(" &fDeaths: &b" + profile.getDeaths()));

        if (profile.getDeaths() == 0) {
            sender.sendMessage(CC.translate(" &fKDR: &6Infinity"));
        } else {
            double kdr = (double) profile.getKills() / profile.getDeaths();
            String color = kdr >= 1 ? "&a" : "&c";
            sender.sendMessage(CC.translate(" &fKDR: " + color + df.format(kdr)));
        }

        if (activePerk != incognito) {
            sender.sendMessage(CC.translate(" &fCurrent Killstreak: &b" + profile.getCurrentKillstreak()));
        }

        sender.sendMessage(CC.translate(" &fHighest Killstreak: &b" + profile.getHighestKillstreak()));
        sender.sendMessage(CC.translate(" &fCredits: &b" + profile.getCredits()));
        sender.sendMessage(CC.translate(" &fTier: &7" + profile.getTier().getDisplay() + "✫"));

        if (profile.getBounty() > 0) {
            sender.sendMessage(CC.translate(" &fBounty: &b" + profile.getBounty()));
        }

        sender.sendMessage(CC.translate(StringUtils.repeat("&7&m-", 53)));
    }
}
