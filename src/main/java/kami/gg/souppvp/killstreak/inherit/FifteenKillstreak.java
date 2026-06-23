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

public class FifteenKillstreak extends Killstreak implements Listener {

    private final SoupPvP plugin = SoupPvP.getInstance();

    @Override
    public String getName() {
        return "Fire Resistance Potion";
    }

    @Override
    public int getRequired() {
        return 15;
    }

    @Override
    public ItemStack getIcon() {
        return new ItemBuilder(Material.POTION)
                .durability(8259)
                .name(CC.t("&a" + getName()))
                .lore(
                        CC.MENU_BAR,
                        "&7Gives you a potion that will apply",
                        "&7fire resistance for 8 minutes.",
                        CC.MENU_BAR,
                        "",
                        "&fKillstreak Required: &d" + getRequired()
                ).build();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (event.getEntity().getKiller() == null) return;

        Player killer = event.getEntity().getKiller();
        Profile profile = plugin.getProfilesHandler().getProfileByUUID(killer.getUniqueId());

        int required = getRequiredKillstreak(profile);
        if (profile.getCurrentKillstreak() != required) return;

        killer.sendMessage(CC.t("&aYou've received the &d" + getName() + " &aperk for reaching a &d" + required + " &akillstreak!"));
        giveItem(killer, KillstreakReward.FIRE_RESISTANCE_POTION);
    }

    private int getRequiredKillstreak(Profile profile) {
        Perk hardline = plugin.getPerksHandler().getPerkByName("Hardline");
        return (profile.getActivePerks().size() > 1 && plugin.getPerksHandler().getPerkByName(profile.getActivePerks().get(1)) == hardline) ? getRequired() - 1 : getRequired();
    }

    private void giveItem(Player player, ItemStack item) {
        if (player.getInventory().firstEmpty() != -1) {
            player.getInventory().addItem(item);
            return;
        }

        for (int i = 0; i < player.getInventory().getSize(); i++) {
            ItemStack current = player.getInventory().getItem(i);
            if (current == null) continue;

            if (current.getType() == Material.BOWL || current.getType() == Material.MUSHROOM_SOUP) {
                player.getInventory().setItem(i, item);
                return;
            }
        }
    }
}