package kami.gg.souppvp.feats.treasurechest;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.feats.treasurechest.reward.TreasureChestReward;
import kami.gg.souppvp.feats.treasurechest.util.PersistableLocation;
import kami.gg.souppvp.util.CC;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

/*
 * Copyright (c) 2026. @Comunidad, made since 1/7/2026
 * Use or redistribution of this source file is only permitted
 * if explicit permission is given by the author.
 */
@Getter @Setter
public class TreasureChest {
    private String id;
    private int slot;
    private int maxOpened;
    private String displayName;
    private Material material;
    private Location centralLocation;
    private List<Location> chests = new ArrayList<>();
    private List<TreasureChestReward> rewards = new ArrayList<>();
    private Map<UUID, Integer> cache = new HashMap<>();

    public TreasureChest(String id) {
        this.id = id;
    }

    public void loadLocations() {
        final File file = new File(SoupPvP.getInstance().getTreasureChestHandler().getFolder(), id + ".yml");
        final FileConfiguration data = YamlConfiguration.loadConfiguration(file);

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        this.slot = data.getInt("slot");
        this.displayName = data.getString("displayName");

        this.material = Material.valueOf(data.getString("material"));
        this.maxOpened = data.getInt("maxOpened");

        if (data.contains("centralLocation")) {
            this.centralLocation = ((PersistableLocation) data.get("centralLocation")).getLocation();
        }

        if (data.get("location") != null) {
            data.getConfigurationSection("location").getKeys(false).forEach(it -> this.chests.add(((PersistableLocation) data.get("location." + it)).getLocation()));
        }

        if (data.get("players") != null) {
            for (String path : data.getConfigurationSection("players").getKeys(false)) {
                final UUID uuid = UUID.fromString(path);

                cache.put(uuid, data.getInt("players." + path));
            }
        }
    }

    public void loadRewards() {
        final File file = new File(SoupPvP.getInstance().getTreasureChestHandler().getFolder(), id + ".yml");
        final FileConfiguration data = YamlConfiguration.loadConfiguration(file);

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        if (data.get("rewards") == null) {
            return;
        }

        SoupPvP.getInstance().getServer().getScheduler().runTaskLater(SoupPvP.getInstance(), () -> data.getConfigurationSection("rewards").getKeys(false).forEach(it ->
                this.rewards.add(new TreasureChestReward(data.getItemStack("rewards." + it + ".itemStack"),
                        data.getDouble("rewards." + it + ".chance"),
                        data.getString("rewards." + it + ".command"),
                        data.getBoolean("rewards." + it + ".giveItem"), data.getBoolean("rewards." + it + ".broadcast", false)))), 5);
    }

    // Pereza absurda
    public String getDisplayName() {
        return CC.t(displayName);
    }

    public void saveCrate(File file, FileConfiguration data) {
        data.getValues(false).forEach((key, value) -> data.set(key, null));

        int i = 0;

        if (this.centralLocation != null) {
            data.set("centralLocation", new PersistableLocation(this.centralLocation));
        }

        data.set("displayName", this.displayName);
        data.set("slot", this.slot);
        data.set("maxOpened", this.maxOpened);
        data.set("material", this.material.name());

        for (Location location : this.chests) {
            i++;
            data.set("location.location_" + i, new PersistableLocation(location));
        }

        for (Map.Entry<UUID, Integer> entry : cache.entrySet()) {
            data.set("players." + entry.getKey().toString(), entry.getValue());
        }

        for (TreasureChestReward treasureChestReward : this.rewards) {
            i++;
            data.set("rewards.reward_" + i + ".itemStack", treasureChestReward.getItemStack());
            data.set("rewards.reward_" + i + ".chance", treasureChestReward.getChance());
            data.set("rewards.reward_" + i + ".command", treasureChestReward.getCommand());
            data.set("rewards.reward_" + i + ".giveItem", treasureChestReward.isGrantItem());
            data.set("rewards.reward_" + i + ".broadcast", treasureChestReward.isBroadcast());
        }

        try {
            data.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
