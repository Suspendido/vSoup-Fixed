package kami.gg.souppvp.perk.inherit.tier2;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.perk.Perk;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.util.CC;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class RevengePerk extends Perk implements Listener {

    @Getter private HashMap<UUID, UUID> revengeMap = new HashMap<>(); //UUID Killed By UUID

    @Override
    public String getName() {
        return "Revenge";
    }

    @Override
    public List<String> getDescription() {
        List<String> lore = new ArrayList<>();
        lore.add(CC.t("&7Gain triple your normal credits for killing"));
        lore.add(CC.t("&7a player who just killed you."));
        return lore;
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.FERMENTED_SPIDER_EYE);
    }

    @Override
    public int getCost() {
        return 850;
    }

    @Override
    public int getTier() {
        return 2;
    }

    @EventHandler
    public void onPlayerDeathEvent(PlayerDeathEvent event){
        if (event.getEntity() != null && event.getEntity().getKiller() != null){
            Profile killedProfile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(event.getEntity().getUniqueId());
            Profile killerProfile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(event.getEntity().getKiller().getUniqueId());
            Perk revengePerk = SoupPvP.getInstance().getPerksHandler().getPerkByName("Revenge");
            Perk killedProfilePerk = SoupPvP.getInstance().getPerksHandler().getPerkByName(killedProfile.getActivePerks().get(1));
            Perk killerProfilePerk = SoupPvP.getInstance().getPerksHandler().getPerkByName(killerProfile.getActivePerks().get(1));
            if (killedProfilePerk == revengePerk){
                if (revengeMap.containsKey(event.getEntity().getUniqueId())){
                    revengeMap.replace(event.getEntity().getUniqueId(), event.getEntity().getKiller().getUniqueId());
                } else {
                    revengeMap.put(event.getEntity().getUniqueId(), event.getEntity().getKiller().getUniqueId());
                }
                Bukkit.getPlayer(event.getEntity().getUniqueId()).sendMessage(CC.t("&cYou need to take your revenge on " + event.getEntity().getKiller().getName() + "."));
            }
            if (killerProfilePerk == revengePerk){
                if (revengeMap.containsKey(event.getEntity().getKiller().getUniqueId())){
                    if (revengeMap.get(event.getEntity().getKiller().getUniqueId()) == event.getEntity().getUniqueId()){
                        event.getEntity().getKiller().sendMessage(CC.t("&cYou earned triple your credits for killing " + event.getEntity().getName() + "."));
                        killerProfile.setCredits(killerProfile.getCredits() + (17 * 2));
                        revengeMap.remove(event.getEntity().getKiller().getUniqueId());
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event){
        if (revengeMap.isEmpty()) return;
        revengeMap.remove(event.getPlayer().getUniqueId());
    }

}
