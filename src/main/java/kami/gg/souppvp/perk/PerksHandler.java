package kami.gg.souppvp.perk;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.perk.inherit.*;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.util.CC;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

@Getter
public class PerksHandler {

    private final List<Perk> perks;
    private final Set<String> disabledPerks;
    private final Map<String, Perk> perkMap = new HashMap<>();

    public PerksHandler() {
        perks = new ArrayList<>();

        perks.add(new BountyHunterPerk());
        perks.add(new TricksterPerk());
        perks.add(new StuntDevilPerk());
        perks.add(new AquamanPerk());

        perks.add(new DistortionPerk());
        perks.add(new ArmorerPerk());
        perks.add(new DeathDoUsApartPerk());
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

        for (Perk perk : perks) {
            perkMap.put(perk.getName().toLowerCase(), perk);
        }

        disabledPerks = new HashSet<>();
    }

    public Perk getPerkByName(String name) {
        return perkMap.get(name.toLowerCase());
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

    public void removeDisabledPerkFromAllPlayers(String perkName) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
            if (profile == null) continue;

            List<String> activePerks = profile.getActivePerks();
            for (int i = 0; i < activePerks.size(); i++) {
                if (activePerks.get(i).equalsIgnoreCase(perkName)) {
                    activePerks.set(i, "None");
                    player.sendMessage(CC.t("&cYour perk &e" + perkName + "&c has been disabled by an administrator."));
                }
            }
        }
    }

}
