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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.Random;

public class JuggernautPerk extends Perk implements Listener {

    @Override
    public String getName() {
        return "Juggernaut";
    }

    @Override
    public String getColor() {
        return "&4";
    }

    @Override
    public List<String> getDescription() {
        return List.of("&7Enemy kills gives you Regen I", "&7for up to 10 seconds");
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.DIAMOND_CHESTPLATE);
    }

    @Override
    public int getCost() {
        return 2000;
    }

    @EventHandler
    public void onPlayerDeathEvent(PlayerDeathEvent e) {
        if (!(e.getEntity().getKiller() instanceof Player player)) return;

        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        if (profile == null) return;
        if (profile.isInEvent()) return;
        if (!profile.getActivePerks().contains(getName())) return;

        int seconds = new Random().nextInt(10) + 1;
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, seconds * 20, 0));
    }

}
