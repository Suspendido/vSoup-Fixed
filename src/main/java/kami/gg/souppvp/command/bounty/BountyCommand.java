package kami.gg.souppvp.command.bounty;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.command.Command;
import kami.gg.souppvp.util.command.CommandManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class BountyCommand extends Command {

    public BountyCommand(CommandManager manager) {
        super(manager, "addbounty");
    }

    @Override
    public List<String> aliases() {
        return Collections.emptyList();
    }

    @Override
    public List<String> usage() {
        return Collections.singletonList(CC.translate("&cUsage: /addbounty <target> <int>"));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sendUsage(sender);
            return;
        }

        String s = args[0];
        Player player = (Player) sender;

        Profile setterProfile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        Profile targetProfile = SoupPvP.getInstance().getProfilesHandler().getProfileByName(s);

        if (targetProfile == null){
            sender.sendMessage(CC.translate("&cCouldn't resolve that player's name."));
            return;
        }
        int amount = Integer.parseInt(args[1]);
        if (setterProfile.getCredits() < amount) {
            sender.sendMessage(CC.translate("&cInsufficient credits!"));
        } else {
            if (amount <= 0){
                sender.sendMessage(CC.translate("&cThe amount has to be greater than zero!"));
            } else {
                Integer beforeBounty = targetProfile.getBounty();
                setterProfile.setCredits(setterProfile.getCredits() - amount);
                targetProfile.setBounty(amount + targetProfile.getBounty());
                Integer afterBounty = targetProfile.getBounty();
                Bukkit.broadcastMessage(CC.translate("&a" + sender.getName() + " &ehas upped the bounty on &a" + targetProfile.getUsername() + " &eto &a" + targetProfile.getBounty() + " (+" + (afterBounty - beforeBounty) + ") &ecredits."));
            }
        }
    }
}
