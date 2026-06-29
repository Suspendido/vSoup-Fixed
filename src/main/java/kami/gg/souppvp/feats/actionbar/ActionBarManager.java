package kami.gg.souppvp.feats.actionbar;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.feats.actionbar.type.*;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

/*
 * Copyright (c) 2026. @Comunidad, made since 28/6/2026
 * Use or redistribution of this source file is only permitted
 * if explicit permission is given by the author.
 */
@Getter
public class ActionBarManager {

    private final List<ActionBarProvider> providers = new ArrayList<>();

    public ActionBarManager(SoupPvP instance) {
        registerProviders();

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (!player.isOnline()) continue;

                    String message = getHighestPriorityMessage(player);
                    sendActionBar(player, message != null ? message : "");
                }
            }
        }.runTaskTimerAsynchronously(instance, 0L, 3L);
    }

    public void registerProviders() {
        providers.add(new StaffActionBar());
        providers.add(new AbilityActionBar());
        providers.add(new GeneralActionBar());
    }

    private String getHighestPriorityMessage(Player player) {
        ActionBarProvider highest = null;
        int highestPriority = -1;

        for (ActionBarProvider provider : providers) {
            if (!provider.isActive(player)) continue;

            int level = provider.priority().getLevel();
            if (level > highestPriority) {
                highestPriority = level;
                highest = provider;
            }
        }

        return highest != null ? highest.getActionBar(player) : null;
    }

    private void sendActionBar(Player player, String message) {
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.CHAT);
        packet.getChatComponents().write(0, WrappedChatComponent.fromText(message));
        packet.getBytes().write(0, (byte) 2);
        ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
    }

}
