package kami.gg.souppvp.listener;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.coinflip.listener.CoinFlipListener;
import kami.gg.souppvp.coinflip.listener.WagerCustomEventListeners;
import kami.gg.souppvp.events.impl.sumo.listener.SumoListener;
import kami.gg.souppvp.events.impl.tnttag.listener.TNTTagListener;
import kami.gg.souppvp.feats.nametag.NametagListener;
import kami.gg.souppvp.feats.staff.listener.StaffListener;
import kami.gg.souppvp.feats.tablist.TablistListener;
import kami.gg.souppvp.juggernaut.JuggernautListener;
import kami.gg.souppvp.listener.impl.*;
import kami.gg.souppvp.perk.inherit.*;
import kami.gg.souppvp.tier.TiersListener;
import kami.gg.souppvp.timer.listener.TimersListener;
import kami.gg.souppvp.util.menu.MenuListener;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

@Getter
public class ListenerManager {
    private final List<Listener> listeners;
    private final List<BukkitTask> tasks;

    public ListenerManager() {
        this.listeners = new ArrayList<>();
        this.tasks = new ArrayList<>();
        this.load();
    }

    private void load() {
        listeners.addAll(Arrays.asList(
                new BountyListener(),
                new ChatListener(),
                new GeneralListeners(),
                new KillStreakAnnouncerListener(),
                new NoFallDamageListener(),
                new PlayerListeners(),
                new PvPListeners(),
                new ShopItemsListener(),
                new SoupListeners(),
                new SpawnEventItemsListener(),
                new SpawnListeners(),
                new NerfsListener(),
                new WorldListener(),
                new NametagListener(),
                new TablistListener(),
                new MenuListener(SoupPvP.getInstance().getMenuManager()),
                new TiersListener(),
                new CoinFlipListener(),
                new WagerCustomEventListeners(),
                new TimersListener(),

                // Staff
                new StaffListener(SoupPvP.getInstance().getStaffManager()),

                // Events
                new SumoListener(),
                new TNTTagListener(),

                // MiniEvent
                new JuggernautListener(),

                // Perks
                new StuntDevilPerk(),
                new AquamanPerk(),
                new DistortionPerk(),
                new ArmorerPerk(),
                new DeathDoUsApartPerk(),
                new InfernoPerk(),
                new CreditorPerk(),
                new CanaPerk(),
                new BonusHeartsPerk(),
                new FireFighterPerk(),
                new LifeSupportPerk(),
                new JuggernautPerk()

        ));

        listeners.forEach(listener ->
                Bukkit.getPluginManager().registerEvents(listener, SoupPvP.getInstance())
        );
    }

    public void reload() {
        for (Listener listener : listeners) {
            HandlerList.unregisterAll(listener);
        }

        iterate(tasks, (task) -> {
            task.cancel();
            return true;
        });

        this.load();
    }

    public static <T> void iterate(Collection<T> list, Predicate<T> consumer) {
        list.removeIf(consumer);
    }
}
