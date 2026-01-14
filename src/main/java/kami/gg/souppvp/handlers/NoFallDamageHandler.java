package kami.gg.souppvp.handlers;

import kami.gg.souppvp.util.TaskUtil;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class NoFallDamageHandler {

    private final List<UUID> noFallDamage;

    public NoFallDamageHandler(){
        noFallDamage = new ArrayList<>();
    }

    public void remove(UUID uuid){
        getNoFallDamage().remove(uuid);
    }

    public void add(UUID uuid){
        getNoFallDamage().add(uuid);
        TaskUtil.runLater(() -> getNoFallDamage().remove(uuid), 5 * 10L);
    }

}
