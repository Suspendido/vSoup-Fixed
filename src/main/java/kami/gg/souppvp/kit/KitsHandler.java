package kami.gg.souppvp.kit;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.kit.storage.KitStorage;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.util.CC;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
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

    public static void copyDefaultKits() {
        File customKitsFolder = new File(SoupPvP.getInstance().getDataFolder(), "customkits");
        if (!customKitsFolder.exists()) {
            customKitsFolder.mkdirs();
        }

        // Get all .yml files from resources/customkits
        try {
            for (String kitFile : getResourceFolderFiles("customkits")) {
                if (kitFile.endsWith(".yml")) {
                    File targetFile = new File(customKitsFolder, kitFile);
                    if (!targetFile.exists()) {
                        try (InputStream inputStream = SoupPvP.getInstance().getResource("customkits/" + kitFile);
                             FileOutputStream outputStream = new FileOutputStream(targetFile)) {
                            if (inputStream != null) {
                                byte[] buffer = new byte[1024];
                                int length;
                                while ((length = inputStream.read(buffer)) > 0) {
                                    outputStream.write(buffer, 0, length);
                                }
                                SoupPvP.getInstance().getLogger().info("Copied default kit: " + kitFile);
                            }
                        } catch (Exception e) {
                            SoupPvP.getInstance().getLogger().warning("Failed to copy default kit: " + kitFile);
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (Exception e) {
            SoupPvP.getInstance().getLogger().warning("Failed to list default kit files from resources");
            e.printStackTrace();
        }
    }

    private static String[] getResourceFolderFiles(String folderPath) {
        try {
            java.util.List<String> files = new java.util.ArrayList<>();
            java.net.URL url = KitsHandler.class.getClassLoader().getResource(folderPath);
            if (url != null && url.getProtocol().equals("jar")) {
                // Running from JAR
                java.util.jar.JarInputStream jar = new java.util.jar.JarInputStream(
                    new java.io.FileInputStream(new java.io.File(KitsHandler.class.getProtectionDomain().getCodeSource().getLocation().toURI()))
                );
                java.util.jar.JarEntry entry;
                while ((entry = jar.getNextJarEntry()) != null) {
                    String name = entry.getName();
                    if (name.startsWith(folderPath + "/") && !name.equals(folderPath + "/")) {
                        files.add(name.substring(folderPath.length() + 1));
                    }
                }
                jar.close();
            } else {
                // Running from IDE
                File folder = new File(KitsHandler.class.getClassLoader().getResource(folderPath).toURI());
                if (folder.exists() && folder.isDirectory()) {
                    File[] fileList = folder.listFiles();
                    if (fileList != null) {
                        for (File file : fileList) {
                            files.add(file.getName());
                        }
                    }
                }
            }
            return files.toArray(new String[0]);
        } catch (Exception e) {
            SoupPvP.getInstance().getLogger().warning("Failed to list resource folder files: " + folderPath);
            e.printStackTrace();
            return new String[0];
        }
    }
}
