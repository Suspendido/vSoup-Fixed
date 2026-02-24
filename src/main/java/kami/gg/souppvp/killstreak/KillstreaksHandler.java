package kami.gg.souppvp.killstreak;

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
    }

}
