package kami.gg.souppvp.events.impl.sumo;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.events.EventType;
import kami.gg.souppvp.events.util.task.EventStartTask;
import kami.gg.souppvp.util.Cooldown;
import kami.gg.souppvp.util.LocationUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

@Getter @Setter
public class SumoHandler {

	private Sumo activeSumo;
	private Location spectatorSpawn;
	private Location spawnA;
    private Location spawnB;

	public SumoHandler() {
		load();
	}

	public void setActiveSumo(Sumo sumo) {
		if (activeSumo != null) {
			activeSumo.setEventTask(null);
		}

		if (sumo == null) {
			activeSumo = null;
			return;
		}

		activeSumo = sumo;
		activeSumo.setEventTask(new EventStartTask(sumo, EventType.SUMO) {
			@Override
			protected Cooldown getCooldown() {
				return sumo.getCooldown();
			}

			@Override
			protected void setCooldown(Cooldown cooldown) {
				sumo.setCooldown(cooldown);
			}

			@Override
			protected void announce() {
				sumo.announce();
			}

			@Override
			protected void onRound() {
				sumo.onRound();
			}
		});
	}

	public void load() {
		spectatorSpawn = LocationUtil.deserialize(SoupPvP.getInstance().getConfig().getString("EVENTS.SUMO.SPECTATOR-SPAWN"));
		spawnA = LocationUtil.deserialize(SoupPvP.getInstance().getConfig().getString("EVENTS.SUMO.SPAWN-A"));
		spawnB = LocationUtil.deserialize(SoupPvP.getInstance().getConfig().getString("EVENTS.SUMO.SPAWN-B"));
	}

	public void save() {
		SoupPvP.getInstance().getConfig().set("EVENTS.SUMO.SPECTATOR-SPAWN", LocationUtil.serialize(spectatorSpawn));
		SoupPvP.getInstance().getConfig().set("EVENTS.SUMO.SPAWN-A", LocationUtil.serialize(spawnA));
		SoupPvP.getInstance().getConfig().set("EVENTS.SUMO.SPAWN-B", LocationUtil.serialize(spawnB));
		SoupPvP.getInstance().saveConfig();
		SoupPvP.getInstance().reloadConfig();
	}

}
