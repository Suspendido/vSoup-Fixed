package kami.gg.souppvp.command.admin;

import kami.gg.souppvp.killstreak.menu.editor.KillstreakEditorMenu;
import kami.gg.souppvp.lang.Lang;
import kami.gg.souppvp.util.command.Command;
import kami.gg.souppvp.util.command.CommandManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class KillstreakEditorCommand extends Command {

    public KillstreakEditorCommand(CommandManager manager) {
        super(manager, "killstreakeditor");
        this.setPermissible("souppvp.admin");
    }

    @Override
    public List<String> aliases() {
        return List.of("kseditor", "ksedit", "killstreakedit");
    }

    @Override
    public List<String> usage() {
        return List.of();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sendMessage(sender, Lang.ONLY_PLAYERS);
            return;
        }

        if (!player.hasPermission("souppvp.admin")) {
            sendMessage(player, "&cYou don't have permission to use this command.");
            return;
        }

        new KillstreakEditorMenu(player).open();
    }
}
