package kami.gg.souppvp.killstreak.inherit;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.killstreak.Killstreak;
import kami.gg.souppvp.killstreak.KillstreakReward;
import kami.gg.souppvp.perk.Perk;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

public class TenKillstreak extends Killstreak implements Listener {

    @Override
    public String getName() {
        return "Golden Apples";
    }

    @Override
    public int getRequired() {
        return 10;
    }

    @Override
    public ItemStack getIcon() {
        return new ItemBuilder(Material.GOLDEN_APPLE)
                .name("&a" + getName())
                .lore(
                        CC.MENU_BAR,
                        "&7Gives you 8 golden apples for",
                        "&7extra absorption hearts.",
                        CC.MENU_BAR,
                        "",
                        "&fKillstreak Required: &d" + getRequired(),
                        ""
                ).build();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeathEvent(PlayerDeathEvent event) {
        if (event.getEntity().getKiller() == null) return;

        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(event.getEntity().getKiller().getUniqueId());
        if (profile == null) return;
        Perk hardline = SoupPvP.getInstance().getPerksHandler().getPerkByName("Hardline");

        boolean hasHardline = false;
        if (profile.getActivePerks().size() > 1) {
            hasHardline = hardline.equals(SoupPvP.getInstance().getPerksHandler().getPerkByName(profile.getActivePerks().get(1)));
        }

        int requiredKills = hasHardline ? getRequired() - 1 : getRequired();
        if (profile.getCurrentKillstreak() != requiredKills) return;
        var killer = event.getEntity().getKiller();

        killer.sendMessage(CC.t("&aYou've received the &d" + getName() + " &aperk for reaching a &d" + getRequired() + " &akillstreak!"));
        giveItemReplacingSoup(killer, KillstreakReward.GOLDEN_APPLES);
    }

    private void giveItemReplacingSoup(Player player, ItemStack reward) {
        if (player.getInventory().firstEmpty() != -1) {
            player.getInventory().addItem(reward);
            return;
        }

        for (int i = 0; i < player.getInventory().getSize(); i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (item == null) continue;

            if (item.getType() == Material.BOWL || item.getType() == Material.MUSHROOM_SOUP) {
                player.getInventory().setItem(i, reward);
                return;
            }
        }
    }

}
