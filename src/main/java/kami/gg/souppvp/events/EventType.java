package kami.gg.souppvp.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.List;

@AllArgsConstructor @Getter
public enum EventType {

    SUMO("Sumo", Arrays.asList(
            "&b┃ &fSingle elimination styled event.",
            "&b┃ &fAll players take turns fighting for the platform",
            "&b┃ &fwhile being spectated by other contestants.",
            "&b┃ &fWin the event by not losing any of your matches."
    ), "&5", "event.sumo", 100, Material.LEASH),

    TNTTAG("TNTTag", Arrays.asList(
            "&b┃ &fRun from the players with a TNT on their heads,",
            "&b┃ &fIf they hit you, you will need to hit another",
            "&b┃ &fplayer or you will explode and die."
    ), "&c", "event.tnttag", 150, Material.TNT);

    private final String name;
    private final List<String> descripction;
    private final String color;
    private final String hostPermission;
    private final int winCredits;
    private final Material material;

}
