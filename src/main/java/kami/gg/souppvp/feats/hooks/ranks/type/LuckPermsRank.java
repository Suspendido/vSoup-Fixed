package kami.gg.souppvp.feats.hooks.ranks.type;

import kami.gg.souppvp.feats.hooks.ranks.IRank;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class LuckPermsRank implements IRank {

    private final LuckPerms luckPerms;

    public LuckPermsRank() {
        this.luckPerms = Bukkit.getServicesManager().load(net.luckperms.api.LuckPerms.class);
    }

    @Override
    public String getRankName(Player player) {
        User user = getUser(player.getUniqueId());
        if (user == null) return "";

        String primaryGroup = user.getPrimaryGroup();
        Group group = LuckPermsProvider.get().getGroupManager().getGroup(primaryGroup);

        return group != null && group.getDisplayName() != null ? group.getDisplayName() : primaryGroup;
    }


    @Override
    public String getRankPrefix(Player player) {
        User user = getUser(player.getUniqueId());
        if (user == null) {
            return "";
        }

        String prefix = user.getCachedData().getMetaData().getPrefix();
        return prefix != null ? prefix : "";
    }

    @Override
    public String getRankSuffix(Player player) {
        User user = getUser(player.getUniqueId());
        if (user == null) {
            return "";
        }

        String suffix = user.getCachedData().getMetaData().getSuffix();
        return suffix != null ? suffix : "";
    }

    @Override
    public String getRankColor(Player player) {
        User user = getUser(player.getUniqueId());
        if (user == null) {
            return "";
        }

        String color = user.getCachedData().getMetaData().getMetaValue("color");
        return color != null ? color : "";
    }

    @Override
    public int getRankWeight(Player player) {
        User user = getUser(player.getUniqueId());
        if (user == null) return 0;

        String primaryGroup = user.getPrimaryGroup();
        Group group = LuckPermsProvider.get().getGroupManager().getGroup(primaryGroup);

        if (group != null && group.getWeight().isPresent()) {
            return group.getWeight().getAsInt();
        }

        return 0;
    }

    private User getUser(UUID uuid) {
        return this.luckPerms.getUserManager().getUser(uuid);
    }
}
