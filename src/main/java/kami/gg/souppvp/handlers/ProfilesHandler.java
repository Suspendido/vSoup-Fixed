package kami.gg.souppvp.handlers;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.profile.ProfileListeners;
import kami.gg.souppvp.feats.storage.StorageType;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class ProfilesHandler {

    private MongoCollection<Document> mongoCollection; // null si FlatFile
    private final Map<UUID, Profile> profiles = new ConcurrentHashMap<>();

    public ProfilesHandler() {
        SoupPvP.getInstance().getServer().getPluginManager().registerEvents(new ProfileListeners(), SoupPvP.getInstance());
        StorageType storage = SoupPvP.getInstance().getStorageType();

        if (storage == StorageType.MONGODB) {
            initMongo();
        } else {
            initFlatFile();
        }
    }

    private void initMongo() {
        if (SoupPvP.getInstance().getMongoDatabase() == null) {
            Bukkit.getLogger().severe("[SoupPvP] MongoDB selected, but mongoDatabase is NULL!");
            return;
        }

        mongoCollection = SoupPvP.getInstance().getMongoDatabase().getCollection("Profiles");

        loadMongoProfiles();
    }

    private void loadMongoProfiles() {
        for (Document doc : mongoCollection.find()) {
            UUID uuid = UUID.fromString(doc.getString("uuid"));
            profiles.computeIfAbsent(uuid, Profile::new);
        }
    }


    private void initFlatFile() {
        SoupPvP.getInstance().getFlatFileHandler().loadAllProfiles();
        profiles.putAll(SoupPvP.getInstance().getFlatFileHandler().getCachedProfiles());

        Bukkit.getLogger().info("[SoupPvP] Loaded " + profiles.size() + " profiles from FlatFile.");
    }


    public Profile getProfileByName(String playerName) {
        Player player = Bukkit.getPlayerExact(playerName);
        if (player != null) {
            return getProfileByUUID(player.getUniqueId());
        }

        OfflinePlayer offline = Bukkit.getOfflinePlayer(playerName);
        if (offline.hasPlayedBefore()) {
            return getProfileByUUID(offline.getUniqueId());
        }

        return null;
    }

    public Profile getProfileByUUID(UUID uuid) {
        return profiles.computeIfAbsent(uuid, Profile::new);
    }

    public void saveProfiles() {
        StorageType type = SoupPvP.getInstance().getStorageType();

        if (type == StorageType.MONGODB) {
            saveProfilesMongo();
        } else {
            SoupPvP.getInstance().getFlatFileHandler().saveAllProfiles();
        }
    }

    public boolean deleteProfile(UUID uuid) {
        if (uuid == null) {
            SoupPvP.getInstance().getLogger().warning("Attempted to delete profile with null UUID");
            return false;
        }

        StorageType storage = SoupPvP.getInstance().getStorageType();
        boolean deleted = false;

        if (storage == StorageType.MONGODB) {
            deleted = deleteProfileMongo(uuid);
        } else {
            SoupPvP.getInstance().getFlatFileHandler().deleteProfile(uuid);
        }

        if (profiles.remove(uuid) != null) {
            SoupPvP.getInstance().getLogger().info("Removed profile from memory: " + uuid);
        }

        return deleted;
    }

    private boolean deleteProfileMongo(UUID uuid) {
        if (mongoCollection == null) {
            SoupPvP.getInstance().getLogger().severe("MongoDB collection is null");
            return false;
        }

        try {
            var result = mongoCollection.deleteOne(Filters.eq("uuid", uuid.toString()));

            if (result.getDeletedCount() > 0) {
                SoupPvP.getInstance().getLogger().info("Deleted profile from MongoDB: " + uuid);
                return true;
            } else {
                SoupPvP.getInstance().getLogger().warning("Profile not found in MongoDB: " + uuid);
                return false;
            }
        } catch (Exception e) {
            SoupPvP.getInstance().getLogger().severe("Failed to delete profile from MongoDB: " + uuid);
            e.printStackTrace();
            return false;
        }
    }

    private void saveProfilesMongo() {
        for (Profile profile : profiles.values()) {
            profile.saveProfile();
        }
    }
}
