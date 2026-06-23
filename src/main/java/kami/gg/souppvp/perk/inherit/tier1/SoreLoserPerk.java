package kami.gg.souppvp.perk.inherit.tier1;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.perk.Perk;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SoreLoserPerk extends Perk implements Listener {

    @Override
    public String getName() {
        return "Sore Loser";
    }

    @Override
    public List<String> getDescription() {
        List<String> lore = new ArrayList<>();
        lore.add(CC.t("&7Automatically send salty messages to"));
        lore.add(CC.t("&7players when they kill you."));
        return lore;
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.GLOWSTONE_DUST);
    }

    @Override
    public int getCost() {
        return 50;
    }

    @Override
    public int getTier() {
        return 1;
    }

    @EventHandler
    public void onPlayerDeathEvent(PlayerDeathEvent event){
        if (event.getEntity() != null && event.getEntity().getKiller() != null){
            Player killed = event.getEntity();
            Player killer = event.getEntity().getKiller();
            Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(killed.getUniqueId());
            if (profile.isInEvent()) return;
            Perk currentPerk = SoupPvP.getInstance().getPerksHandler().getPerkByName(profile.getActivePerks().get(0));
            if (currentPerk == null) return;
            Perk soreLoserPerk = SoupPvP.getInstance().getPerksHandler().getPerkByName("Sore Loser");
            if (currentPerk == soreLoserPerk){
                List<String> saltyMessages = new ArrayList<>();
                saltyMessages.add("eZ");
                saltyMessages.add("ur trash");
                saltyMessages.add("L");
                saltyMessages.add("ur not good");
                saltyMessages.add("get a life");
                saltyMessages.add("fck u");
                saltyMessages.add("ok");
                saltyMessages.add("k");
                saltyMessages.add("K");
                saltyMessages.add("ur 2 ez.");
                saltyMessages.add("again?");
                saltyMessages.add("all luck no skill!");
                saltyMessages.add("weirdo");
                saltyMessages.add("cringe");
                int randomIndex = new Random().nextInt(saltyMessages.size());
                Bukkit.dispatchCommand(killed, "msg " + killer.getName() + " " + saltyMessages.get(randomIndex));
            }
        }
    }

}
