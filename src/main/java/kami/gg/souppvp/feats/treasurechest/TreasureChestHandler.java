package kami.gg.souppvp.feats.treasurechest;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.feats.treasurechest.listener.TreasureChestListener;
import kami.gg.souppvp.feats.treasurechest.reward.TreasureChestReward;
import kami.gg.souppvp.feats.treasurechest.util.PersistableLocation;
import kami.gg.souppvp.lang.Lang;
import kami.gg.souppvp.util.CC;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/*
 * Copyright (c) 2026. @Comunidad, made since 1/7/2026
 * Use or redistribution of this source file is only permitted
 * if explicit permission is given by the author.
 */
@Getter @Setter
public class TreasureChestHandler {

    private final SoupPvP instance;
    private List<TreasureChest> treasureChests = new ArrayList<>();
    private TreasureChestListener treasureChestListener;
    private File folder;

    public TreasureChestHandler(SoupPvP instance) {
        this.instance = instance;

        treasureChestListener = new TreasureChestListener(this.instance);

        Bukkit.getPluginManager().registerEvents(treasureChestListener, instance);
        Bukkit.getServer().getScheduler().runTaskLater(instance, () -> this.loadData(false), 5);

        // Register PersistableLocation serializer
        Bukkit.getServer().getScheduler().runTaskLater(instance, () -> {
            try {
                org.bukkit.configuration.serialization.ConfigurationSerialization.registerClass(PersistableLocation.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 1);
    }

    public void loadData(boolean rewards) {
        this.folder = new File(this.instance.getDataFolder(), "data/treasurechests");

        if (!this.folder.exists()) {
            this.folder.mkdir();
        }

        File[] files = this.folder.listFiles();
        if (files == null) {
            return;
        }

        for (File file : files) {
            final TreasureChest treasureChest = new TreasureChest(file.getName().replace(".yml", ""));

            treasureChest.loadRewards();
            if (!rewards) {
                treasureChest.loadLocations();
            }

            this.treasureChests.add(treasureChest);
        }
    }

    public void saveData() {
        this.treasureChests.forEach(it -> {
            final File file = new File(this.instance.getDataFolder(), "data/treasurechests/" + it.getId() + ".yml");

            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            it.saveCrate(file, YamlConfiguration.loadConfiguration(file));
        });
    }

    public TreasureChestReward openChest(TreasureChest treasureChest, Chest chest, Player player) {
        final TreasureChestReward treasureChestReward = this.findReward(treasureChest, chest, player);
        final ItemStack itemStack = treasureChestReward.getItemStack().clone();

        if (itemStack.getAmount() <= 0) {
            itemStack.setAmount(1);
        }

        final ItemStack displayItem = itemStack.clone();

        if (displayItem.getItemMeta() != null) {
            final ItemMeta itemMeta = displayItem.getItemMeta();

            itemMeta.setDisplayName("#" + ThreadLocalRandom.current().nextInt(50000,1000000));
            displayItem.setItemMeta(itemMeta);
        }

        spawnFireworks(chest.getLocation().add(0.5, -1, 0.5), 1, 0, Color.RED, FireworkEffect.Type.BURST);

        final Item item = chest.getWorld().dropItem(chest.getLocation().getBlock().getRelative(BlockFace.UP).getLocation().add(0.5, 0, 0.5), displayItem);
        item.setVelocity(new Vector(0, 0 , 0));
        item.setMetadata("TREASURE_REWARD", new FixedMetadataValue(SoupPvP.getInstance(), true));

        if (treasureChestReward.isGrantItem()) {
            player.getInventory().addItem(itemStack.clone());
        }

        return treasureChestReward;
    }

    private TreasureChestReward findReward(TreasureChest treasureChest, Chest chest, Player player) {
        final List<TreasureChestReward> rewards = new ArrayList<>(treasureChest.getRewards());
        double sumNumber = rewards.stream().mapToDouble(TreasureChestReward::getChance).sum();
        double random = Math.random() * sumNumber;

        TreasureChestReward choice = new TreasureChestReward(null, 0, "", false, false);

        for (TreasureChestReward treasureChestReward : rewards) {
            double chance = treasureChestReward.getChance();

            choice = treasureChestReward;
            random -= chance;
            if (random < 0) {
                break;
            }
        }

        final String command = choice.getCommand();
        final String item = choice.getItemStack().getItemMeta().hasDisplayName() ? choice.getItemStack().getItemMeta().getDisplayName() : choice.getItemStack().getType().name();

        for (int i = 0; i < 8; i++) {
            Bukkit.broadcastMessage(Lang.format(Lang.TREASURE_CHEST_WON_BROADCAST, "player", player.getName(), "chest_name", treasureChest.getId(), "item", item));
        }

        if (!command.isEmpty() && !command.equalsIgnoreCase("none")) {
            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), command.replace("{player}", player.getName()).replace("{displayName}", item));
        }

        spawnFireworks(chest.getLocation(), 3, 2, Color.RED, FireworkEffect.Type.BALL_LARGE);

        return choice;
    }

    public void spawnFireworks(Location location, int amount, int power, Color color, FireworkEffect.Type fireworkEffect) {
        Firework firework = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
        FireworkMeta fireworkMeta = firework.getFireworkMeta();

        fireworkMeta.setPower(power);
        fireworkMeta.addEffect(FireworkEffect.builder().with(fireworkEffect).with(FireworkEffect.Type.BURST).withColor(color).flicker(true).build());
        firework.setFireworkMeta(fireworkMeta);
        firework.detonate();

        for (int i = 0; i < amount; i++) {
            Firework entityFirework = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
            entityFirework.setFireworkMeta(fireworkMeta);
        }
    }

    public void spawnChestsWithAnimation(TreasureChest treasureChest, Runnable onComplete) {
        Location center = treasureChest.getCentralLocation();
        List<Location> chestLocs = treasureChest.getChests();
        int totalChests = chestLocs.size();
        int[] spawned = {0};

        // Spawn chests one by one with delay
        for (int i = 0; i < chestLocs.size(); i++) {
            final Location chestLoc = chestLocs.get(i);

            new BukkitRunnable() {
                @Override
                public void run() {
                    animateChestSpawn(chestLoc, center, () -> {
                        spawned[0]++;
                        if (spawned[0] >= totalChests) {
                            onComplete.run();
                        }
                    });
                }
            }.runTaskLater(SoupPvP.getInstance(), i * 5L); // 5 ticks delay between each chest
        }
    }

    private void animateChestSpawn(Location targetLoc, Location center, Runnable onComplete) {
        Location spawnLoc = targetLoc.clone().add(0, 10, 0);

        // Use ArmorStand with chest head for animation
        ArmorStand armorStand = (ArmorStand) spawnLoc.getWorld().spawnEntity(spawnLoc, EntityType.ARMOR_STAND);
        armorStand.setGravity(false);
        armorStand.setVisible(false);
        armorStand.setMarker(true);

        // Set chest as head
        ItemStack chestHead = new ItemStack(Material.CHEST);
        armorStand.setHelmet(chestHead);

        new BukkitRunnable() {
            int ticks = 0;
            final int duration = 60; // 3 second animation (slower)

            @Override
            public void run() {
                if (ticks >= duration) {
                    armorStand.remove();
                    targetLoc.getBlock().setType(Material.CHEST);

                    // Orient chest towards center
                    Chest chest = (Chest) targetLoc.getBlock().getState();
                    double dx = center.getX() - targetLoc.getX();
                    double dz = center.getZ() - targetLoc.getZ();

                    BlockFace facing;
                    if (Math.abs(dx) > Math.abs(dz)) {
                        facing = dx > 0 ? BlockFace.EAST : BlockFace.WEST;
                    } else {
                        facing = dz > 0 ? BlockFace.SOUTH : BlockFace.NORTH;
                    }

                    byte data = switch (facing) {
                        case SOUTH -> 3;
                        case WEST -> 4;
                        case EAST -> 5;
                        default -> 2;
                    };
                    targetLoc.getBlock().setData(data);

                    onComplete.run();
                    cancel();
                    return;
                }

                // Move towards target with spiral motion
                double progress = (double) ticks / duration;
                double y = spawnLoc.getY() - (spawnLoc.getY() - targetLoc.getY()) * progress;

                // Add spiral rotation
                double angle = ticks * 0.3; // Slower rotation speed
                double radius = 0.5 * (1 - progress); // Spiral inward
                double x = targetLoc.getX() + Math.cos(angle) * radius;
                double z = targetLoc.getZ() + Math.sin(angle) * radius;

                Location teleportLoc = new Location(targetLoc.getWorld(), x, y, z);
                teleportLoc.setYaw((float) (angle * 57.2958)); // Convert radians to degrees
                armorStand.teleport(teleportLoc);

                ticks++;
            }
        }.runTaskTimer(SoupPvP.getInstance(), 0, 1);
    }
}
