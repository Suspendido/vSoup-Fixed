package kami.gg.souppvp.perk.inherit;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.perk.Perk;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Random;

public class DeathDoUsApartPerk extends Perk implements Listener {

    private static final List<ItemStack> DEBUFFS = List.of(
            new ItemBuilder(Material.POTION).durability(16388).build(), // Slowness
            new ItemBuilder(Material.POTION).durability(16420).build(), // Slowness II
            new ItemBuilder(Material.POTION).durability(16452).build(), // Slowness III
            new ItemBuilder(Material.POTION).durability(16424).build(), // Weakness
            new ItemBuilder(Material.POTION).durability(16456).build(), // Weakness II
            new ItemBuilder(Material.POTION).durability(16426).build(), // Poison
            new ItemBuilder(Material.POTION).durability(16458).build(), // Poison II
            new ItemBuilder(Material.POTION).durability(16460).build(), // Blindness
            new ItemBuilder(Material.POTION).durability(16428).build()  // Nausea
    );

    @Override
    public String getName() {
        return "Death Do Us Apart";
    }

    @Override
    public String getColor() {
        return "&3";
    }

    @Override
    public List<String> getDescription() {
        return List.of("&7Have a chance to spawn a random debuff at your death point");
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.SKULL_ITEM);
    }

    @Override
    public int getCost() {
        return 800;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeathEvent(PlayerDeathEvent e) {
        if (!(e.getEntity() instanceof Player player)) return;

        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        if (profile == null) return;
        if (profile.isInEvent()) return;
        if (!profile.getActivePerks().contains(getName())) return;

        Random random = new Random();
        int count = random.nextDouble() >= 0.5 ? 2 : 1;

        for (int i = 0; i < count; i++) {
            ItemStack itemStack = DEBUFFS.get(random.nextInt(DEBUFFS.size()));
            ThrownPotion potion = (ThrownPotion) player.getWorld().spawnEntity(player.getLocation().add(0, 5, 0), EntityType.SPLASH_POTION);
            potion.setItem(itemStack);
        }
    }

}
