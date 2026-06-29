package kami.gg.souppvp.handlers;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.util.Cuboid;
import kami.gg.souppvp.util.LocationUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

@Getter @Setter
public class SpawnHandler {

    private Location a;
    private Location b;
    private Cuboid cuboid;

    public SpawnHandler() {
        a = LocationUtil.convertStringToLocation(SoupPvP.getInstance().getConfig().getString("SPAWN.LOCATION-A"));
        b = LocationUtil.convertStringToLocation(SoupPvP.getInstance().getConfig().getString("SPAWN.LOCATION-B"));
        cuboid = new Cuboid(a, b);
    }

}
