package kami.gg.souppvp.perk.inherit.tier2;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.perk.Perk;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TauntPerk extends Perk implements Listener {

    @Override
    public String getName() {
        return "Taunt";
    }

    @Override
    public List<String> getDescription() {
        List<String> lore = new ArrayList<>();
        lore.add(CC.t("&7Automatically taunt players in private"));
        lore.add(CC.t("&7messages when you kill them."));
        return lore;
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.REDSTONE);
    }

    @Override
    public int getCost() {
        return 800;
    }

    @Override
    public int getTier() {
        return 2;
    }

    @EventHandler
    public void onPlayerDeathEvent(PlayerDeathEvent event){
        if (event.getEntity() != null && event.getEntity().getKiller() != null){
            Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(event.getEntity().getKiller().getUniqueId());
            if (profile.isInEvent()) return;
            Perk currentPerk = SoupPvP.getInstance().getPerksHandler().getPerkByName(profile.getActivePerks().get(1));
            if (currentPerk == null) return;
            Perk soreLoserPerk = SoupPvP.getInstance().getPerksHandler().getPerkByName("Taunt");
            if (currentPerk == soreLoserPerk){
                List<String> tauntMessages = new ArrayList<>();
                tauntMessages.add("that's it?");
                tauntMessages.add("bruh");
                tauntMessages.add("try again");
                tauntMessages.add("lmfao");
                tauntMessages.add("later skater");
                tauntMessages.add("rough");
                tauntMessages.add("tuff");
                int randomIndex = new Random().nextInt(tauntMessages.size());
                Bukkit.dispatchCommand(event.getEntity().getKiller(), "msg " + event.getEntity().getName() + " " + tauntMessages.get(randomIndex));
            }
        }
    }

}
