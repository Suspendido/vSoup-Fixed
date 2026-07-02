package kami.gg.souppvp.kit;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.kit.storage.KitStorage;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.util.CC;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class KitsHandler {

    @Getter public List<Kit> kits;
    private final KitStorage kitStorage;

    public KitsHandler(KitStorage kitStorage) {
        this.kitStorage = kitStorage;
        kits = new ArrayList<>();

        // Load all kits from storage (both default and custom)
        loadAllKits();
    }

    private void loadAllKits() {
        List<CustomKit> customKits = kitStorage.loadAllKits();

        for (CustomKit kit : customKits) {
            addKit(kit);

            if (kit.getPrimaryAbility() != null) {
                SoupPvP.getInstance().getServer().getPluginManager().registerEvents(kit.getPrimaryAbility(), SoupPvP.getInstance());
            }

            if (kit.getSecondaryAbility() != null) {
                SoupPvP.getInstance().getServer().getPluginManager().registerEvents(kit.getSecondaryAbility(), SoupPvP.getInstance());
            }
        }
    }

    public void saveCustomKit(CustomKit kit) {
        kitStorage.saveKit(kit);
        if (!kits.contains(kit)) {
            addKit(kit);
        }

        if (kit.getPrimaryAbility() != null) {
            SoupPvP.getInstance().getServer().getPluginManager().registerEvents(kit.getPrimaryAbility(), SoupPvP.getInstance());
        }

        if (kit.getSecondaryAbility() != null) {
            SoupPvP.getInstance().getServer().getPluginManager().registerEvents(kit.getSecondaryAbility(), SoupPvP.getInstance());
        }
    }

    public boolean deleteCustomKit(String kitName) {
        Kit kit = getKitByName(kitName);
        if (kit instanceof CustomKit) {
            // Store the kit price for refunds
            int kitPrice = kit.getPrice();
            
            // Find a fallback kit (Default kit)
            Kit fallbackKit = getKitByName("Default");
            
            // Update all players who have this kit selected or purchased
            for (Profile profile : SoupPvP.getInstance().getProfilesHandler().getProfiles().values()) {
                // Refund credits if player had purchased this kit
                if (profile.getUnlockedKits().contains(kitName)) {
                    profile.getUnlockedKits().remove(kitName);
                    profile.setCredits(profile.getCredits() + kitPrice);
                    
                    // Notify online players about the refund
                    Player player = Bukkit.getPlayer(profile.getUuid());
                    if (player != null && player.isOnline()) {
                        player.sendMessage(CC.t("&eThe kit &r" + kit.getRarityType().getColor() + kitName + " &ehas been removed from the server."));
                        player.sendMessage(CC.t("&aYou have been refunded &6" + kitPrice + " &acredits."));
                    }
                }
                
                if (kitName.equalsIgnoreCase(profile.getCurrentKit())) {
                    profile.setCurrentKit(fallbackKit != null ? fallbackKit.getName() : null);
                    profile.setPreviousKit(fallbackKit != null ? fallbackKit.getName() : null);
                    
                    // If player is online, equip the fallback kit
                    Player player = Bukkit.getPlayer(profile.getUuid());
                    if (player != null && player.isOnline() && fallbackKit != null) {
                        fallbackKit.equipKit(player);
                    }
                }
                
                // Also update previous kit if it matches
                if (kitName.equalsIgnoreCase(profile.getPreviousKit())) {
                    profile.setPreviousKit(fallbackKit != null ? fallbackKit.getName() : null);
                }
            }
            
            kits.remove(kit);
            return kitStorage.deleteKit(kitName);
        }
        return false;
    }

    public Kit getKitByName(String name) {
        for (Kit kit : getKits()) {
            if (kit.getName().equalsIgnoreCase(name)) {
                return kit;
            }
        }
        return null;
    }

    public void addKit(Kit kit) {
        getKits().add(kit);
    }

    public boolean hasKitUnlocked(Profile profile, Kit kit) {
        if (kit == null) return false;
        if (SoupPvP.getIsFreeKitsMode()) return true;
        return profile.getUnlockedKits().contains(kit.getName());
    }

    public boolean isKitAvailable(String kitName) {
        Kit kit = getKitByName(kitName);
        return kit != null && kit.isEnabled();
    }
}
