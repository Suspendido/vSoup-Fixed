package kami.gg.souppvp.perk.inherit;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.perk.Perk;
import kami.gg.souppvp.profile.Profile;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ArmorerPerk extends Perk implements Listener {

    @Override
    public String getName() {
        return "Armorer";
    }

    @Override
    public String getColor() {
        return "&b";
    }

    @Override
    public List<String> getDescription() {
        return List.of("&7Gain an extra 10 durability every", "&7time you kill another player.");
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.DIAMOND_CHESTPLATE);
    }

    @Override
    public int getCost() {
        return 800;
    }

    @EventHandler
    public void onPlayerDeathEvent(PlayerDeathEvent event) {
        if (!(event.getEntity().getKiller() instanceof Player player)) return;

        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        if (profile == null) return;
        if (profile.isInEvent()) return;
        if (!profile.getActivePerks().contains(getName())) return;

        for (ItemStack armor : player.getInventory().getArmorContents()) {
            if (armor == null) continue;

            short newDurability = (short) Math.max(0, armor.getDurability() - 10);
            armor.setDurability(newDurability);
        }
    }
}
