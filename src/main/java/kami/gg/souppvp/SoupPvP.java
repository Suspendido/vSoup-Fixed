package kami.gg.souppvp;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import kami.gg.souppvp.coinflip.listener.CoinFlipListener;
import kami.gg.souppvp.coinflip.listener.WagerCustomEventListeners;
import kami.gg.souppvp.events.impl.sumo.SumoHandler;
import kami.gg.souppvp.events.impl.sumo.listener.SumoListener;
import kami.gg.souppvp.events.impl.tnttag.TNTTagHandler;
import kami.gg.souppvp.events.impl.tnttag.listener.TNTTagListener;
import kami.gg.souppvp.feats.hooks.placeholder.PlaceholderHook;
import kami.gg.souppvp.feats.staff.StaffManager;
import kami.gg.souppvp.feats.staff.listener.StaffListener;
import kami.gg.souppvp.handlers.*;
import kami.gg.souppvp.feats.hooks.clients.ClientHook;
import kami.gg.souppvp.feats.hooks.ranks.IRankHook;
import kami.gg.souppvp.juggernaut.JuggernautListener;
import kami.gg.souppvp.killstreak.KillstreaksHandler;
import kami.gg.souppvp.kit.progress.KitProgressManager;
import kami.gg.souppvp.kit.KitsHandler;
import kami.gg.souppvp.feats.leaderboard.LeaderboardManager;
import kami.gg.souppvp.listener.*;
import kami.gg.souppvp.map.MapManager;
import kami.gg.souppvp.feats.nametag.NametagManager;
import kami.gg.souppvp.feats.nametag.NametagListener;
import kami.gg.souppvp.perk.PerksHandler;
import kami.gg.souppvp.feats.scoreboard.ScoreboardAdapter;
import kami.gg.souppvp.feats.scoreboard.ScoreboardManager;
import kami.gg.souppvp.feats.storage.FlatFileHandler;
import kami.gg.souppvp.feats.storage.StorageType;
import kami.gg.souppvp.feats.tablist.TablistManager;
import kami.gg.souppvp.feats.tablist.TablistListener;
import kami.gg.souppvp.tasks.CanaPerkAndFiremanKitTask;
import kami.gg.souppvp.tasks.ClearDropsTask;
import kami.gg.souppvp.tasks.ClearTimerCacheTask;
import kami.gg.souppvp.tasks.SaveProfilesTask;
import kami.gg.souppvp.listener.SpawnTeleporatationListener;
import kami.gg.souppvp.tier.TiersListener;
import kami.gg.souppvp.timer.TimersHandler;
import kami.gg.souppvp.timer.TimersListener;
import kami.gg.souppvp.util.assemble.Assemble;
import kami.gg.souppvp.util.command.CommandManager;
import kami.gg.souppvp.util.menu.MenuListener;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

@Getter @Setter
public class SoupPvP extends JavaPlugin {

    @Getter public static final Gson GSON = new Gson();
    @Getter public static final Type LIST_STRING_TYPE = new TypeToken<List<String>>() {}.getType();
    @Getter @Setter public static Boolean isFreeKitsMode;
    @Getter public static SoupPvP instance;

    // Storage
    private StorageType storageType;
    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;
    private FlatFileHandler flatFileHandler;

    // Handlers
    private KitsHandler kitsHandler;
    private ProfilesHandler profilesHandler;
    private CombatTagsHandler combatTagsHandler;
    private SpawnTeleportationHandler spawnTeleportationHandler;
    private CoinFlipsHandler coinFlipsHandler;
    private SpawnHandler spawnHandler;
    private ClearDropsTask clearDropsTask;
    private SaveProfilesTask saveProfilesTask;
    private ClearTimerCacheTask clearTimerCacheTask;
    private CanaPerkAndFiremanKitTask canaPerkAndFiremanKitTask;
    private SumoHandler sumoHandler;
    private TNTTagHandler tntTagHandler;
    private NoFallDamageHandler noFallDamageHandler;
    private PerksHandler perksHandler;
    private KillstreaksHandler killstreaksHandler;
    private TimersHandler timersHandler;
    private TablistManager tablistManager;
    private ScoreboardManager scoreboardManager;
    private Assemble assemble;
    private NametagManager nametagManager;
    private IRankHook rankHook;
    private ClientHook clientHook;
    private PlaceholderHook placeholderHook;
    private MapManager mapManager;
    private LeaderboardManager leaderboardManager;
    private StaffManager staffManager;
    private KitProgressManager kitProgressManager;

    @Override
    public void onEnable() {
        instance = this;
        SoupPvP.getInstance().saveDefaultConfig();
        isFreeKitsMode = SoupPvP.getInstance().getConfig().getBoolean("FREE-KITS");
        for (World world : Bukkit.getWorlds()) {
            world.setGameRuleValue("doDaylightCycle", "false");
            world.setGameRuleValue("doMobSpawning", "false");
            world.setTime(6000L);
        }

        setupStorage();
        tablistManager = new TablistManager();
        nametagManager = new NametagManager();
        scoreboardManager = new ScoreboardManager(this);
        kitsHandler = new KitsHandler();
        profilesHandler = new ProfilesHandler();
        combatTagsHandler = new CombatTagsHandler();
        spawnTeleportationHandler = new SpawnTeleportationHandler();
        coinFlipsHandler = new CoinFlipsHandler();
        spawnHandler = new SpawnHandler();
        sumoHandler = new SumoHandler();
        tntTagHandler = new TNTTagHandler();
        noFallDamageHandler = new NoFallDamageHandler();
        perksHandler = new PerksHandler();
        killstreaksHandler = new KillstreaksHandler();
        timersHandler = new TimersHandler();
        clearDropsTask = new ClearDropsTask();
        saveProfilesTask = new SaveProfilesTask();
        clearTimerCacheTask = new ClearTimerCacheTask();
        canaPerkAndFiremanKitTask = new CanaPerkAndFiremanKitTask();
        mapManager = new MapManager();
        rankHook = new IRankHook();
        clientHook = new ClientHook();
        placeholderHook = new PlaceholderHook();
        leaderboardManager = new LeaderboardManager();
        staffManager = new StaffManager(this);
        kitProgressManager = new KitProgressManager(this);
        (new PacketBorderHandler()).start();

        setupAssemble();
        assemble.setTicks(2);

        leaderboardManager.updateAllLeaderboards();
        Bukkit.getScheduler().runTaskTimerAsynchronously(SoupPvP.getInstance(), leaderboardManager::updateAllLeaderboards, leaderboardManager.getUpdateInterval(), leaderboardManager.getUpdateInterval());

        registerListeners();
        new CommandManager(this);

        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (!(entity instanceof Player || entity instanceof Minecart || entity instanceof Wither || entity instanceof ItemFrame || entity instanceof EnderDragon)) {
                    entity.remove();
                }
            }
        }

        getLogger().info("Storage type: " + storageType.name());
    }

    @Override
    public void onDisable() {
        if (assemble != null) {
            assemble.cleanup();
        }

        if (storageType == StorageType.FLATFILE) {
            flatFileHandler.saveAllProfiles();
            getLogger().info("Saved all profiles to flat file.");
        } else {
            SoupPvP.getInstance().getProfilesHandler().saveProfiles();
        }

        tablistManager.cleanup();
        scoreboardManager.cleanup();
        assemble.cleanup();
        leaderboardManager.getCachedLeaderboards().clear();

        if (mongoClient != null) {
            mongoClient.close();
        }
    }

    public void setupAssemble() {
        if (assemble != null) {
            assemble.cleanup();
        }

        assemble = new Assemble(this, new ScoreboardAdapter());
    }

    private void setupStorage() {
        String storageTypeString = getConfig().getString("STORAGE.TYPE", "MONGODB").toUpperCase();

        try {
            storageType = StorageType.valueOf(storageTypeString);
        } catch (IllegalArgumentException e) {
            getLogger().warning("Invalid storage type '" + storageTypeString + "', defaulting to MONGODB");
            storageType = StorageType.MONGODB;
        }

        switch (storageType) {
            case FLATFILE:
                flatFileHandler = new FlatFileHandler(this);
                flatFileHandler.loadAllProfiles();
                getLogger().info("Flat file storage initialized.");
                break;

            case MONGODB:
                setupDatabase();
                getLogger().info("MongoDB storage initialized.");
                break;
        }
    }

    private void setupDatabase() {
        if (getConfig().getBoolean("MONGO.URI.ENABLED")) {
            MongoClientURI mongoClientURI = new MongoClientURI(getConfig().getString("MONGO.URI.CONNECTION"));
            mongoClient = new MongoClient(mongoClientURI);
            mongoDatabase = mongoClient.getDatabase(getConfig().getString("MONGO.URI.DATABASE"));
        } else {
            if (getConfig().getBoolean("MONGO.AUTHENTICATION.ENABLED")) {
                mongoClient = new MongoClient(new ServerAddress(getConfig().getString("MONGO.HOST"),
                        getConfig().getInt("MONGO.PORT")),
                        Collections.singletonList(MongoCredential.createCredential(
                                getConfig().getString("MONGO.AUTHENTICATION.USERNAME"),
                                getConfig().getString("MONGO.AUTHENTICATION.AUTHENTICATION-DATABASE"),
                                getConfig().getString("MONGO.AUTHENTICATION.PASSWORD").toCharArray())
                        ));
                mongoDatabase = mongoClient.getDatabase(getConfig().getString("MONGO.AUTHENTICATION.AUTHENTICATION-DATABASE"));
            } else {
                mongoClient = new MongoClient(new ServerAddress(getConfig().getString("MONGO.HOST"),
                        getConfig().getInt("MONGO.PORT")));
                mongoDatabase = mongoClient.getDatabase(getConfig().getString("MONGO.DATABASE"));
            }
        }
    }

    private void registerListeners() {
        SoupPvP.getInstance().getServer().getPluginManager().registerEvents(new WorldListener(), this);
        SoupPvP.getInstance().getServer().getPluginManager().registerEvents(new StaffListener(staffManager), this);
        SoupPvP.getInstance().getServer().getPluginManager().registerEvents(new NametagListener(), this);
        SoupPvP.getInstance().getServer().getPluginManager().registerEvents(new TablistListener(), this);
        SoupPvP.getInstance().getServer().getPluginManager().registerEvents(new MenuListener(), this);
        SoupPvP.getInstance().getServer().getPluginManager().registerEvents(new ChatListener(), this);
        SoupPvP.getInstance().getServer().getPluginManager().registerEvents(new GeneralListeners(), this);
        SoupPvP.getInstance().getServer().getPluginManager().registerEvents(new PlayerListeners(), this);
        SoupPvP.getInstance().getServer().getPluginManager().registerEvents(new SoupListeners(), this);
        SoupPvP.getInstance().getServer().getPluginManager().registerEvents(new SpawnEventItemsListener(), this);
        SoupPvP.getInstance().getServer().getPluginManager().registerEvents(new PvPListeners(), this);
        SoupPvP.getInstance().getServer().getPluginManager().registerEvents(new SpawnListeners(), this);
        SoupPvP.getInstance().getServer().getPluginManager().registerEvents(new SpawnTeleporatationListener(), this);
        SoupPvP.getInstance().getServer().getPluginManager().registerEvents(new ShopItemsListener(), this);
        SoupPvP.getInstance().getServer().getPluginManager().registerEvents(new TiersListener(), this);
        SoupPvP.getInstance().getServer().getPluginManager().registerEvents(new KillStreakAnnouncerListener(), this);
        SoupPvP.getInstance().getServer().getPluginManager().registerEvents(new BountyListener(), this);
        SoupPvP.getInstance().getServer().getPluginManager().registerEvents(new NoFallDamageListener(), this);
        SoupPvP.getInstance().getServer().getPluginManager().registerEvents(new CoinFlipListener(), this);
        SoupPvP.getInstance().getServer().getPluginManager().registerEvents(new WagerCustomEventListeners(), this);
        SoupPvP.getInstance().getServer().getPluginManager().registerEvents(new SumoListener(), this);
        SoupPvP.getInstance().getServer().getPluginManager().registerEvents(new TNTTagListener(), this);
        SoupPvP.getInstance().getServer().getPluginManager().registerEvents(new JuggernautListener(), this);
        SoupPvP.getInstance().getServer().getPluginManager().registerEvents(new StrengthAndInstantHarmNerfListener(), this);
        SoupPvP.getInstance().getServer().getPluginManager().registerEvents(new TimersListener(), this);
    }
}