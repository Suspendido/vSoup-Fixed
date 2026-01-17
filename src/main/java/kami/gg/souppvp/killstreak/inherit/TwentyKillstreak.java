package kami.gg.souppvp.killstreak.inherit;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.killstreak.Killstreak;
import kami.gg.souppvp.killstreak.KillstreakReward;
import kami.gg.souppvp.perk.Perk;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

public class TwentyKillstreak extends Killstreak implements Listener {

    private final SoupPvP plugin = SoupPvP.getInstance();

    @Override
    public String getName() {
        return "Grandma Soups";
    }

    @Override
    public int getRequired() {
        return 20;
    }

    @Override
    public ItemStack getIcon() {
        return new ItemBuilder(Material.MUSHROOM_SOUP)
                .enchantment(Enchantment.DURABILITY, 1)
                .name("&a" + getName())
                .lore(
                        CC.MENU_BAR,
                        "&7Gives you 2 Grandma Soups, that will",
                        "&7instantly give maximum health on consumption.",
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

        killer.sendMessage(CC.translate("&aYou've received the &d" + getName() + " &aperk for reaching a &d" + required + " &akillstreak!"));
        giveGrandmaSoups(killer);
    }

    private int getRequiredKillstreak(Profile profile) {
        Perk hardline = plugin.getPerksHandler().getPerkByName("Hardline");
        return (profile.getActivePerks().size() > 1 && plugin.getPerksHandler().getPerkByName(profile.getActivePerks().get(1)) == hardline) ? getRequired() - 1 : getRequired();
    }

    private void giveGrandmaSoups(Player player) {
        for (int i = 0; i < 2; i++) {
            giveSoup(player);
        }
    }

    private void giveSoup(Player player) {
        if (player.getInventory().firstEmpty() != -1) {
            player.getInventory().addItem(KillstreakReward.GRANDMA_SOUP);
            return;
        }

        for (int i = 0; i < player.getInventory().getSize(); i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (item == null) continue;

            if (item.getType() == Material.BOWL || item.getType() == Material.MUSHROOM_SOUP) {
                player.getInventory().setItem(i, KillstreakReward.GRANDMA_SOUP);
                return;
            }
        }
    }
}