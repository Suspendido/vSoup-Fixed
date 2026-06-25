package kami.gg.souppvp.kit.storage;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.kit.CustomKit;
import kami.gg.souppvp.kit.Kit;
import kami.gg.souppvp.kit.ability.KitAbility;
import kami.gg.souppvp.kit.ability.KitAbilityRegistry;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class KitStorage {

    private final File kitsFolder;
    private final KitAbilityRegistry abilityRegistry;

    public KitStorage(SoupPvP plugin, KitAbilityRegistry abilityRegistry) {
        this.abilityRegistry = abilityRegistry;
        this.kitsFolder = new File(plugin.getDataFolder(), "customkits");
        
        if (!kitsFolder.exists()) {
            kitsFolder.mkdirs();
        }
    }

    public void saveKit(Kit kit) {
        File kitFile = new File(kitsFolder, kit.getName() + ".yml");
        
        try {
            if (!kitFile.exists()) {
                kitFile.createNewFile();
            }
            
            FileConfiguration config = YamlConfiguration.loadConfiguration(kitFile);
            KitSerializer.serializeKit(kit, config);
            config.save(kitFile);
            
            SoupPvP.getInstance().getLogger().log(Level.INFO, "Saved kit: " + kit.getName());
        } catch (IOException e) {
            SoupPvP.getInstance().getLogger().log(Level.SEVERE, "Failed to save kit: " + kit.getName(), e);
        }
    }

    public CustomKit loadKit(String kitName) {
        File kitFile = new File(kitsFolder, kitName + ".yml");

        if (!kitFile.exists()) {
            return null;
        }

        try {
            FileConfiguration config = YamlConfiguration.loadConfiguration(kitFile);
            String primaryAbilityName = config.getString("primaryAbility", "None");
            String secondaryAbilityName = config.getString("secondaryAbility", "None");

            KitAbility primaryAbility = abilityRegistry.getAbility(primaryAbilityName);
            KitAbility secondaryAbility = abilityRegistry.getAbility(secondaryAbilityName);

            return KitSerializer.deserializeKit(config, primaryAbility, secondaryAbility);
        } catch (Exception e) {
            SoupPvP.getInstance().getLogger().log(Level.SEVERE, "Failed to load kit: " + kitName, e);
            return null;
        }
    }

    public List<CustomKit> loadAllKits() {
        List<CustomKit> kits = new ArrayList<>();
        
        File[] files = kitsFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        
        if (files != null) {
            for (File file : files) {
                String kitName = file.getName().replace(".yml", "");
                CustomKit kit = loadKit(kitName);
                if (kit != null) {
                    kits.add(kit);
                }
            }
        }
        
        SoupPvP.getInstance().getLogger().log(Level.INFO, "Loaded " + kits.size() + " custom kits");
        return kits;
    }

    public boolean deleteKit(String kitName) {
        File kitFile = new File(kitsFolder, kitName + ".yml");
        
        if (kitFile.exists()) {
            return kitFile.delete();
        }
        
        return false;
    }

    public boolean kitExists(String kitName) {
        File kitFile = new File(kitsFolder, kitName + ".yml");
        return kitFile.exists();
    }
}
