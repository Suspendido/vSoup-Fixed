package kami.gg.souppvp.perk.inherit;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.perk.Perk;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.TaskUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.List;

public class JammerPerk extends Perk implements Listener {

    @Override
    public String getName() {
        return "Jammer";
    }

    @Override
    public String getColor() {
        return "&e";
    }

    @Override
    public List<String> getDescription() {
        List<String> lore = new ArrayList<>();
        lore.add(CC.t("&7Have a high chance of jamming someone's ability"));
        lore.add(CC.t("&7to use the sponge."));
        return lore;
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.SPONGE);
    }

    @Override
    public int getCost() {
        return 125;
    }


    @EventHandler
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event){
        if (!(event.getDamager() instanceof Player)) return;
        if (!(event.getEntity() instanceof Player)) return;
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(event.getDamager().getUniqueId());
        if (profile.isInEvent()) return;
        Perk currentPerk = SoupPvP.getInstance().getPerksHandler().getPerkByName(profile.getActivePerks().get(1));
        if (currentPerk == null) return;
        Perk jammerPerk = SoupPvP.getInstance().getPerksHandler().getPerkByName("Jammer");
        if (currentPerk == jammerPerk){
            if (event.getEntity().hasMetadata("jammed")) return;
            event.getEntity().setMetadata("jammed", new FixedMetadataValue(SoupPvP.getInstance(), "jammed"));
            event.getEntity().sendMessage(CC.t("&cYou've been jammed by &e" + event.getDamager().getName() + "&c."));
            TaskUtil.runLater(() -> {
                event.getEntity().removeMetadata("jammed", SoupPvP.getInstance());
                event.getEntity().sendMessage(CC.t("&cYou're no longer jammed."));
            }, 10 * 20L);
        }
    }

}
