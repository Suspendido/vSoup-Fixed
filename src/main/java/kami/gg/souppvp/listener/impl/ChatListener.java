package kami.gg.souppvp.listener.impl;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.profile.Profile;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(event.getPlayer().getUniqueId());
        event.setFormat(ChatColor.GRAY + "[" + profile.getTier().getTierLevel() + "✫" + "] " + event.getPlayer().getDisplayName() + ": " + event.getMessage());
    }

}
