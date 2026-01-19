package kami.gg.souppvp.events.menu.button;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.events.Events;
import kami.gg.souppvp.events.impl.sumo.Sumo;
import kami.gg.souppvp.events.impl.sumo.SumoState;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.profile.ProfileState;
import kami.gg.souppvp.util.ItemBuilder;
import kami.gg.souppvp.util.PlayerUtil;
import kami.gg.souppvp.util.menu.Button;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class SumoEventButton extends Button {

    @Override
    public ItemStack getButtonItem(Player player) {
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        List<String> lore = new ArrayList<>();
        Sumo activeSumo = SoupPvP.getInstance().getSumoHandler().getActiveSumo();

        lore.add("&b┃ &fSingle elimination styled event.");
        lore.add("&b┃ &fAll players take turns fighting for the platform");
        lore.add("&b┃ &fwhile being spectated by other contestants.");
        lore.add("&b┃ &fWin the event by not losing any of your matches.");
        lore.add("");
        lore.add("&b┃ &fRewards for winning:");
        lore.add("&b┃ &b100 credits");
        lore.add("");

        if (activeSumo != null) {
            lore.add("&bOngoing Sumo Event:");
            lore.add("&b┃ &fHost: &b" + activeSumo.getHost().getUsername());
            lore.add("&b┃ &fParticipants: &b" + activeSumo.getEventPlayers().size() + "&f/&b" + activeSumo.getMaxPlayers());
            lore.add(activeSumo.getState().equals(SumoState.WAITING)
                    ? "&b┃ &fState: &bWaiting..."
                    : "&b┃ &fState: &bFighting"
            );
            lore.add("");

            lore.add(profile.isInEvent()
                    ? "&eYou're in this event!"
                    : "&eClick to join!"
            );
        } else {
            lore.add("&eClick to host!");
        }
        return new ItemBuilder(Events.SUMO.getMaterial())
                .name("&dSumo Event")
                .lore(lore)
                .build();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        var sumoHandler = SoupPvP.getInstance().getSumoHandler();
        Sumo activeSumo = sumoHandler.getActiveSumo();

        if (profile.getProfileState() != ProfileState.SPAWN) {
            playFail(player);
            sendMessage(player, "&cYou can only do this at spawn.");
            return;
        }

        if (activeSumo == null) {
            if (!player.hasPermission("souppvp.sumohost")) {
                playFail(player);
                return;
            }

            Sumo sumo = new Sumo(player);
            sumoHandler.setActiveSumo(sumo);
            sumo.handleJoin(player);

            playSuccess(player);
            return;
        }

        if (profile.isInEvent()) {
            playFail(player);
            return;
        }

        activeSumo.handleJoin(player);
        PlayerUtil.playSound(player, Sound.CLICK);
    }

}
