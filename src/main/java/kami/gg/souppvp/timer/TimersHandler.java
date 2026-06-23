package kami.gg.souppvp.timer;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.PlayerUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Sound;

import java.util.HashMap;
import java.util.UUID;

/**
 * @author hieu
 * @date 24/06/2023
 */

@Getter @Setter
public class TimersHandler {

    @Getter public HashMap<UUID, Timer> primaryAbilitiesHashMap;
    @Getter public HashMap<UUID, Timer> secondaryAbilitiesHashMap;

    public TimersHandler(){
        this.primaryAbilitiesHashMap = new HashMap<>();
        this.secondaryAbilitiesHashMap = new HashMap<>();
        Bukkit.getScheduler().scheduleSyncRepeatingTask(SoupPvP.getInstance(), this::clearTimersCache, 2L, 2L);
    }

    public void clearTimersCache(){
        for (UUID uuid: getPrimaryAbilitiesHashMap().keySet()) {
            if (Bukkit.getPlayer(uuid) == null) {
                getPrimaryAbilitiesHashMap().remove(uuid);
                return;
            }

            if (getPrimaryAbilitiesHashMap().get(uuid).getCooldown() <= System.currentTimeMillis()) {
                Bukkit.getPlayer(uuid).sendMessage(CC.t("&eYou may now use &d" + getPrimaryAbilitiesHashMap().get(uuid).getAbilityName() + "&e!"));
                PlayerUtil.playSound(Bukkit.getPlayer(uuid), Sound.CHICKEN_EGG_POP, 1.0);
                getPrimaryAbilitiesHashMap().remove(uuid);
                return;
            }
        }
        for (UUID uuid: getSecondaryAbilitiesHashMap().keySet()) {
            if (Bukkit.getPlayer(uuid) == null) {
                getSecondaryAbilitiesHashMap().remove(uuid);
                return;
            }
            if (getSecondaryAbilitiesHashMap().get(uuid).getCooldown() <= System.currentTimeMillis()) {
                Bukkit.getPlayer(uuid).sendMessage(CC.t("&eYou may now use &d" + getSecondaryAbilitiesHashMap().get(uuid).getAbilityName() + "&e!"));
                PlayerUtil.playSound(Bukkit.getPlayer(uuid), Sound.CHICKEN_EGG_POP, 1.0);
                getSecondaryAbilitiesHashMap().remove(uuid);
                return;
            }
        }
    }

    public boolean containsInHashMapPlayer(UUID uuid){
        return this.getPrimaryAbilitiesHashMap().containsKey(uuid) || this.getSecondaryAbilitiesHashMap().containsKey(uuid);
    }

    public boolean containsPlayer(UUID uuid, boolean primaryAbility){
        if (primaryAbility){
            return this.getPrimaryAbilitiesHashMap().containsKey(uuid);
        } else {
            return this.getSecondaryAbilitiesHashMap().containsKey(uuid);
        }
    }

    public boolean hasTimer(UUID uuid, String abilityName, boolean primaryAbility) {
        if (primaryAbility) {
            for (UUID uuid1 : this.getPrimaryAbilitiesHashMap().keySet()){
                if (this.getPrimaryAbilitiesHashMap().get(uuid1).getAbilityName().equals(abilityName)) {
                    return true;
                }
            }
        } else {
            for (UUID uuid1 : this.getSecondaryAbilitiesHashMap().keySet()){
                if (this.getSecondaryAbilitiesHashMap().get(uuid1).getAbilityName().equals(abilityName)){
                    return true;
                }
            }
        }
        return false;
    }

    public long getRemaining(UUID uuid, String abilityName, boolean primaryAbility) {
        if (primaryAbility){
            for (UUID uuid1 : this.getPrimaryAbilitiesHashMap().keySet()){
                if (uuid1 == uuid){
                    if (this.getPrimaryAbilitiesHashMap().get(uuid1).getAbilityName().equals(abilityName)){
                        return (this.getPrimaryAbilitiesHashMap().get(uuid1).getCooldown() - System.currentTimeMillis());
                    }
                }
            }
        } else {
            for (UUID uuid1 : this.getSecondaryAbilitiesHashMap().keySet()){
                if (uuid1 == uuid){
                    if (this.getSecondaryAbilitiesHashMap().get(uuid1).getAbilityName().equals(abilityName)){
                        return (this.getSecondaryAbilitiesHashMap().get(uuid1).getCooldown() - System.currentTimeMillis());
                    }
                }
            }
        }
        return 0L;
    }

    public void addPlayerTimer(UUID uuid, Timer timer, boolean primaryAbility){
        if (primaryAbility) {
            this.getPrimaryAbilitiesHashMap().put(uuid, timer);
        } else {
            this.getSecondaryAbilitiesHashMap().put(uuid, timer);
        }
    }

    public void removePlayerTimer(UUID uuid, boolean primaryAbility){
        if (primaryAbility) {
            this.getPrimaryAbilitiesHashMap().remove(uuid);
        } else {
            this.getSecondaryAbilitiesHashMap().remove(uuid);
        }
    }

    public void removeAllPlayerTimers(UUID uuid){
        for (UUID uuid1 : this.getPrimaryAbilitiesHashMap().keySet()){
            if (uuid1 == uuid){
                removePlayerTimer(uuid, true);
            }
        }
        for (UUID uuid1 : this.getSecondaryAbilitiesHashMap().keySet()){
            if (uuid1 == uuid){
                removePlayerTimer(uuid, false);
            }
        }
    }

}
