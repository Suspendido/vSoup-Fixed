package kami.gg.souppvp.feats.soupsays;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.util.CC;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/*
 * Copyright (c) 2026. @Comunidad, made since 23/6/2026
 * Use or redistribution of this source file is only permitted
 * if explicit permission is given by the author.
 */
public abstract class Tasks implements Listener {

    @Getter
    private final Map<UUID, Integer> points = new HashMap<>();

    public abstract String getTaskID();
    public abstract String getTaskDisplayName();
    public abstract int getPointsToWin();

    @Getter @Setter
    private int credits;

    public void addProgress(Player player) {
        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        this.points.put(player.getUniqueId(), points.getOrDefault(player.getUniqueId(), 0)+1);

        if (this.points.get(player.getUniqueId()) >= this.getPointsToWin()) {
            deactivate(player);
            return;
        }

        player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);
        player.sendMessage(CC.t("&6You have obtained a point towards this task! (&f" + this.points.get(player.getUniqueId()) + "/" + getPointsToWin() + "&6)"));
    }

    public void activate() {
        final SoupPvP instance = SoupPvP.getInstance();

        this.credits = ThreadLocalRandom.current().nextInt(0, 1000) <= 500 ? 200 : 300;

        instance.getServer().getPluginManager().registerEvents(this, instance);

        List<String> message = Arrays.asList(
                "",
                "&7███████",
                "&7█" + "&4█████" + "&7█ &b&lSoup Says",
                "&7█" + "&4█" + "&7█████ &7First person to",
                "&7█" + "&4█" + "&7█████ &f" + getTaskDisplayName(),
                "&7█" + "&4█" + "&7█████ &cwill receive",
                "&7█" + "&4█████" + "&7█ &b&l" + credits + " Credits",
                "&7███████",
                ""
        );

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            for (String s : message) {
                onlinePlayer.sendMessage(CC.t(s));
            }
        }
    }

    public void deactivate(Player winner) {
        final SoupPvP instance = SoupPvP.getInstance();
        final SoupSaysManager soupSaysManager = instance.getSoupSaysManager();

        this.points.clear();
        soupSaysManager.setActiveTask(null);
        HandlerList.unregisterAll(this);


        List<String> cancelmsg = Arrays.asList(
                "",
                "&b&lSoup Says",
                getTaskDisplayName() + " &7has been forcefully cancelled.",
                ""
        );

        if (winner == null) {
            for (String s : cancelmsg) {
                Bukkit.broadcastMessage(CC.t(s));
            }
            return;
        }

        winner.playSound(winner.getLocation(), Sound.LEVEL_UP, 1, 1);
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(winner.getUniqueId());
        profile.setCredits(profile.getCredits() + credits);

        List<String> winmsg = Arrays.asList(
                "",
                "&b&lSoup Says",
                "",
                instance.getRankHook().getRankColor(winner) + winner.getName() + " &7has completed the task first and received &b&l" + credits + " Credits&7!",
                ""
        );

        for (String s : winmsg) {
            Bukkit.broadcastMessage(CC.t(s));
        }
    }
}
