package kami.gg.souppvp.killstreak;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.killstreak.special.SpecialKillstreak;
import kami.gg.souppvp.killstreak.special.SpecialTypeKillstreak;
import kami.gg.souppvp.killstreak.special.impl.*;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class KillstreaksHandler {

    private final List<ConfigurableKillstreak> killstreaks;
    private final KillstreakConfig config;
    private final Map<SpecialTypeKillstreak, SpecialKillstreak> specialKillstreaks;

    public KillstreaksHandler(SoupPvP plugin) {
        this.config = new KillstreakConfig(plugin);
        this.killstreaks = new ArrayList<>();
        this.specialKillstreaks = new HashMap<>();
        registerSpecials();
        loadKillstreaks();
    }

    public void registerSpecials() {
        RepairKillstreak repair = new RepairKillstreak();
        NukeKillstreak nuke = new NukeKillstreak();
        GoldenApplesKillstreak goldenApples = new GoldenApplesKillstreak();
        FireResistancePotionKillstreak fireResistance = new FireResistancePotionKillstreak();
        GrandmaSoupsKillstreak grandmaSoups = new GrandmaSoupsKillstreak();
        AttackDogsKillstreak attackDogs = new AttackDogsKillstreak();
        ExtraCreditsKillstreak extraCredits = new ExtraCreditsKillstreak();
        SecurityGuardKillstreak securityGuard = new SecurityGuardKillstreak();
        AngrySnowmanKillstreak angrySnowman = new AngrySnowmanKillstreak();

        specialKillstreaks.put(repair.getSpecialType(), repair);
        specialKillstreaks.put(nuke.getSpecialType(), nuke);
        specialKillstreaks.put(goldenApples.getSpecialType(), goldenApples);
        specialKillstreaks.put(fireResistance.getSpecialType(), fireResistance);
        specialKillstreaks.put(grandmaSoups.getSpecialType(), grandmaSoups);
        specialKillstreaks.put(attackDogs.getSpecialType(), attackDogs);
        specialKillstreaks.put(extraCredits.getSpecialType(), extraCredits);
        specialKillstreaks.put(securityGuard.getSpecialType(), securityGuard);
        specialKillstreaks.put(angrySnowman.getSpecialType(), angrySnowman);
    }

    private void loadKillstreaks() {
        List<ConfigurableKillstreak> configurableKillstreaks = config.loadKillstreaks();
        for (ConfigurableKillstreak ks : configurableKillstreaks) {
            killstreaks.add(ks);
            Bukkit.getPluginManager().registerEvents(ks, SoupPvP.getInstance());
        }
    }

    public void reload() {
        killstreaks.clear();
        loadKillstreaks();
    }

    public void addKillstreak(ConfigurableKillstreak killstreak) {
        killstreaks.add(killstreak);
        Bukkit.getPluginManager().registerEvents(killstreak, SoupPvP.getInstance());
        config.saveKillstreak(killstreak);
    }

    public void removeKillstreak(int id) {
        killstreaks.removeIf(ks -> ks.getId() == id);
        config.deleteKillstreak(id);
    }

    public void updateKillstreak(ConfigurableKillstreak killstreak) {
        for (int i = 0; i < killstreaks.size(); i++) {
            if (killstreaks.get(i).getId() == killstreak.getId()) {
                killstreaks.set(i, killstreak);
                break;
            }
        }
        config.saveKillstreak(killstreak);
    }

}
