package kami.gg.souppvp.events.menu.button;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.events.Events;
import kami.gg.souppvp.events.impl.tnttag.TNTTagGame;
import kami.gg.souppvp.events.impl.tnttag.TNTTagState;
import kami.gg.souppvp.events.impl.tnttag.task.TNTTagStartTask;
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

public class TNTTagEventButton extends Button {

    @Override
    public ItemStack getButtonItem(Player player) {
        TNTTagGame game = SoupPvP.getInstance().getTntTagHandler().getActiveGame();

        List<String> lore = new ArrayList<>();

        lore.add("&b┃ &fRun from the players with a TNT on their heads,");
        lore.add("&b┃ &fIf they hit you, you will need to hit another");
        lore.add("&b┃ &fplayer or you will explode and die.");
        lore.add("");
        lore.add("&b┃ &fRewards for winning:");
        lore.add("&b┃ &b100 credits");
        lore.add("");

        if (game == null) {
            lore.add("&eClick to host!");
        } else {
            lore.add("&bOngoing TNTTag Event:");
            lore.add("&b┃ &fHost: &b" + game.getHost().getUsername());
            lore.add("&b┃ &fParticipants: &b" + game.getEventPlayers().size() + "&f/&b" + game.getMaxPlayers());
            lore.add(game.getState().equals(TNTTagState.WAITING)
                    ? "&b┃ &fState: &bWaiting..."
                    : "&b┃ &fState: &bFighting"
            );
            lore.add("");
            lore.add(game.getEventPlayers().containsKey(player.getUniqueId())
                    ? "&eYou're already in the game!"
                    : "&eClick to join!"
            );
        }

        return new ItemBuilder(Events.TNTTAG.getMaterial())
                .name("&4TNTTag Event")
                .lore(lore)
                .build();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());

        if (profile.getProfileState() != ProfileState.SPAWN) {
            playFail(player);
            sendMessage(player, "&cYou can only do this at spawn.");
            return;
        }

        var handler = SoupPvP.getInstance().getTntTagHandler();
        TNTTagGame game = handler.getActiveGame();

        if (game == null) {
            if (!player.hasPermission("souppvp.tnttaghost")) {
                playFail(player);
                return;
            }

            game = new TNTTagGame(player);
            handler.setActiveGame(game);

            game.handleJoin(player);
            game.setEventTask(new TNTTagStartTask(game));

            playSuccess(player);
            sendMessage(player, "&aTNTTag game created!");
            player.closeInventory();
            return;
        }

        if (game.getState() != TNTTagState.WAITING) {
            playFail(player);
            sendMessage(player, "&cThis TNTTag game already started.");
            return;
        }

        if (game.getEventPlayers().containsKey(player.getUniqueId())) {
            playFail(player);
            sendMessage(player, "&cYou're already in this game!");
            return;
        }

        game.handleJoin(player);
        PlayerUtil.playSound(player, Sound.CLICK);
        player.closeInventory();
        sendMessage(player, "&aYou joined the TNTTag game!");
    }
}