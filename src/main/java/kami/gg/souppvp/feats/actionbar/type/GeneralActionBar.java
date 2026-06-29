package kami.gg.souppvp.feats.actionbar.type;

import kami.gg.souppvp.feats.actionbar.ActionBarPriority;
import kami.gg.souppvp.feats.actionbar.ActionBarProvider;
import kami.gg.souppvp.util.CC;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/*
 * Copyright (c) 2026. @Comunidad, made since 28/6/2026
 * Use or redistribution of this source file is only permitted
 * if explicit permission is given by the author.
 */
public class GeneralActionBar implements ActionBarProvider {

    private static final Map<UUID, List<PerkMessage>> perkMessages = new ConcurrentHashMap<>();

    @Override
    public String getActionBar(Player player) {
        List<String> lines = new ArrayList<>();
        List<PerkMessage> messages = perkMessages.get(player.getUniqueId());

        if (messages != null) {
            long now = System.currentTimeMillis();
            messages.removeIf(msg -> now > msg.expiry);
            if (messages.isEmpty()) {
                perkMessages.remove(player.getUniqueId());
            } else {
                for (PerkMessage msg : messages) {
                    lines.add(msg.text);
                }
            }
        }

        if (lines.isEmpty()) return "";

        return CC.t(String.join(" &7● ", lines));
    }

    @Override
    public ActionBarPriority priority() {
        return ActionBarPriority.GENERAL;
    }

    @Override
    public boolean isActive(Player player) {
        List<PerkMessage> messages = perkMessages.get(player.getUniqueId());
        if (messages != null) {
            long now = System.currentTimeMillis();
            messages.removeIf(msg -> now > msg.expiry);
            if (!messages.isEmpty()) return true;
            perkMessages.remove(player.getUniqueId());
        }
        return false;
    }

    public static void sendMessage(Player player, String text) {
        perkMessages.computeIfAbsent(player.getUniqueId(), k -> new ArrayList<>())
                .add(new PerkMessage(text, System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(3)));
    }

    public static void clear() {
        perkMessages.clear();
    }

    private record PerkMessage(String text, long expiry) {}
}
