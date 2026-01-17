package kami.gg.souppvp.killstreak;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.killstreak.inherit.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class KillstreaksHandler {

    private final List<Killstreak> killstreaks;

    public KillstreaksHandler(){
        killstreaks = new ArrayList<>();
        killstreaks.add(new FiveKillstreak());
        killstreaks.add(new TenKillstreak());
        killstreaks.add(new FifteenKillstreak());
        killstreaks.add(new TwentyKillstreak());
        killstreaks.add(new TwentyFiveKillstreak());
        killstreaks.add(new ThirtyKillstreak());
        killstreaks.add(new ThirtyFiveKillstreak());
        killstreaks.add(new FortyKillstreak());
        killstreaks.add(new FortyFiveKillstreak());
        killstreaks.add(new FiftyKillstreak());
        registerKillstreakListeners();
    }

    private void registerKillstreakListeners(){
        SoupPvP.getInstance().getServer().getPluginManager().registerEvents(new FiveKillstreak(), SoupPvP.getInstance());
        SoupPvP.getInstance().getServer().getPluginManager().registerEvents(new TenKillstreak(), SoupPvP.getInstance());
        SoupPvP.getInstance().getServer().getPluginManager().registerEvents(new FifteenKillstreak(), SoupPvP.getInstance());
        SoupPvP.getInstance().getServer().getPluginManager().registerEvents(new TwentyKillstreak(), SoupPvP.getInstance());
        SoupPvP.getInstance().getServer().getPluginManager().registerEvents(new TwentyFiveKillstreak(), SoupPvP.getInstance());
        SoupPvP.getInstance().getServer().getPluginManager().registerEvents(new ThirtyKillstreak(), SoupPvP.getInstance());
        SoupPvP.getInstance().getServer().getPluginManager().registerEvents(new ThirtyFiveKillstreak(), SoupPvP.getInstance());
        SoupPvP.getInstance().getServer().getPluginManager().registerEvents(new FortyKillstreak(), SoupPvP.getInstance());
        SoupPvP.getInstance().getServer().getPluginManager().registerEvents(new FortyFiveKillstreak(), SoupPvP.getInstance());
        SoupPvP.getInstance().getServer().getPluginManager().registerEvents(new FiftyKillstreak(), SoupPvP.getInstance());
    }

}
