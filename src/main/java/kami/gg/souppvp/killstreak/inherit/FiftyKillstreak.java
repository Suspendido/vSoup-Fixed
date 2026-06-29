package kami.gg.souppvp.killstreak.inherit;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.killstreak.Killstreak;
import kami.gg.souppvp.perk.Perk;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.profile.ProfileState;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.ItemBuilder;
import kami.gg.souppvp.util.PlayerUtil;
import kami.gg.souppvp.util.countdown.CountdownBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class FiftyKillstreak extends Killstreak implements Listener {

    @Override
    public String getName() {
        return "Nuke";
    }

    @Override
    public int getRequired() {
        return 50;
    }

    @Override
    public ItemStack getIcon() {
        return new ItemBuilder(Material.TNT)
                .name("&a" + getName())
                .lore(Arrays.asList(
                        CC.MENU_BAR,
                        "&7Will start a 10 second timer and upon finish,",
                        "&7it will decimate all enemies in a 25 block radius.",
                        CC.MENU_BAR,
                        "",
                        "&fKillstreak Required: &d" + getRequired(),
                        ""
                )).build();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        if (killer == null) return;

        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(killer.getUniqueId());
        if (profile == null) return;

        boolean hasHardline = hasHardline(profile);

        int neededKS = hasHardline ? getRequired() - 1 : getRequired();
        if (profile.getCurrentKillstreak() != neededKS) return;

        giveNuke(killer);
    }

    private void giveNuke(Player killer) {
        killer.sendMessage(CC.t("&aYou've received the &d" + getName() + " &aperk for reaching a &d" + getRequired() + " &akillstreak!"));
        Bukkit.broadcastMessage(CC.t("&a" + killer.getName() + " &ehas unlocked a &cNuke&e!"));
        startNukeCountdown(killer);
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

            boolean inSpawn = SoupPvP.getInstance().getSpawnHandler().getCuboid().contains(p) && profile.getProfileState() == ProfileState.SPAWN;

            if (inSpawn) continue;

            if (p.getLocation().distance(killer.getLocation()) <= 25) {
                p.damage(100.0, killer);
                nuked++;
            }
        }

        String context = nuked == 1 ? "player" : "players";
        Bukkit.broadcastMessage(CC.t("&eThe nuke eliminated a total of &c" + nuked + " &e" + context + "."));
    }

    private boolean hasHardline(Profile profile) {
        List<String> perks = profile.getActivePerks();
        if (perks == null || perks.isEmpty()) return false;

        Perk hardline = SoupPvP.getInstance().getPerksHandler().getPerkByName("Hardline");
        if (hardline == null) return false;

        return perks.stream().anyMatch(name -> hardline.equals(SoupPvP.getInstance().getPerksHandler().getPerkByName(name)));
    }
}
