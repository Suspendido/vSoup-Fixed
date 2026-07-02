package kami.gg.souppvp.feats.treasurechest.util;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.LinkedHashMap;
import java.util.Map;

/*
 * Copyright (c) 2026. @Comunidad, made since 1/7/2026
 * Use or redistribution of this source file is only permitted
 * if explicit permission is given by the author.
 */
public class PersistableLocation implements ConfigurationSerializable {

    private final String world;
    private final double x;
    private final double y;
    private final double z;
    private final float yaw;
    private final float pitch;

    public PersistableLocation(Location location) {
        this.world = location.getWorld().getName();
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
        this.yaw = location.getYaw();
        this.pitch = location.getPitch();
    }

    public PersistableLocation(Map<String, Object> map) {
        this.world = (String) map.get("world");
        this.x = (Double) map.get("x");
        this.y = (Double) map.get("y");
        this.z = (Double) map.get("z");
        this.yaw = ((Number) map.get("yaw")).floatValue();
        this.pitch = ((Number) map.get("pitch")).floatValue();
    }

    public Location getLocation() {
        return new Location(org.bukkit.Bukkit.getWorld(world), x, y, z, yaw, pitch);
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("world", world);
        map.put("x", x);
        map.put("y", y);
        map.put("z", z);
        map.put("yaw", yaw);
        map.put("pitch", pitch);
        return map;
    }
}
