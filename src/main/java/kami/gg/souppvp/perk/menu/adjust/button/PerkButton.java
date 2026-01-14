package kami.gg.souppvp.perk.menu.adjust.button;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.perk.Perk;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.ItemBuilder;
import kami.gg.souppvp.util.TaskUtil;
import kami.gg.souppvp.util.menu.Button;
import kami.gg.souppvp.util.menu.menus.ConfirmMenu;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class PerkButton extends Button {

    private final Perk perk;
    private final int tier;

    public PerkButton(Perk perk, int tier) {
        this.perk = perk;
        this.tier = tier;
    }

    @Override
    public ItemStack getButtonItem(Player player) {
        String color = null;
        if (tier == 1) color = "&e";
        if (tier == 2) color = "&c";
        if (tier == 3) color = "&5";

        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        List<String> lore = new ArrayList<>();

        if (perk.getDescription() != null && !perk.getDescription().isEmpty()) {
            lore.addAll(perk.getDescription());
        } else {
            lore.add("&7No description available.");
        }

        lore.add("");

        if (profile.getUnlockedPerks().contains(perk.getName().replaceAll(" ", "_"))){
            if (profile.getActivePerks().get(tier-1).equals(perk.getName().replaceAll(" ", "_"))){
                lore.add("&fStatus: &6Equipped");
                lore.add("");
                lore.add("&aYou currently have this perk enabled.");
            } else {
                lore.add("&fStatus: &aUnlocked");
                lore.add("");
                lore.add("&aClick here to select this perk.");
            }
        } else {
            lore.add("&fStatus: &cLocked");
            lore.add("&fCost: &a" + perk.getCost() + " credits");
            lore.add("");
            lore.add("&eClick here to purchase this perk.");
        }
        return new ItemBuilder(perk.getIcon())
                .name(color + perk.getName())
                .lore(lore)
                .build();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        if (clickType.isLeftClick()) {
            Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
            Perk currentPerk = SoupPvP.getInstance().getPerksHandler().getPerkByName(profile.getActivePerks().get(tier-1));
            if (currentPerk == perk) {
                playFail(player);
                return;
            }
            String formattedPerkName = perk.getName().replaceAll(" ", "_");
            if (profile.getUnlockedPerks().contains(formattedPerkName)) {
                Perk hardlinePerk = SoupPvP.getInstance().getPerksHandler().getPerkByName("Hardline");

                if (SoupPvP.getInstance().getPerksHandler().getPerkByName(profile.getActivePerks().get(tier-1)) == hardlinePerk) {
                    if (profile.getCurrentKillstreak() > 0 ) {
                        sendMessage(player, "&cYou lost your killstreak of " + profile.getCurrentKillstreak() + "!");
                    }

                    profile.setCurrentKillstreak(0);
                }

                profile.getActivePerks().set(tier-1, perk.getName().replaceAll(" ", "_"));
                playNeutral(player);

            } else {
                if (profile.getCredits() >= perk.getCost()) {
                    playNeutral(player);
                    new ConfirmMenu("Select a procedure action", data -> {
                        if (data) {
                            profile.setCredits(profile.getCredits() - perk.getCost());
                            profile.getUnlockedPerks().add(perk.getName().replaceAll(" ", "_"));
                            Perk hardlinePerk = SoupPvP.getInstance().getPerksHandler().getPerkByName("Hardline");
                            if (SoupPvP.getInstance().getPerksHandler().getPerkByName(profile.getActivePerks().get(tier-1)) == hardlinePerk){
                                if (profile.getCurrentKillstreak() > 0 ) {
                                    sendMessage(player, "&cYou lost your killstreak of " + profile.getCurrentKillstreak() + "!");
                                }

                                profile.setCurrentKillstreak(0);
                            }
                            profile.getActivePerks().set(tier-1, perk.getName().replaceAll(" ", "_"));
                            player.sendMessage(CC.translate("&aSuccessfully bought the &e" + perk.getName() + " &aperk."));
                            TaskUtil.runLater(player::closeInventory, 1L);
                        }
                    }).openMenu(player);
                } else {
                    playFail(player);
                }
            }
        }
    }
}
