package kami.gg.souppvp.perk.menu.button;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.perk.Perk;
import kami.gg.souppvp.perk.menu.AllPerksMenu;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.util.ItemBuilder;
import kami.gg.souppvp.util.menu.Button;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class SlotCycleButton extends Button {

    @Override
    public ItemStack getButtonItem(Player player) {
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        int selected = AllPerksMenu.getSelectedSlot(player);
        
        List<String> lore = new ArrayList<>();

        lore.add("&7Each slot can hold one perk.");
        lore.add("&7Click this to cycle through your slots,");
        lore.add("&7then click a perk to equip");
        lore.add("");

        for (int i = 0; i < 3; i++) {
            String perkName = profile.getActivePerks().get(i);
            Perk perk = SoupPvP.getInstance().getPerksHandler().getPerkByName(perkName);
            String display = perk != null ? perk.getColor() + perk.getName() : "&cNone";
            
            if (i == selected) {
                lore.add("&a▶ Slot " + (i + 1) + " &8(" + display + "&8)");
            } else {
                lore.add("&7Slot " + (i + 1) + " &8(" + display + "&8)");
            }
        }
        lore.add("");
        lore.add("&eClick to scroll through!");

        return new ItemBuilder(Material.SIGN)
                .name("&b&lSelect Perk Slot")
                .lore(lore)
                .build();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        int current = AllPerksMenu.getSelectedSlot(player);
        AllPerksMenu.setSelectedSlot(player, (current + 1) % 3);
        playNeutral(player);
        new AllPerksMenu().openMenu(player);
    }
}