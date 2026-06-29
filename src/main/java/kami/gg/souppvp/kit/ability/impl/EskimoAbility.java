package kami.gg.souppvp.kit.ability.impl;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.kit.ability.AbilityItemComparator;
import kami.gg.souppvp.kit.ability.KitAbility;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.profile.ProfileState;
import kami.gg.souppvp.timer.Timer;
import kami.gg.souppvp.util.*;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.concurrent.TimeUnit;

public class EskimoAbility implements KitAbility {

    private final Timer iceTimer;

    public EskimoAbility() {
        this.iceTimer = new Timer(getName(), TimeUnit.SECONDS.toMillis(30));
        SoupPvP.getInstance().getTimerManager().registerTimer(iceTimer);
    }

    @Override
    public String getName() {
        return "Eskimo";
    }

    @Override
    public String getDescription() {
        return "&fCreate ice dome to trap enemies + Strength I";
    }

    @Override
    public String getColor() {
        return "&5";
    }

    @Override
    public ItemStack getItem() {
        return new ItemBuilder(Material.PACKED_ICE).name("&5Ice Dome").build();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        if (profile.isInEvent() || profile.getProfileState() == ProfileState.SPAWN) return;
        if (!hasAbility(player, profile, getName())) return;

        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) return;

        ItemStack hand = player.getItemInHand();
        if (!AbilityItemComparator.isSameAbilityItem(hand, getItem())) return;

        event.setCancelled(true);

        if (SoupPvP.getInstance().getSpawnHandler().getCuboid().contains(player.getLocation())) {
            player.sendMessage(CC.t("&cYou can't do this in spawn."));
            return;
        }

        if (iceTimer.hasTimer(player)) {
            player.sendMessage(CC.t("&cYou can't use this for another &e" + DurationFormatter.getRemaining(iceTimer.getRemaining(player), true) + "&c."));
            return;
        }

        iceTimer.applyTimer(player);
        XPBarTimer.runXpBar(player, 30);
        player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 5 * 20, 1));
        BlockUtil.generateTemporarySphere(player.getLocation().add(0, -1, 0), 5, true, Material.ICE, 5);
    }
}
