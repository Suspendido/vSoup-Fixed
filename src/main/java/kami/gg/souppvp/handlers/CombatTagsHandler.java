package kami.gg.souppvp.handlers;

import lombok.Getter;

import java.util.HashMap;
import java.util.UUID;

@Getter
public class CombatTagsHandler {

    private final HashMap<UUID, Long> combatTags;

    public CombatTagsHandler(){
        combatTags = new HashMap<>();
    }

}
