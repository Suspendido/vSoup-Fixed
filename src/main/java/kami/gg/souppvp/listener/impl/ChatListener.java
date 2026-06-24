package kami.gg.souppvp.listener.impl;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.tier.TierCategory;
import kami.gg.souppvp.util.CC;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(event.getPlayer().getUniqueId());
        TierCategory category = TierCategory.getCategoryByName(profile.getSelectedTierIcon());
        event.setFormat(CC.t(category.getColor() + "[" + profile.getTier() + category.getIcon() + "] " + SoupPvP.getInstance().getRankHook().getRankPrefix(event.getPlayer()) + event.getPlayer().getName() + "&7: &f" + event.getMessage()));
    }

}
