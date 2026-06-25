package kami.gg.souppvp.kit.ability.impl;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.kit.ability.KitAbility;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.profile.ProfileState;
import kami.gg.souppvp.timer.Timer;
import kami.gg.souppvp.util.BlockUtil;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.DurationFormatter;
import kami.gg.souppvp.util.ItemBuilder;
import kami.gg.souppvp.util.XPBarTimer;
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

    private final ItemStack ICE_ITEM = new ItemBuilder(Material.PACKED_ICE).name(CC.t("&5Ice Dome")).build();

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
        return ICE_ITEM.clone();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        if (profile.isInEvent() || profile.getProfileState() == ProfileState.SPAWN) return;

        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) return;

        ItemStack hand = player.getItemInHand();
        if (!hand.isSimilar(ICE_ITEM)) return;

        event.setCancelled(true);

        if (SoupPvP.getInstance().getSpawnHandler().getCuboid().contains(player.getLocation())) {
            player.sendMessage(CC.t("&cYou can't do this in spawn."));
            return;
        }

        if (SoupPvP.getInstance().getTimersHandler().hasTimer(player.getUniqueId(), "Ice Dome", true)) {
            long remaining = SoupPvP.getInstance().getTimersHandler().getRemaining(player.getUniqueId(), "Ice Dome", true);
            player.sendMessage(CC.t("&cYou can't use this for another &e" + DurationFormatter.getRemaining(remaining, true) + "&c."));
            return;
        }

        SoupPvP.getInstance().getTimersHandler().addPlayerTimer(
                player.getUniqueId(),
                new Timer("Ice Dome", TimeUnit.SECONDS.toMillis(30)),
                true
        );

        XPBarTimer.runXpBar(player, 30);
        player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 5 * 20, 1));
        BlockUtil.generateTemporarySphere(player.getLocation().add(0, -1, 0), 5, true, Material.ICE, 5);
    }
}
