package kami.gg.souppvp.command;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.command.Command;
import kami.gg.souppvp.util.command.CommandManager;
import kami.gg.souppvp.util.command.extra.TabCompletion;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SoupPvPCommand extends Command {

    public SoupPvPCommand(CommandManager manager) {
        super(manager, "souppvp");
        this.completions.add(new TabCompletion(Arrays.asList("reload", "save", "version", "ver"), 0));
        this.setPermissible("souppvp.reload");
    }

    @Override
    public List<String> aliases() {
        return Collections.emptyList();
    }

    @Override
    public List<String> usage() {
        return CC.translate(Arrays.asList(
                CC.LINE,
                "&6&lSoupPvP Core",
                "&fThis server is using &6SoupPvP Core",
                "",
                "&e/souppvp reload &8- &fReloads all the config.",
                "&e/souppvp version &8- &fGets the plugin current version.",
                "&e/souppvp save &8- &fSaves the config.",
                CC.LINE
        ));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sendUsage(sender);
            return;
        }

        if (!sender.hasPermission(permissible)) return;
        switch (args[0].toLowerCase()) {
            case "reload":
                if (sender.hasPermission("souppvp.reload")) {
                    sender.sendMessage(CC.translate("&7[&6&lSoupPvP&7] &eStarting config reload..."));
                    long startTime = System.currentTimeMillis();

                    SoupPvP.getInstance().getTablistManager().reload();
                    SoupPvP.getInstance().getScoreboardManager().reload();
                    SoupPvP.getInstance().getNametagManager().reload();

                    long endTime = System.currentTimeMillis();
                    sender.sendMessage(CC.translate("&7[&6&lSoupPvP&7] &aConfigs reloaded! &7(" + (endTime - startTime) + "ms)"));
                }
                return;

            case "ver":
            case "version":
                sendMessage(sender, "&7[&6&lSoupPvP&7] &eCurrent version is &a" + SoupPvP.getInstance().getDescription().getVersion() + "&e.");
                return;

            case "save":
                for (Profile profile : SoupPvP.getInstance().getProfilesHandler().getProfiles()) {
                    profile.saveProfile();
                }
                Long started = System.currentTimeMillis();
                Long ended = System.currentTimeMillis();
                sendMessage(sender, CC.translate("&7[&6&lSoupPvP&7] &aSuccessfully saved a total of &a&l" + SoupPvP.getInstance().getProfilesHandler().getProfiles().size() + "&a profiles within &a&l" + (ended - started) + "&ams."));
                return;
        }
        sendUsage(sender);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onProcess(PlayerCommandPreprocessEvent e) {
        Player sender = e.getPlayer();
        String message = e.getMessage().toLowerCase();
        if (sender.hasPermission("souppvp.reload")) return;

        // /souppvp
        if (message.equals("/souppvp") || message.equals("/souppvp:" + name) || message.equals("/" + name)) {
            for (String line : Arrays.asList(
                    CC.LINE,
                    "&fThis server is running &6SoupPvP Core",
                    "&fOriginal project was made by &bhieu&f.",
                    CC.LINE
            )) {
                sendMessage(sender, line);
            }
            return;
        }

        // Aliases
        for (String alias : aliases()) {
            if (message.equals("/souppvp:" + alias) || message.equals("/" + alias)) {
                for (String line : Arrays.asList(
                        CC.LINE,
                        "&fThis server is running &6SoupPvP Core&f.",
                        "&fOriginal project was made by &bhieu&f.",
                        CC.LINE
                )) {
                    sendMessage(sender, line);
                }
                return;
            }
        }
    }
}
