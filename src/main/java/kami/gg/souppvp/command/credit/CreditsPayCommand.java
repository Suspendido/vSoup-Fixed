package kami.gg.souppvp.command.credit;

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

public class CreditsPayCommand extends Command {

    public CreditsPayCommand(CommandManager manager) {
        super(manager, "paycredits");
    }

    @Override
    public List<String> aliases() {
        return Collections.singletonList("pay");
    }

    @Override
    public List<String> usage() {
        return Collections.singletonList(CC.t("&cUsage: /paycredits <player> <int>"));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sendUsage(sender);
            return;
        }

        String s = args[0];
        Player player = (Player) sender;
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        Profile targetProfile = SoupPvP.getInstance().getProfilesHandler().getProfileByName(s);
        int amount = Integer.parseInt(args[1]);

        if (targetProfile == null) {
            sender.sendMessage(CC.t("Couldn't resolve that player's name."));
            return;
        }

        if (profile == targetProfile){
            sender.sendMessage(CC.t("&cYou can't send credits to yourself."));
        } else {
            if (profile.getCredits() < amount){
                sender.sendMessage(CC.t("&cInsufficient credits!"));
            } else {
                if (amount > 0){
                    targetProfile.setCredits(targetProfile.getCredits() + amount);
                    sender.sendMessage(CC.t("&aSuccessfully sent &e" + targetProfile.getUsername() + " &b" + amount + " &acredits."));
                    if (Bukkit.getPlayer(targetProfile.getUuid()) != null){
                        Bukkit.getPlayer(targetProfile.getUuid()).sendMessage(CC.t("&aYou've received &b" + amount + " &acredits from &e" + sender.getName() + "&a."));
                    }
                    profile.setCredits(profile.getCredits() - amount);
                } else {
                    sender.sendMessage(CC.t("&cThe amount has to be greater than zero!"));
                }
            }
        }
    }
}
