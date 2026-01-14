package kami.gg.souppvp.command;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.shop.ShopMenu;
import kami.gg.souppvp.shop.items.ItemsMenu;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.command.Command;
import kami.gg.souppvp.util.command.CommandManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class ShopCommand extends Command {

    public ShopCommand(CommandManager manager) {
        super(manager, "shop");
    }

    @Override
    public List<String> aliases() {
        return Collections.emptyList();
    }

    @Override
    public List<String> usage() {
        return null;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(CC.translate("&cOnly players can run this command."));
            return;
        }

        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());

        if (profile.isJuggernaut()){
            sender.sendMessage(CC.translate("&cYou may not visit the shop whilst in Juggernaut."));
            return;
        }

        new ShopMenu().openMenu(player);
    }
}