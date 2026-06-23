package kami.gg.souppvp.feats.storage;

import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.command.Command;
import kami.gg.souppvp.util.command.CommandManager;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class StorageStatsCommand extends Command {

    public StorageStatsCommand(CommandManager manager) {
        super(manager, "storagestats");
        setPermissible("souppvp.admin.storagestats");
    }

    @Override
    public List<String> aliases() {
        return Arrays.asList("sstats", "datastats");
    }

    @Override
    public List<String> usage() {
        return Collections.singletonList(CC.t("&c/storagestats - View storage statistics"));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        StorageType type = getInstance().getStorageType();
        
        sendMessage(sender, "&6&l[Storage Stats]", "&eStorage Type: &f" + type.name());
        
        if (type == StorageType.FLATFILE) {
            FlatFileHandler.StorageStats stats = getInstance().getFlatFileHandler().getStats();
            sendMessage(sender,
                "&eFiles on Disk: &f" + stats.getFilesOnDisk(),
                "&eProfiles in Cache: &f" + stats.getProfilesInCache(),
                "&eTotal Size: &f" + stats.getTotalSizeMB()
            );
        } else {
            sendMessage(sender, "&eUsing MongoDB storage");
        }
    }
}