package kami.gg.souppvp.listener;

import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Squid;
import org.bukkit.entity.Wither;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

import java.util.Arrays;
import java.util.List;

public class WorldListener implements Listener {

    private final List<CreatureSpawnEvent.SpawnReason> spawnReasons;

    public WorldListener() {
        this.spawnReasons = Arrays.asList(CreatureSpawnEvent.SpawnReason.SPAWNER_EGG, CreatureSpawnEvent.SpawnReason.EGG, CreatureSpawnEvent.SpawnReason.SPAWNER, CreatureSpawnEvent.SpawnReason.BREEDING, CreatureSpawnEvent.SpawnReason.CUSTOM, CreatureSpawnEvent.SpawnReason.SLIME_SPLIT);
    }

    @EventHandler(ignoreCancelled = true)
    public void onWeather(WeatherChangeEvent e) {
        if (e.toWeatherState()) e.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onSquidSpawn(CreatureSpawnEvent event) {
        Entity entity = event.getEntity();

        if (entity instanceof Squid) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onWitherSpawn(CreatureSpawnEvent e) {
        Entity entity = e.getEntity();

        if (entity instanceof Wither) {
            e.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onSpawn(CreatureSpawnEvent e) {
        if (!spawnReasons.contains(e.getSpawnReason())) {
            e.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onExplode(EntityChangeBlockEvent event) {
        Entity entity = event.getEntity();

        if (entity instanceof Wither || entity instanceof EnderDragon) {
            event.setCancelled(true);
        }
    }
}