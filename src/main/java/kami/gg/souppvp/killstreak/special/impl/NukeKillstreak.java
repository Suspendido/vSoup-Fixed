package kami.gg.souppvp.killstreak.special.impl;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.killstreak.ConfigurableKillstreak;
import kami.gg.souppvp.killstreak.special.SpecialKillstreak;
import kami.gg.souppvp.killstreak.special.SpecialTypeKillstreak;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.profile.ProfileState;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.PlayerUtil;
import kami.gg.souppvp.util.countdown.CountdownBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class NukeKillstreak implements SpecialKillstreak {

    @Override
    public SpecialTypeKillstreak getSpecialType() {
        return SpecialTypeKillstreak.NUKE;
    }

    @Override
    public void apply(Player player, ConfigurableKillstreak.RewardData rewardData) {
        Bukkit.broadcastMessage(CC.t("&a" + player.getName() + " &ehas unlocked a &cNuke&e!"));
        startNukeCountdown(player);
    }

    private void startNukeCountdown(Player killer) {
        CountdownBuilder countdown = new CountdownBuilder(10);
        countdown.setMessageFilter(new ArrayList<>(Bukkit.getOnlinePlayers()));
        countdown.withMessage("&eTactical Nuke Incoming! &c{time}...");

        for (int i = 0; i <= 10; i++) {
            countdown.broadcastAt(i, TimeUnit.SECONDS);
        }

        countdown.onBroadcast(() -> {
            killer.getWorld().spigot().playEffect(
                    killer.getLocation().add(new Vector(0, 3, 0)),
                    Effect.HAPPY_VILLAGER,
                    26, 0,
                    0.1F, 0.5F, 0.1F,
                    0.2F, 2, 50
            );

            for (Player p : Bukkit.getOnlinePlayers()) {
                PlayerUtil.playSound(p, Sound.CHICKEN_EGG_POP, 1.0);
            }
        });

        countdown.onFinish(() -> applyNukeDamage(killer));
        countdown.start();
    }

    private void applyNukeDamage(Player killer) {
        int nuked = 0;

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.equals(killer)) continue;

            Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(p.getUniqueId());
            if (profile == null) continue;
            if (SoupPvP.getInstance().getSpawnHandler().getCuboid().contains(p) && profile.getProfileState() == ProfileState.SPAWN) continue;

            if (p.getLocation().distance(killer.getLocation()) <= 25) {
                p.damage(100.0, killer);
                nuked++;
            }
        }

        String context = nuked == 1 ? "player" : "players";
        Bukkit.broadcastMessage(CC.t("&eThe nuke eliminated a total of &c" + nuked + " &e" + context + "."));
    }
}
