package kami.gg.souppvp.kit.inherit;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.kit.Kit;
import kami.gg.souppvp.kit.KitRarity;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.profile.ProfileState;
import kami.gg.souppvp.timer.Timer;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.DurationFormatter;
import kami.gg.souppvp.util.ItemBuilder;
import kami.gg.souppvp.util.PlayerUtil;
import kami.gg.souppvp.util.XPBarTimer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class GrapplerKit extends Kit {

    private final ItemStack grapplerRod = new ItemBuilder(Material.FISHING_ROD).name(CC.translate("&aGrappler")).build();

    @Override
    public String getName() {
        return "Grappler";
    }

    @Override
    public KitRarity getRarityType() {
        return KitRarity.UNCOMMON;
    }

    @Override
    public Integer getPrice() {
        return getRarityType().getPrice();
    }

    @Override
    public ItemStack getIcon() {
        return new ItemBuilder(Material.FISHING_ROD).build();
    }

    @Override
    public List<String> getDescription() {
        return List.of(
                "&7Have the ability to take daily vacations. Using your grappler, you can",
                "&7hook yourself onto locations and can travel a vast distance if accurate."
        );
    }

    @Override
    public List<ItemStack> getCombatEquipments() {
        return List.of(
                new ItemBuilder(Material.IRON_SWORD).enchantment(Enchantment.DAMAGE_ALL, 1).enchantment(Enchantment.DURABILITY, 3).build(),
                grapplerRod
        );
    }

    @Override
    public ItemStack[] getArmor() {
        return new ItemStack[]{
                new ItemBuilder(Material.IRON_BOOTS).build(),
                new ItemBuilder(Material.CHAINMAIL_LEGGINGS).enchantment(Enchantment.DURABILITY, 3).build(),
                new ItemBuilder(Material.GOLD_CHESTPLATE).enchantment(Enchantment.DURABILITY, 3).build(),
                new ItemBuilder(Material.CHAINMAIL_HELMET).build()
        };
    }

    @Override
    public List<PotionEffect> getPotionEffects() {
        return List.of(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
    }

    @Override
    public void onSelect(Player player) {

    }

    private boolean isInvalid(Profile p) {
        return p.isInEvent() || p.getProfileState() == ProfileState.SPAWN;
    }

    private boolean isGrappler(Profile p) {
        return p.getCurrentKit().equals(getName());
    }

    @EventHandler
    public void onGrappleLaunch(ProjectileLaunchEvent event) {
        if (!(event.getEntity().getShooter() instanceof Player player)) return;
        if (event.getEntity().getType() != EntityType.FISHING_HOOK) return;

        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        if (profile == null || isInvalid(profile) || !isGrappler(profile)) return;

        var timers = SoupPvP.getInstance().getTimersHandler();

        if (SoupPvP.getInstance().getSpawnHandler().getCuboid().contains(player.getLocation())) {
            player.sendMessage(CC.translate("&cYou can't do this in spawn."));
            return;
        }

        if (timers.hasTimer(player.getUniqueId(), "Grappler", true)) {
            long remaining = timers.getRemaining(player.getUniqueId(), "Grappler", true);
            player.sendMessage(CC.translate("&cYou can't use this for another &e" + DurationFormatter.getRemaining(remaining, true) + "&c."));
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerFishEvent(PlayerFishEvent event) {
        Player player = event.getPlayer();
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        if (profile == null || isInvalid(profile) || !isGrappler(profile)) return;

        if (!player.getItemInHand().isSimilar(grapplerRod)) return;
        if (!event.getHook().isValid()) return;
        if (event.getState() == PlayerFishEvent.State.FISHING) return;

        Entity hooked = event.getHook();
        if (hooked == null) return;

        if (event.getCaught() != null) {
            event.getHook().remove();
            return;
        }

        Location pLoc = player.getLocation();
        Location hookLoc = hooked.getLocation();

        Vector velocity = new Vector(
                hookLoc.getX() - pLoc.getX(),
                hookLoc.getY() - pLoc.getY() + 2.5,
                hookLoc.getZ() - pLoc.getZ()
        ).multiply(0.25);

        player.setVelocity(velocity);
        hooked.remove();

        // Add cooldown
        var timers = SoupPvP.getInstance().getTimersHandler();
        timers.addPlayerTimer(player.getUniqueId(), new Timer("Grappler", TimeUnit.SECONDS.toMillis(30)), true);
        XPBarTimer.runXpBar(player, 30);

        PlayerUtil.playSound(player, Sound.ORB_PICKUP);
    }
}
