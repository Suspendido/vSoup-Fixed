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
import kami.gg.souppvp.util.XPBarTimer;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class SpidermanKit extends Kit {

    private final ItemStack WEB_SHOOTER = new ItemBuilder(Material.WEB).name(CC.translate("&dWeb Shooter")).build();

    @Override
    public String getName() {
        return "Spiderman";
    }

    @Override
    public KitRarity getRarityType() {
        return KitRarity.ULTIMATE;
    }

    @Override
    public Integer getPrice() {
        return getRarityType().getPrice();
    }

    @Override
    public ItemStack getIcon() {
        return new ItemBuilder(Material.WEB).build();
    }

    @Override
    public List<String> getDescription() {
        return Arrays.asList(
                "&7Be a superhero and fight criminals like your enemies.",
                "&7Using your web shooter, trap and slow down enemies."
        );
    }

    @Override
    public List<ItemStack> getCombatEquipments() {
        return Arrays.asList(
                new ItemBuilder(Material.IRON_SWORD)
                        .enchantment(Enchantment.DAMAGE_ALL, 1)
                        .enchantment(Enchantment.DURABILITY, 1)
                        .build(),
                WEB_SHOOTER
        );
    }

    @Override
    public ItemStack[] getArmor() {
        return new ItemStack[]{
                new ItemBuilder(Material.LEATHER_BOOTS).color(Color.BLUE)
                        .enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                        .enchantment(Enchantment.DURABILITY, 3)
                        .build(),
                new ItemBuilder(Material.LEATHER_LEGGINGS).color(Color.RED)
                        .enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                        .enchantment(Enchantment.DURABILITY, 3)
                        .build(),
                new ItemBuilder(Material.LEATHER_CHESTPLATE).color(Color.BLUE)
                        .enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                        .enchantment(Enchantment.DURABILITY, 3)
                        .build(),
                new ItemBuilder(Material.LEATHER_HELMET).color(Color.RED)
                        .enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                        .enchantment(Enchantment.DURABILITY, 3)
                        .build()
        };
    }

    @Override
    public List<PotionEffect> getPotionEffects() {
        return Arrays.asList(
                new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1),
                new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0)
        );
    }

    @Override
    public void onSelect(Player player) {
    }

    private static final Vector[] WEB_OFFSETS = new Vector[]{
            new Vector(1, 1, -1), new Vector(-1, 1, -1),
            new Vector(1, 1, 1), new Vector(-1, 1, 1),

            new Vector(0, 1, 0),
            new Vector(-1, 1, 0), new Vector(1, 1, 0),
            new Vector(0, 1, -1), new Vector(0, 1, 1)
    };

    private List<Location> getSurrounding(Location loc) {
        List<Location> locations = new ArrayList<>(WEB_OFFSETS.length);
        for (Vector v : WEB_OFFSETS) {
            locations.add(loc.clone().add(v));
        }
        return locations;
    }

    private void restoreBlock(BlockState bs) {
        if (bs instanceof Sign signBefore) {
            Location loc = signBefore.getLocation();
            Block block = loc.getBlock();

            block.setType(signBefore.getType());

            Sign signNow = (Sign) block.getState();
            for (int i = 0; i < 4; i++) {
                signNow.setLine(i, signBefore.getLine(i));
            }
            signNow.update(true);
            return;
        }
        bs.update(true);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(p.getUniqueId());

        if (profile.isInEvent() || profile.getProfileState() == ProfileState.SPAWN) return;
        if (!profile.getCurrentKit().equals(getName())) return;

        ItemStack hand = p.getItemInHand();
        if (!hand.isSimilar(WEB_SHOOTER)) return;

        if (!(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)) return;

        e.setCancelled(true);

        // cooldown
        if (SoupPvP.getInstance().getTimersHandler().hasTimer(p.getUniqueId(), "Web Shooter", true)) {
            long rem = SoupPvP.getInstance().getTimersHandler().getRemaining(p.getUniqueId(), "Web Shooter", true);
            p.sendMessage(ChatColor.RED + "You can't use this for another "
                    + ChatColor.YELLOW + DurationFormatter.getRemaining(rem, true)
                    + ChatColor.RED + ".");
            return;
        }

        SoupPvP.getInstance().getTimersHandler().addPlayerTimer(p.getUniqueId(), new Timer("Web Shooter", TimeUnit.SECONDS.toMillis(45)), true);
        XPBarTimer.runXpBar(p, 45);

        FallingBlock fb = p.getWorld().spawnFallingBlock(
                p.getEyeLocation(),
                Material.STAINED_GLASS,
                (byte) 0
        );

        fb.setMetadata("spiderman", new FixedMetadataValue(SoupPvP.getInstance(), p.getUniqueId()));
        fb.setDropItem(false);
        fb.setVelocity(p.getEyeLocation().getDirection().multiply(2.3).add(new Vector(0, 0.3, 0)));
        p.playSound(p.getLocation(), Sound.WITHER_SHOOT, 1f, 1f);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!fb.isValid() || fb.isDead() || !p.isOnline()) {
                    cancel();
                    return;
                }

                for (Entity ent : fb.getNearbyEntities(3, 3, 3)) {
                    if (!(ent instanceof Player found)) continue;
                    if (found == p) continue;

                    Profile pfFound = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(found.getUniqueId());
                    if (pfFound.getProfileState() == ProfileState.SPAWN) continue;

                    fb.remove();
                    cancel();

                    Location loc = found.getLocation();
                    if (loc.getBlock().getType() == Material.WEB)
                        loc.add(0, 1, 0);

                    List<BlockState> placed = new ArrayList<>();

                    for (Location scan : getSurrounding(loc)) {
                        Block b = scan.getBlock();
                        if (b.getType() != Material.AIR) continue;
                        placed.add(b.getState());
                        b.setType(Material.WEB);
                    }

                    Bukkit.getScheduler().runTaskLater(SoupPvP.getInstance(), () -> {
                        for (BlockState bs : placed) restoreBlock(bs);
                    }, 120L);
                    return;
                }
            }
        }.runTaskTimer(SoupPvP.getInstance(), 2L, 2L);
    }
}
