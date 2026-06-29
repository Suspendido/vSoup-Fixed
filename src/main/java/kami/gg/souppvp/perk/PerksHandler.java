package kami.gg.souppvp.perk;

import kami.gg.souppvp.perk.inherit.*;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.ItemBuilder;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
public class PerksHandler {

    private final List<Perk> perks;
    private final List<ItemStack> deathDoUsApartDebuffsList;
    private final Set<String> disabledPerks;

    public PerksHandler() {
        perks = new ArrayList<>();

        perks.add(new BountyHunterPerk());
        perks.add(new SoreLoserPerk());
        perks.add(new MartydomPerk());
        perks.add(new RelinquishPerk());
        perks.add(new TricksterPerk());
        perks.add(new NourishmentPerk());
        perks.add(new RefillerPerk());
        perks.add(new StuntDevilPerk());
        perks.add(new AquamanPerk());

        perks.add(new DistortionPerk());
        perks.add(new JammerPerk());
        perks.add(new ReverseCopyCatPerk());
        perks.add(new SteadyHandsPerk());
        perks.add(new ArmorerPerk());
        perks.add(new DeathDoUsApartPerk());
        perks.add(new TauntPerk());
        perks.add(new RevengePerk());
        perks.add(new InfernoPerk());
        perks.add(new HardlinePerk());

        perks.add(new IncognitoPerk());
        perks.add(new ConartistPerk());
        perks.add(new CreditorPerk());
        perks.add(new CanaPerk());
        perks.add(new BonusHeartsPerk());
        perks.add(new FireFighterPerk());
        perks.add(new LifeSupportPerk());
        perks.add(new JuggernautPerk());

        deathDoUsApartDebuffsList = new ArrayList<>();
        deathDoUsApartDebuffsList.add(new ItemBuilder(Material.POTION).durability(16388).build());
        deathDoUsApartDebuffsList.add(new ItemBuilder(Material.POTION).durability(16420).build());
        deathDoUsApartDebuffsList.add(new ItemBuilder(Material.POTION).durability(16452).build());
        deathDoUsApartDebuffsList.add(new ItemBuilder(Material.POTION).durability(16424).build());
        deathDoUsApartDebuffsList.add(new ItemBuilder(Material.POTION).durability(16456).build());
        deathDoUsApartDebuffsList.add(new ItemBuilder(Material.POTION).durability(16426).build());
        deathDoUsApartDebuffsList.add(new ItemBuilder(Material.POTION).durability(16458).build());
        deathDoUsApartDebuffsList.add(new ItemBuilder(Material.POTION).durability(16460).build());
        deathDoUsApartDebuffsList.add(new ItemBuilder(Material.POTION).durability(16428).build());

        disabledPerks = new HashSet<>();
    }

    public Perk getPerkByName(String name){
        String addSpaceName = name.replaceAll("_", " ");
        for (Perk perk : perks){
            if (perk.getName().equals(addSpaceName)){
                return perk;
            }
        }
        return null;
    }

    public boolean isPerkDisabled(String perkName) {
        return disabledPerks.contains(perkName);
    }

    public void setPerkDisabled(String perkName, boolean disabled) {
        if (disabled) {
            disabledPerks.add(perkName);
        } else {
            disabledPerks.remove(perkName);
        }
    }

    public void togglePerk(String perkName) {
        if (disabledPerks.contains(perkName)) {
            disabledPerks.remove(perkName);
        } else {
            disabledPerks.add(perkName);
        }
    }

    public void removeDisabledPerkFromAllPlayers(String perkName) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            Profile profile = kami.gg.souppvp.SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
            if (profile != null) {
                for (int i = 0; i < profile.getActivePerks().size(); i++) {
                    if (profile.getActivePerks().get(i).equals(perkName)) {
                        profile.getActivePerks().set(i, "None");
                        player.sendMessage(CC.t("&cYour perk &e" + perkName + " &chas been disabled by an administrator."));
                    }
                }
            }
        }
    }

}
