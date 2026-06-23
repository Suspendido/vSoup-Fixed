package kami.gg.souppvp.feats.hooks.ranks.type;

import com.sylluxpvp.circuit.shared.grant.Grant;
import com.sylluxpvp.circuit.shared.profile.Profile;
import com.sylluxpvp.circuit.shared.rank.Rank;
import com.sylluxpvp.circuit.shared.service.ServiceContainer;
import com.sylluxpvp.circuit.shared.service.impl.ProfileService;
import kami.gg.souppvp.feats.hooks.ranks.IRank;
import kami.gg.souppvp.util.CC;
import org.bukkit.entity.Player;

public class CirCuitRank implements IRank {

    public String getRankName(Player player) {
        Profile profile = ServiceContainer.getService(ProfileService.class).find(player.getUniqueId());
        if (profile == null) {
            return "";
        } else {
            Grant<Rank> grant = profile.getCurrentGrant();
            Rank rank = grant != null ? grant.getData() : null;
            return rank != null ? rank.getName() : "Default";
        }
    }

    public String getRankPrefix(Player player) {
        Profile profile = ServiceContainer.getService(ProfileService.class).find(player.getUniqueId());
        if (profile == null) {
            return "";
        } else {
            Grant<Rank> grant = profile.getCurrentGrant();
            Rank rank = grant != null ? grant.getData() : null;
            return rank != null ? CC.t(rank.getPrefix() + rank.getColor()) : "";
        }
    }

    public String getRankSuffix(Player player) {
        Profile profile = ServiceContainer.getService(ProfileService.class).find(player.getUniqueId());
        if (profile == null) {
            return "";
        } else {
            Grant<Rank> grant = profile.getCurrentGrant();
            Rank rank = grant != null ? grant.getData() : null;
            return rank != null ? CC.t(rank.getSuffix()) : "";
        }
    }

    public String getRankColor(Player player) {
        Profile profile = ServiceContainer.getService(ProfileService.class).find(player.getUniqueId());
        if (profile == null) {
            return "";
        } else {
            Grant<Rank> grant = profile.getCurrentGrant();
            Rank rank = grant != null ? grant.getData() : null;
            return rank != null ? CC.t(rank.getColor()) : "&7";
        }
    }

    @Override
    public int getRankWeight(Player player) {
        Profile profile = ServiceContainer.getService(ProfileService.class).find(player.getUniqueId());
        if (profile == null) {
            return 0;
        } else {
            Grant<Rank> grant = profile.getCurrentGrant();
            Rank rank = grant != null ? grant.getData() : null;
            return rank != null ? rank.getWeight() : 0;
        }
    }
}