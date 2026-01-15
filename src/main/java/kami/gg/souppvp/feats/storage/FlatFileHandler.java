package kami.gg.souppvp.feats.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.profile.Profile;
import lombok.Getter;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class FlatFileHandler {

    private final SoupPvP instance;
    private final Gson gson;
    private final File profilesFolder;
    private final Map<UUID, Profile> cachedProfiles;

    public FlatFileHandler(SoupPvP instance) {
        this.instance = instance;
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();

        this.profilesFolder = new File(instance.getDataFolder(), "profiles");
        if (!profilesFolder.exists()) {
            profilesFolder.mkdirs();
        }

        this.cachedProfiles = new ConcurrentHashMap<>();
    }

    public void saveProfile(Profile profile) {
        File file = new File(profilesFolder, profile.getUuid().toString() + ".json");

        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(profile, writer);
        } catch (IOException e) {
            instance.getLogger().severe("Failed to save profile: " + profile.getUuid());
            e.printStackTrace();
        }
    }

    public void deleteProfile(Profile profile) {
        File file = new File(profilesFolder, profile.getUuid().toString() + ".json");


    }

    public void saveAllProfiles() {
        int saved = 0;

        for (Profile profile : cachedProfiles.values()) {
            saveProfile(profile);
            saved++;
        }

        instance.getLogger().info("Saved " + saved + " profiles to flat file.");
    }

    public Profile loadProfile(UUID uuid) {
        if (cachedProfiles.containsKey(uuid)) {
            return cachedProfiles.get(uuid);
        }

        File file = new File(profilesFolder, uuid.toString() + ".json");

        if (!file.exists()) {
            return null;
        }

        try (FileReader reader = new FileReader(file)) {
            Profile profile = gson.fromJson(reader, Profile.class);
            cachedProfiles.put(uuid, profile);
            return profile;
        } catch (IOException e) {
            instance.getLogger().severe("Failed to load profile: " + uuid);
            e.printStackTrace();
            return null;
        }
    }

    public void loadAllProfiles() {
        File[] files = profilesFolder.listFiles((dir, name) -> name.endsWith(".json"));

        if (files == null) return;

        int loaded = 0;

        for (File file : files) {
            try {
                String uuidString = file.getName().replace(".json", "");
                UUID uuid = UUID.fromString(uuidString);

                try (FileReader reader = new FileReader(file)) {
                    Profile profile = gson.fromJson(reader, Profile.class);
                    cachedProfiles.put(uuid, profile);
                    loaded++;
                }
            } catch (Exception e) {
                instance.getLogger().warning("Failed to load profile from file: " + file.getName());
                e.printStackTrace();
            }
        }

        instance.getLogger().info("Loaded " + loaded + " profiles from FlatFile.");
    }

    public void deleteProfile(UUID uuid) {
        File file = new File(profilesFolder, uuid.toString() + ".json");

        if (file.exists()) {
            file.delete();
        }

        cachedProfiles.remove(uuid);
    }

    public boolean profileExists(UUID uuid) {
        if (cachedProfiles.containsKey(uuid)) {
            return true;
        }

        File file = new File(profilesFolder, uuid.toString() + ".json");
        return file.exists();
    }

    public Set<UUID> getAllProfileUUIDs() {
        Set<UUID> uuids = new HashSet<>();
        File[] files = profilesFolder.listFiles((dir, name) -> name.endsWith(".json"));

        if (files != null) {
            for (File file : files) {
                try {
                    String uuidString = file.getName().replace(".json", "");
                    uuids.add(UUID.fromString(uuidString));
                } catch (IllegalArgumentException e) {
                    instance.getLogger().warning("Invalid UUID in filename: " + file.getName());
                }
            }
        }

        return uuids;
    }

    public Profile getProfileFromCache(UUID uuid) {
        return cachedProfiles.get(uuid);
    }

    public void cacheProfile(Profile profile) {
        cachedProfiles.put(profile.getUuid(), profile);
    }

    public void uncacheProfile(UUID uuid) {
        cachedProfiles.remove(uuid);
    }

    public void clearCache() {
        cachedProfiles.clear();
    }

    public StorageStats getStats() {
        File[] files = profilesFolder.listFiles((dir, name) -> name.endsWith(".json"));
        int fileCount = files != null ? files.length : 0;

        long totalSize = 0;
        if (files != null) {
            for (File file : files) {
                totalSize += file.length();
            }
        }

        return new StorageStats(fileCount, cachedProfiles.size(), totalSize);
    }

    @Getter
    public static class StorageStats {
        private final int filesOnDisk;
        private final int profilesInCache;
        private final long totalSizeBytes;

        public StorageStats(int filesOnDisk, int profilesInCache, long totalSizeBytes) {
            this.filesOnDisk = filesOnDisk;
            this.profilesInCache = profilesInCache;
            this.totalSizeBytes = totalSizeBytes;
        }

        public String getTotalSizeMB() {
            return String.format("%.2f MB", totalSizeBytes / (1024.0 * 1024.0));
        }
    }
}