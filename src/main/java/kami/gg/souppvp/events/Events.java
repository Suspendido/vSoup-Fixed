package kami.gg.souppvp.events;

import kami.gg.souppvp.SoupPvP;
import lombok.Getter;
import org.bukkit.Material;

@Getter
public enum Events {

    SUMO(SoupPvP.getInstance().getSumoHandler().getActiveSumo(), "Sumo", Material.LEASH),
    TNTTAG(SoupPvP.getInstance().getTntTagHandler().getActiveGame(), "TNTTag", Material.TNT);

    Events(Object object, String title, Material material){
        this.object = object;
        this.title = title;
        this.material = material;
    }

    private final Object object;
    private final String title;
    private final Material material;

}
