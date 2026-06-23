package kami.gg.souppvp.command;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.command.Command;
import kami.gg.souppvp.util.command.CommandManager;
import kami.gg.souppvp.util.command.extra.TabCompletion;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class SoupPvPCommand extends Command {

    public SoupPvPCommand(CommandManager manager) {
        super(manager, "souppvp");
        this.completions.add(new TabCompletion(Arrays.asList("reload", "save", "version", "ver", "saveuser", "deleteusers", "deleteprofiles"), 0));
        this.setPermissible("souppvp.reload");
    }

    @Override
    public List<String> aliases() {
        return Collections.emptyList();
    }

    @Override
    public List<String> usage() {
        return Arrays.asList(
                CC.CHAT_BAR,
                "&6&lSoupPvP Core",
                "&fThis server is using &6SoupPvP Core",
                "",
                "&e/souppvp reload &8- &fReloads all the config.",
                "&e/souppvp version &8- &fGets the plugin current version.",
                "&e/souppvp save &8- &fSaves the config.",
                "&e/souppvp deleteusers &8- &fDeletes all the user data.",
                CC.CHAT_BAR
        );
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
                    sendMessage(sender, "&7[&6&lSoupPvP&7] &eStarting config reload...");
                    long startTime = System.currentTimeMillis();

                    SoupPvP.getInstance().getTablistManager().reload();
                    SoupPvP.getInstance().getScoreboardManager().reload();
                    SoupPvP.getInstance().getNametagManager().reload();
                    SoupPvP.getInstance().getMapManager().reload();
                    SoupPvP.getInstance().getStaffManager().reload();
                    SoupPvP.getInstance().getListenerManager().reload();

                    long endTime = System.currentTimeMillis();
                    sendMessage(sender, "&7[&6&lSoupPvP&7] &aConfigs reloaded! &7(" + (endTime - startTime) + "ms)");
                }
                return;

            case "deleteprofiles":
            case "deleteusers":
                if (!sender.hasPermission("souppvp.admin.deleteprofiles")) {
                    sendMessage(sender, "&cYou don't have permission to use this command!");
                    return;
                }

                sendMessage(sender, "&7[&6&lSoupPvP&7] &eDeleting all profiles...");

                Bukkit.getScheduler().runTaskAsynchronously(SoupPvP.getInstance(), () -> {
                    long startDelete = System.currentTimeMillis();
                    int deletedCount = 0;
                    int failedCount = 0;

                    List<UUID> uuidsToDelete = List.copyOf(SoupPvP.getInstance().getProfilesHandler().getProfiles().keySet());

                    for (UUID uuid : uuidsToDelete) {
                        boolean deleted = SoupPvP.getInstance().getProfilesHandler().deleteProfile(uuid);
                        if (deleted) {
                            deletedCount++;
                        } else {
                            failedCount++;
                        }
                    }

                    long endDelete = System.currentTimeMillis();

                    int finalDeleted = deletedCount;
                    int finalFailed = failedCount;
                    Bukkit.getScheduler().runTask(SoupPvP.getInstance(), () -> {
                        sendMessage(sender, "&7[&6&lSoupPvP&7] &aDeleted &l" + finalDeleted + "&a profiles.");
                        if (finalFailed > 0) {
                            sendMessage(sender, "&7[&6&lSoupPvP&7] &cFailed to delete &l" + finalFailed + "&c profiles.");
                        }
                        sendMessage(sender, "&7[&6&lSoupPvP&7] &7Completed in &e" + (endDelete - startDelete) + "ms");
                    });
                });
                return;
            case "ver":
            case "version":
                sendMessage(sender, "&7[&6&lSoupPvP&7] &eCurrent version is &a" + SoupPvP.getInstance().getDescription().getVersion() + "&e.");
                return;

            case "saveusers":
            case "save":
                for (Profile profile : SoupPvP.getInstance().getProfilesHandler().getProfiles().values()) {
                    profile.saveProfile();
                }
                Long started = System.currentTimeMillis();
                Long ended = System.currentTimeMillis();
                sendMessage(sender, "&7[&6&lSoupPvP&7] &aSuccessfully saved a total of &a&l" + SoupPvP.getInstance().getProfilesHandler().getProfiles().size() + "&a profiles within &a&l" + (ended - started) + "&ams.");
                return;
        }
        sendUsage(sender);
    }
}
