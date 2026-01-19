package kami.gg.souppvp.profile;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.coinflip.CoinFlipState;
import kami.gg.souppvp.events.impl.sumo.Sumo;
import kami.gg.souppvp.events.impl.tnttag.TNTTagGame;
import kami.gg.souppvp.feats.storage.StorageType;
import kami.gg.souppvp.kit.progress.KitProgress;
import kami.gg.souppvp.tier.Tiers;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.Bukkit;

import java.text.DecimalFormat;
import java.util.*;

@Getter @Setter
public class Profile {

    private UUID uuid;
    private String username;
    private ProfileState profileState;
    private CoinFlipState coinFlipState;
    private Sumo sumoEvent;
    private TNTTagGame tntTagGame;
    private Tiers tier;

    private Boolean loaded;
    private Boolean enableKillDeathMessages;
    private Boolean enableParticleEffects;
    private Boolean enableKillstreakMessages;
    private Boolean enableScoreboard;
    private Boolean enableEasySoup;

    private String currentKit;
    private String previousKit;

    private List<String> unlockedKits;
    private List<String> activePerks;
    private List<String> unlockedPerks;

    private int kills;
    private int deaths;
    private int credits;
    private int bounty;
    private int experiences;
    private int currentKillstreak;
    private int highestKillstreak;
    private int totalWagerGames;
    private int wagersWon;
    private int wagersLost;
    private int eventsWon;

    private boolean juggernaut;
    private final Map<String, KitProgress> kitProgress = new HashMap<>();

    public Profile(UUID uuid) {
        if (uuid == null) {
            throw new IllegalArgumentException("UUID cannot be null when creating Profile");
        }

        this.uuid = uuid;
        this.username = Bukkit.getOfflinePlayer(uuid).getName();

        if (this.username == null || this.username.isEmpty()) {
            this.username = uuid.toString().substring(0, 8);
            SoupPvP.getInstance().getLogger().warning("Could not get username for UUID: " + uuid);
        }

        initDefaults();
        loadProfile();
    }

    public Profile(String username) {
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }

        this.username = username;
        this.uuid = Bukkit.getOfflinePlayer(username).getUniqueId();

        if (this.uuid == null) {
            throw new IllegalArgumentException("Could not find UUID for username: " + username);
        }

        initDefaults();
        loadProfile();
    }

    private void initDefaults() {
        this.loaded = false;
        this.currentKit = "Default";
        this.previousKit = null;

        this.unlockedKits = new ArrayList<>();
        this.unlockedKits.add("Default");

        this.kills = 0;
        this.deaths = 0;
        this.credits = 0;
        this.bounty = 0;
        this.experiences = 0;
        this.tier = Tiers.ZERO;

        this.currentKillstreak = 0;
        this.highestKillstreak = 0;

        this.activePerks = new ArrayList<>();
        this.activePerks.add("None");
        this.activePerks.add("None");
        this.activePerks.add("None");

        this.unlockedPerks = new ArrayList<>();

        this.totalWagerGames = 0;
        this.wagersWon = 0;
        this.wagersLost = 0;

        this.enableKillDeathMessages = true;
        this.enableParticleEffects = true;
        this.enableKillstreakMessages = true;
        this.enableScoreboard = true;
        this.enableEasySoup = true;

        this.profileState = ProfileState.SPAWN;
        this.coinFlipState = CoinFlipState.NONE;

        this.sumoEvent = null;
        this.tntTagGame = null;
        this.eventsWon = 0;

        this.juggernaut = false;
    }

    public void loadProfile() {
        StorageType storage = SoupPvP.getInstance().getStorageType();

        switch (storage) {
            case MONGODB:
                loadMongo();
                break;

            case FLATFILE:
                loadFlatFile();
                break;
        }
        loaded = true;
    }

    private void loadMongo() {
        var collection = SoupPvP.getInstance().getProfilesHandler().getMongoCollection();
        if (collection == null) return;

        Document doc = collection.find(Filters.eq("uuid", uuid.toString())).first();
        if (doc == null) return;

        this.currentKit = doc.getString("currentKit");
        this.previousKit = doc.getString("previousKit");

        this.unlockedKits = SoupPvP.getGSON().fromJson(doc.getString("unlockedKits"), SoupPvP.getLIST_STRING_TYPE());

        this.kills = doc.getInteger("kills", 0);
        this.deaths = doc.getInteger("deaths", 0);
        this.credits = doc.getInteger("credits", 0);
        this.bounty = doc.getInteger("bounty", 0);
        this.experiences = doc.getInteger("experiences", 0);

        this.tier = Tiers.getTierByNumber(doc.getInteger("tier", 0));

        this.currentKillstreak = doc.getInteger("currentKillstreak", 0);
        this.highestKillstreak = doc.getInteger("highestKillstreak", 0);

        this.activePerks = SoupPvP.getGSON().fromJson(doc.getString("activePerks"), SoupPvP.getLIST_STRING_TYPE());
        this.unlockedPerks = SoupPvP.getGSON().fromJson(doc.getString("unlockedPerks"), SoupPvP.getLIST_STRING_TYPE());

        Document wagers = (Document) doc.get("wagers");
        if (wagers != null) {
            this.totalWagerGames = wagers.getInteger("totalWagersGames", 0);
            this.wagersWon = wagers.getInteger("wagersWon", 0);
            this.wagersLost = wagers.getInteger("wagersLost", 0);
        }

        Document options = (Document) doc.get("options");
        if (options != null) {
            this.enableKillDeathMessages = options.getBoolean("enableKillDeathMessages", true);
            this.enableParticleEffects = options.getBoolean("enableParticleEffects", true);
            this.enableKillstreakMessages = options.getBoolean("enableKillstreakMessages", true);
            this.enableScoreboard = options.getBoolean("enableScoreboard", true);
            this.enableEasySoup = options.getBoolean("enableEasySoup", true);
        }

        Document events = (Document) doc.get("eventsStatistics");
        if (events != null) {
            this.eventsWon = events.getInteger("eventsWon", 0);
        }
    }

    private void saveMongo() {
        var collection = SoupPvP.getInstance().getProfilesHandler().getMongoCollection();
        if (collection == null) return;

        Document doc = new Document();
        doc.put("uuid", uuid.toString());
        doc.put("username", username);

        doc.put("currentKit", currentKit);
        doc.put("previousKit", previousKit);

        doc.put("unlockedKits", SoupPvP.getGSON().toJson(unlockedKits));

        doc.put("kills", kills);
        doc.put("deaths", deaths);
        doc.put("credits", credits);
        doc.put("bounty", bounty);
        doc.put("experiences", experiences);
        doc.put("tier", tier.getTierLevel());

        doc.put("currentKillstreak", currentKillstreak);
        doc.put("highestKillstreak", highestKillstreak);

        doc.put("activePerks", SoupPvP.getGSON().toJson(activePerks));
        doc.put("unlockedPerks", SoupPvP.getGSON().toJson(unlockedPerks));

        Document wagers = new Document();
        wagers.put("totalWagersGames", totalWagerGames);
        wagers.put("wagersWon", wagersWon);
        wagers.put("wagersLost", wagersLost);
        doc.put("wagers", wagers);

        Document options = new Document();
        options.put("enableKillDeathMessages", enableKillDeathMessages);
        options.put("enableParticleEffects", enableParticleEffects);
        options.put("enableKillstreakMessages", enableKillstreakMessages);
        options.put("enableScoreboard", enableScoreboard);
        options.put("enableEasySoup", enableEasySoup);
        doc.put("options", options);

        Document events = new Document();
        events.put("eventsWon", eventsWon);
        doc.put("eventsStatistics", events);

        collection.replaceOne(Filters.eq("uuid", uuid.toString()), doc, new ReplaceOptions().upsert(true));
    }

    private void loadFlatFile() {
        var flat = SoupPvP.getInstance().getFlatFileHandler();
        if (flat == null) return;

        Profile fromFile = flat.loadProfile(uuid);
        if (fromFile == null) {
            this.loaded = true;
            return;
        }

        if (fromFile.getUsername() != null && !fromFile.getUsername().isEmpty()) {
            this.username = fromFile.getUsername();
        }

        this.currentKit = fromFile.getCurrentKit();
        this.previousKit = fromFile.getPreviousKit();
        this.unlockedKits = new ArrayList<>(fromFile.getUnlockedKits());

        this.kills = fromFile.getKills();
        this.deaths = fromFile.getDeaths();
        this.credits = fromFile.getCredits();
        this.bounty = fromFile.getBounty();
        this.experiences = fromFile.getExperiences();
        this.tier = fromFile.getTier();

        this.currentKillstreak = fromFile.getCurrentKillstreak();
        this.highestKillstreak = fromFile.getHighestKillstreak();

        this.activePerks = new ArrayList<>(fromFile.getActivePerks());
        this.unlockedPerks = new ArrayList<>(fromFile.getUnlockedPerks());

        this.totalWagerGames = fromFile.getTotalWagerGames();
        this.wagersWon = fromFile.getWagersWon();
        this.wagersLost = fromFile.getWagersLost();

        this.enableKillDeathMessages = fromFile.getEnableKillDeathMessages();
        this.enableParticleEffects = fromFile.getEnableParticleEffects();
        this.enableKillstreakMessages = fromFile.getEnableKillstreakMessages();
        this.enableScoreboard = fromFile.getEnableScoreboard();
        this.enableEasySoup = fromFile.getEnableEasySoup();

        this.eventsWon = fromFile.getEventsWon();

        this.loaded = true;
    }

    private void saveFlatFile() {
        var flat = SoupPvP.getInstance().getFlatFileHandler();
        if (flat == null) return;

        flat.saveProfile(this);
    }

    public void saveProfile() {
        StorageType storage = SoupPvP.getInstance().getStorageType();

        switch (storage) {
            case MONGODB:
                saveMongo();
                break;

            case FLATFILE:
                saveFlatFile();
                break;
        }
        loaded = true;
    }

    public KitProgress getKitProgress(String kitName) {
        return kitProgress.computeIfAbsent(kitName.toLowerCase(), k -> new KitProgress());
    }

    public boolean isInEvent() {
        return this.sumoEvent != null || this.tntTagGame != null;
    }

    public void removeSpawnTeleportation() {
        SoupPvP.getInstance().getSpawnTeleportationHandler().getSpawnTeleporataion().remove(uuid);
    }

    public boolean isTeleportingToSpawn() {
        return SoupPvP.getInstance().getSpawnTeleportationHandler().getSpawnTeleporataion().containsKey(uuid);
    }

    public void addCombatTag() {
        SoupPvP.getInstance().getCombatTagsHandler().getCombatTags().put(uuid, System.currentTimeMillis() + (15 * 1000));
    }

    public boolean isCombatTagged() {
        Long t = SoupPvP.getInstance().getCombatTagsHandler().getCombatTags().get(uuid);
        return t != null && t - System.currentTimeMillis() > 0;
    }

    public double getWinPercent() {
        if (wagersWon + wagersLost == 0 || wagersWon == 0) return 0;

        return Double.parseDouble(new DecimalFormat("##").format(wagersWon * 100L / (wagersWon + wagersLost)));
    }
}
