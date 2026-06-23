package kami.gg.souppvp.perk.inherit.tier2;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.perk.Perk;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.util.CC;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ArmorerPerk extends Perk implements Listener {

    @Override
    public String getName() {
        return "Armorer";
    }

    @Override
    public List<String> getDescription() {
        List<String> lore = new ArrayList<>();
        lore.add(CC.t("&7Gain an extra 10 durability every"));
        lore.add(CC.t("&7time you kill another player."));
        return lore;
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.DIAMOND_CHESTPLATE);
    }

    @Override
    public int getCost() {
        return 800;
    }

    @Override
    public int getTier() {
        return 2;
    }

    @EventHandler
    public void onPlayerDeathEvent(PlayerDeathEvent event){
        if (event.getEntity().getKiller() == null) return;
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(event.getEntity().getKiller().getUniqueId());
        if (profile.isInEvent()) return;
        Perk currentPerk = SoupPvP.getInstance().getPerksHandler().getPerkByName(profile.getActivePerks().get(1));
        if (currentPerk == null) return;
        Perk armorerPerk = SoupPvP.getInstance().getPerksHandler().getPerkByName("Armorer");
        if (currentPerk == armorerPerk){
            for (ItemStack itemStack : event.getEntity().getKiller().getInventory().getArmorContents()){
                if (itemStack == null) return;
                itemStack.setDurability((short) (itemStack.getDurability() - 10));
            }
        }
    }

}
