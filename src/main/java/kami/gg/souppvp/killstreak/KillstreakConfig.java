package kami.gg.souppvp.killstreak;

import kami.gg.souppvp.SoupPvP;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class KillstreakConfig {
    private final File configFile;
    private FileConfiguration config;

    public KillstreakConfig(SoupPvP plugin) {
        this.configFile = new File(plugin.getDataFolder(), "killstreaks.yml");
        loadConfig();
    }

    public void loadConfig() {
        if (!configFile.exists()) {
            SoupPvP.getInstance().getDataFolder().mkdirs();
            try {
                configFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        config = YamlConfiguration.loadConfiguration(configFile);
        
        if (config.getConfigurationSection("killstreaks") == null) {
            createDefaultConfig();
        }
    }

    public void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createDefaultConfig() {
        config.set("killstreaks", new ArrayList<>());
        saveConfig();
    }

    public List<ConfigurableKillstreak> loadKillstreaks() {
        List<ConfigurableKillstreak> killstreaks = new ArrayList<>();
        ConfigurationSection section = config.getConfigurationSection("killstreaks");
        
        if (section == null) {
            return killstreaks;
        }

        for (String key : section.getKeys(false)) {
            ConfigurationSection ksSection = section.getConfigurationSection(key);
            ConfigurableKillstreak killstreak = loadKillstreak(ksSection);
            killstreaks.add(killstreak);
        }

        return killstreaks;
    }

    private ConfigurableKillstreak loadKillstreak(ConfigurationSection section) {
        int id = Integer.parseInt(section.getName());
        String name = section.getString("name");
        int requiredKills = section.getInt("required_kills");
        String rewardTypeStr = section.getString("reward_type");
        Material iconMaterial = Material.valueOf(section.getString("icon_material", "DIAMOND"));
        List<String> lore = section.getStringList("lore");

        ConfigurableKillstreak.RewardType rewardType = ConfigurableKillstreak.RewardType.valueOf(rewardTypeStr.toUpperCase());
        ConfigurableKillstreak.RewardData rewardData = loadRewardData(section.getConfigurationSection("reward_data"));

        return new ConfigurableKillstreak(id, name, requiredKills, rewardType, rewardData, iconMaterial, lore);
    }

    private ConfigurableKillstreak.RewardData loadRewardData(ConfigurationSection section) {
        if (section == null) {
            return new ConfigurableKillstreak.RewardData();
        }

        ConfigurableKillstreak.RewardData rewardData = new ConfigurableKillstreak.RewardData();

        // Load items
        if (section.contains("items")) {
            List<ItemStack> items = new ArrayList<>();
            for (String itemStr : section.getStringList("items")) {
                ItemStack item = parseItemString(itemStr);
                if (item != null) {
                    items.add(item);
                }
            }
            rewardData.setItems(items);
        }

        // Load effects
        if (section.contains("effects")) {
            List<Map<String, Object>> effects = new ArrayList<>();
            for (String effectStr : section.getStringList("effects")) {
                Map<String, Object> effectMap = parseEffectString(effectStr);
                if (effectMap != null) {
                    effects.add(effectMap);
                }
            }
            rewardData.setEffects(effects);
        }

        // Load special type
        if (section.contains("special_type")) {
            rewardData.setSpecialType(section.getString("special_type"));
        }

        // Load special params
        if (section.contains("special_params")) {
            rewardData.setSpecialParams(section.getConfigurationSection("special_params").getValues(false));
        }

        return rewardData;
    }

    private ItemStack parseItemString(String itemStr) {
        // Format: MATERIAL:amount:durability:name:lore1,lore2
        String[] parts = itemStr.split(":");
        if (parts.length < 1) return null;

        Material material = Material.valueOf(parts[0]);
        int amount = parts.length > 1 ? Integer.parseInt(parts[1]) : 1;
        short durability = parts.length > 2 ? Short.parseShort(parts[2]) : 0;

        kami.gg.souppvp.util.ItemBuilder builder = new kami.gg.souppvp.util.ItemBuilder(material)
                .amount(amount)
                .durability(durability);

        if (parts.length > 3) {
            builder.name(parts[3].replace("_", " "));
        }

        if (parts.length > 4) {
            String[] loreLines = parts[4].split(",");
            builder.lore(loreLines);
        }

        return builder.build();
    }

    private Map<String, Object> parseEffectString(String effectStr) {
        // Format: TYPE:duration:amplifier
        String[] parts = effectStr.split(":");
        if (parts.length < 3) return null;

        Map<String, Object> effect = new HashMap<>();
        effect.put("type", parts[0]);
        effect.put("duration", Integer.parseInt(parts[1]));
        effect.put("amplifier", Integer.parseInt(parts[2]));

        return effect;
    }

    public void saveKillstreak(ConfigurableKillstreak killstreak) {
        int id = killstreak.getId() != 0 ? killstreak.getId() : getNextId();
        String path = "killstreaks." + id;

        config.set(path + ".name", killstreak.getName());
        config.set(path + ".required_kills", killstreak.getRequiredKills());
        config.set(path + ".reward_type", killstreak.getRewardType().name());
        config.set(path + ".icon_material", killstreak.getIconMaterial().name());
        config.set(path + ".lore", killstreak.getLore());

        // Create reward_data section if it doesn't exist
        ConfigurationSection rewardSection = config.getConfigurationSection(path + ".reward_data");
        if (rewardSection == null) {
            rewardSection = config.createSection(path + ".reward_data");
        }
        saveRewardData(rewardSection, killstreak.getRewardData());
        saveConfig();
        
        killstreak.setId(id);
    }

    private void saveRewardData(ConfigurationSection section, ConfigurableKillstreak.RewardData rewardData) {
        if (rewardData.getItems() != null && !rewardData.getItems().isEmpty()) {
            List<String> itemStrings = new ArrayList<>();
            for (ItemStack item : rewardData.getItems()) {
                itemStrings.add(itemToString(item));
            }
            section.set("items", itemStrings);
        }

        if (rewardData.getEffects() != null && !rewardData.getEffects().isEmpty()) {
            List<String> effectStrings = new ArrayList<>();
            for (Map<String, Object> effect : rewardData.getEffects()) {
                effectStrings.add(effectToString(effect));
            }
            section.set("effects", effectStrings);
        }

        if (rewardData.getSpecialType() != null) {
            section.set("special_type", rewardData.getSpecialType());
        }

        if (rewardData.getSpecialParams() != null && !rewardData.getSpecialParams().isEmpty()) {
            section.set("special_params", rewardData.getSpecialParams());
        }
    }

    private String itemToString(ItemStack item) {
        StringBuilder sb = new StringBuilder();
        sb.append(item.getType().name());
        sb.append(":").append(item.getAmount());
        sb.append(":").append(item.getDurability());
        
        if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
            sb.append(":").append(item.getItemMeta().getDisplayName().replace(" ", "_"));
        }
        
        if (item.hasItemMeta() && item.getItemMeta().hasLore()) {
            sb.append(":").append(String.join(",", item.getItemMeta().getLore()));
        }

        return sb.toString();
    }

    private String effectToString(Map<String, Object> effect) {
        return effect.get("type") + ":" + effect.get("duration") + ":" + effect.get("amplifier");
    }

    public void deleteKillstreak(int id) {
        config.set("killstreaks." + id, null);
        saveConfig();
    }

    private int getNextId() {
        ConfigurationSection section = config.getConfigurationSection("killstreaks");
        if (section == null) return 1;

        int maxId = 0;
        for (String key : section.getKeys(false)) {
            try {
                int id = Integer.parseInt(key);
                if (id > maxId) {
                    maxId = id;
                }
            } catch (NumberFormatException ignored) {
            }
        }

        return maxId + 1;
    }
}
