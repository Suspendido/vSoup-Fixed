package kami.gg.souppvp.perk.inherit;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.perk.Perk;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class BonusHeartsPerk extends Perk implements Listener {

    @Override
    public String getName() {
        return "Bonus Hearts";
    }

    @Override
    public String getColor() {
        return "&c";
    }

    @Override
    public List<String> getDescription() {
        return List.of("&7On every kill you will", "&7regen five additional hearts.");
    }

    @Override
    public ItemStack getIcon() {
        return new ItemBuilder(Material.INK_SACK).durability(1).build();
    }

    @Override
    public int getCost() {
        return 1275;
    }

    @EventHandler
    public void onPlayerDeathEvent(PlayerDeathEvent event) {
        if (!(event.getEntity().getKiller() instanceof Player player)) return;

        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        if (profile == null) return;
        if (profile.isInEvent()) return;
        if (!profile.getActivePerks().contains(getName())) return;

        double newHealth = Math.min(player.getHealth() + 10, player.getMaxHealth());
        player.setHealth(newHealth);
    }
}
