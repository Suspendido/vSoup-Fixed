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

public class PerkButton extends Button {

    private final Perk perk;

    public PerkButton(Perk perk) {
        this.perk = perk;
    }

    @Override
    public ItemStack getButtonItem(Player player) {
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        int selectedSlot = AllPerksMenu.getSelectedSlot(player);

        boolean unlocked = profile.getUnlockedPerks().contains(perk.getName());
        boolean equipped = perk.getName().equals(profile.getActivePerks().get(selectedSlot));

        ItemStack icon = perk.getIcon() != null ? perk.getIcon() : new ItemStack(Material.BARRIER);

        List<String> lore = new ArrayList<>();

        lore.addAll(perk.getDescription());
        lore.add("");
        lore.add(equipped ? "&cClick to unequip" : unlocked ?
                "&eClick to equip in &bSlot " + (selectedSlot + 1) : "&eClick to purchase for &b" + perk.getCost() + " credits");

        return new ItemBuilder(icon)
                .name(perk.getColor() + perk.getName())
                .lore(lore)
                .setGlow(equipped)
                .build();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        int selectedSlot = AllPerksMenu.getSelectedSlot(player);

        if (!profile.getUnlockedPerks().contains(perk.getName())) {
            if (profile.getCredits() < perk.getCost()) {
                sendMessage(player, "&cInsufficient credits! You're " + (profile.getCredits() - perk.getCost()) + " credits short.");
                playFail(player);
                return;
            }

            profile.setCredits(profile.getCredits() - perk.getCost());
            profile.getUnlockedPerks().add(perk.getName());
            sendMessage(player, "&aPurchased " + perk.getColor() + perk.getName() + "&a!");
            playSuccess(player);
            new AllPerksMenu().openMenu(player);
            return;
        }

        if (perk.getName().equals(profile.getActivePerks().get(selectedSlot))) {
            profile.getActivePerks().set(selectedSlot, "None");
            sendMessage(player, "&cUnequipped " + perk.getColor() + perk.getName() + " &cfrom Slot " + (selectedSlot + 1) + "&c.");
            playFail(player);
            new AllPerksMenu().openMenu(player);
            return;
        }

        int existingSlot = profile.getActivePerks().indexOf(perk.getName());
        if (existingSlot != -1 && existingSlot != selectedSlot) {
            profile.getActivePerks().set(existingSlot, "None");
        }

        profile.getActivePerks().set(selectedSlot, perk.getName());
        sendMessage(player, "&aEquipped " + perk.getColor() + perk.getName() + " &ato Slot " + (selectedSlot + 1) + "&a!");
        playSuccess(player);
        new AllPerksMenu().openMenu(player);
    }
}