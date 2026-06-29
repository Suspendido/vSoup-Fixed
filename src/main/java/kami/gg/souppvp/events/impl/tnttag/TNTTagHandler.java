package kami.gg.souppvp.events.impl.tnttag;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.events.EventType;
import kami.gg.souppvp.events.util.task.EventStartTask;
import kami.gg.souppvp.util.Cooldown;
import kami.gg.souppvp.util.LocationUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

@Getter @Setter
public class TNTTagHandler {

    private TNTTagGame activeGame;

    private Location spectatorSpawn;
    private Location eventSpawn;

    public TNTTagHandler() {
        load();
    }

    public void setActiveGame(TNTTagGame game) {
        if (activeGame != null) {
            activeGame.setEventTask(null);
        }

        if (game == null) {
            activeGame = null;
            return;
        }

        activeGame = game;
        activeGame.setEventTask(new EventStartTask(game, EventType.TNTTAG) {
            @Override
            protected Cooldown getCooldown() {
                return game.getCooldown();
            }

            @Override
            protected void setCooldown(Cooldown cooldown) {
                game.setCooldown(cooldown);
            }

            @Override
            protected void announce() {
                game.announce();
            }

            @Override
            protected void onRound() {
                game.onRound();
            }
        });
    }

    public void load() {
        spectatorSpawn = LocationUtil.deserialize(SoupPvP.getInstance().getConfig().getString("EVENTS.TNTTAG.SPECTATOR-SPAWN"));
        eventSpawn = LocationUtil.deserialize(SoupPvP.getInstance().getConfig().getString("EVENTS.TNTTAG.EVENT-SPAWN"));
    }

    public void save() {
        SoupPvP.getInstance().getConfig().set("EVENTS.TNTTAG.SPECTATOR-SPAWN", LocationUtil.serialize(spectatorSpawn));
        SoupPvP.getInstance().getConfig().set("EVENTS.TNTTAG.EVENT-SPAWN", LocationUtil.serialize(eventSpawn));

        SoupPvP.getInstance().saveConfig();
        SoupPvP.getInstance().reloadConfig();
    }
}
