package kami.gg.souppvp.handlers;

import com.mongodb.client.MongoCollection;
import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.profile.ProfileListeners;
import kami.gg.souppvp.storage.StorageType;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class ProfilesHandler {

    private MongoCollection<Document> mongoCollection; // null si FlatFile
    private final List<Profile> profiles = new ArrayList<>();

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
            Profile profile = new Profile(uuid);
            profiles.add(profile);
        }
    }

    private void initFlatFile() {
        SoupPvP.getInstance().getFlatFileHandler().loadAllProfiles();
        profiles.addAll(SoupPvP.getInstance().getFlatFileHandler().getCachedProfiles().values());

        Bukkit.getLogger().info("[SoupPvP] Loaded " + profiles.size() + " profiles from FlatFile.");
    }

    public Profile getProfileByName(String playerName) {
        Player player = Bukkit.getPlayer(playerName);

        if (player != null) {
            for (Profile profile : profiles) {
                if (profile.getUsername().equalsIgnoreCase(playerName)) {
                    return profile;
                }
            }
        }

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);

        if (offlinePlayer.hasPlayedBefore()) {
            for (Profile profile : profiles) {
                if (profile.getUuid().equals(offlinePlayer.getUniqueId())) {
                    return profile;
                }
            }
            return loadOrCreateProfile(offlinePlayer.getUniqueId());
        }

        if (player != null) {
            return loadOrCreateProfile(player.getUniqueId());
        }

        return null;
    }

    public Profile getProfileByUUID(UUID uuid) {
        for (Profile profile : profiles) {
            if (profile.getUuid().equals(uuid)) {
                return profile;
            }
        }
        return null;
    }

    private Profile loadOrCreateProfile(UUID uuid) {
        Profile profile = new Profile(uuid);
        profiles.add(profile);
        return profile;
    }

    public void saveProfiles() {
        StorageType type = SoupPvP.getInstance().getStorageType();

        if (type == StorageType.MONGODB) {
            saveProfilesMongo();
        } else {
            SoupPvP.getInstance().getFlatFileHandler().saveAllProfiles();
        }
    }

    private void saveProfilesMongo() {
        for (Profile profile : profiles) {
            profile.saveProfile();
        }
    }
}
