package kami.gg.souppvp.handlers;

import lombok.Getter;

import java.util.HashMap;
import java.util.UUID;

@Getter
public class SpawnTeleportationHandler {

    private final HashMap<UUID, Long> spawnTeleporataion;

    public SpawnTeleportationHandler(){
        spawnTeleporataion = new HashMap<>();
    }

}
