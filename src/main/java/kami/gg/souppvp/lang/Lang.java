package kami.gg.souppvp.lang;

import java.util.Map;

public class Lang {

    // General
    public static String NO_PERMISSION;
    public static String ONLY_PLAYERS;
    public static String PLAYER_NOT_FOUND;
    public static String INVALID_NUMBER;
    public static String INVALID_PROFILE;
    public static String SUCCESSFULLY_UPDATED;

    // Kits
    public static String KITS_NOT_IN_SPAWN;
    public static String KITS_NO_KIT_SELECTED;
    public static String KITS_KIT_EQUIPPED;
    public static String KITS_KIT_LOCKED;
    public static String KITS_KIT_COOLDOWN;

    // Spawn
    public static String SPAWN_TELEPORTED;
    public static String SPAWN_CANNOT_TELEPORT;

    // Combat
    public static String COMBAT_TAGGED;
    public static String COMBAT_NOT_TAGGED;

    // Coinflip
    public static String COINFLIP_CREATED;
    public static String COINFLIP_ACCEPTED;
    public static String COINFLIP_CANCELLED;
    public static String COINFLIP_WINNER;
    public static String COINFLIP_NO_ACTIVE_WAGERS;

    // Bounty
    public static String BOUNTY_PLACED;
    public static String BOUNTY_RECEIVED;
    public static String BOUNTY_CLAIMED;

    // Repair
    public static String REPAIR_REPAIRED;
    public static String REPAIR_NOT_ENOUGH_CREDITS;
    public static String REPAIR_NO_ITEMS;

    // Shop
    public static String SHOP_PURCHASED;
    public static String SHOP_NOT_ENOUGH_CREDITS;
    public static String SHOP_INVENTORY_FULL;

    // Perks
    public static String PERKS_EQUIPPED;
    public static String PERKS_UNEQUIPPED;
    public static String PERKS_NOT_UNLOCKED;

    // Sumo
    public static String SUMO_JOINED;
    public static String SUMO_LEFT;
    public static String SUMO_STARTED;
    public static String SUMO_ENDED;
    public static String SUMO_NO_ACTIVE_EVENT;

    // TNT Tag
    public static String TNTTAG_JOINED;
    public static String TNTTAG_LEFT;
    public static String TNTTAG_STARTED;
    public static String TNTTAG_TAGGED;
    public static String TNTTAG_WINNER;
    public static String TNTTAG_NO_ACTIVE_EVENT;

    // Staff
    public static String STAFF_VANISHED;
    public static String STAFF_UNVANISHED;
    public static String STAFF_FROZEN;
    public static String STAFF_UNFROZEN;
    public static String STAFF_MODE;
    public static String STAFF_MODE_OFF;

    // Statistics
    public static String STATISTICS_HEADER;
    public static String STATISTICS_KILLS;
    public static String STATISTICS_DEATHS;
    public static String STATISTICS_KD_RATIO;
    public static String STATISTICS_CREDITS;

    // Tier
    public static String TIER_CURRENT_TIER;
    public static String TIER_TIER_UP;
    public static String TIER_NOT_ENOUGH_XP;

    // Options
    public static String OPTIONS_OPENED;
    public static String OPTIONS_SETTING_CHANGED;

    // Killstreak
    public static String KILLSTREAK_KILLSTREAK;
    public static String KILLSTREAK_REWARD;

    // Map
    public static String MAP_VOTED;
    public static String MAP_CURRENT_MAP;
    public static String MAP_CHANGED;

    // Juggernaut
    public static String JUGGERNAUT_ACTIVATED;
    public static String JUGGERNAUT_DEACTIVATED;
    public static String JUGGERNAUT_NOT_AVAILABLE;

    // Market
    public static String MARKET_LISTED;
    public static String MARKET_PURCHASED;
    public static String MARKET_DELISTED;
    public static String MARKET_NO_LISTINGS;

    public static void load(LangManager manager) {
        // General
        NO_PERMISSION = manager.getMessage("GENERAL.NO_PERMISSION");
        ONLY_PLAYERS = manager.getMessage("GENERAL.ONLY_PLAYERS");
        PLAYER_NOT_FOUND = manager.getMessage("GENERAL.PLAYER_NOT_FOUND");
        INVALID_NUMBER = manager.getMessage("GENERAL.INVALID_NUMBER");
        INVALID_PROFILE = manager.getMessage("GENERAL.INVALID_PROFILE");
        SUCCESSFULLY_UPDATED = manager.getMessage("GENERAL.SUCCESSFULLY_UPDATED");

        // Kits
        KITS_NOT_IN_SPAWN = manager.getMessage("KITS.NOT_IN_SPAWN");
        KITS_NO_KIT_SELECTED = manager.getMessage("KITS.NO_KIT_SELECTED");
        KITS_KIT_EQUIPPED = manager.getMessage("KITS.KIT_EQUIPPED");
        KITS_KIT_LOCKED = manager.getMessage("KITS.KIT_LOCKED");
        KITS_KIT_COOLDOWN = manager.getMessage("KITS.KIT_COOLDOWN");

        // Spawn
        SPAWN_TELEPORTED = manager.getMessage("SPAWN.TELEPORTED");
        SPAWN_CANNOT_TELEPORT = manager.getMessage("SPAWN.CANNOT_TELEPORT");

        // Combat
        COMBAT_TAGGED = manager.getMessage("COMBAT.TAGGED");
        COMBAT_NOT_TAGGED = manager.getMessage("COMBAT.NOT_TAGGED");

        // Coinflip
        COINFLIP_CREATED = manager.getMessage("COINFLIP.CREATED");
        COINFLIP_ACCEPTED = manager.getMessage("COINFLIP.ACCEPTED");
        COINFLIP_CANCELLED = manager.getMessage("COINFLIP.CANCELLED");
        COINFLIP_WINNER = manager.getMessage("COINFLIP.WINNER");
        COINFLIP_NO_ACTIVE_WAGERS = manager.getMessage("COINFLIP.NO_ACTIVE_WAGERS");

        // Bounty
        BOUNTY_PLACED = manager.getMessage("BOUNTY.PLACED");
        BOUNTY_RECEIVED = manager.getMessage("BOUNTY.RECEIVED");
        BOUNTY_CLAIMED = manager.getMessage("BOUNTY.CLAIMED");

        // Repair
        REPAIR_REPAIRED = manager.getMessage("REPAIR.REPAIRED");
        REPAIR_NOT_ENOUGH_CREDITS = manager.getMessage("REPAIR.NOT_ENOUGH_CREDITS");
        REPAIR_NO_ITEMS = manager.getMessage("REPAIR.NO_ITEMS");

        // Shop
        SHOP_PURCHASED = manager.getMessage("SHOP.PURCHASED");
        SHOP_NOT_ENOUGH_CREDITS = manager.getMessage("SHOP.NOT_ENOUGH_CREDITS");
        SHOP_INVENTORY_FULL = manager.getMessage("SHOP.INVENTORY_FULL");

        // Perks
        PERKS_EQUIPPED = manager.getMessage("PERKS.EQUIPPED");
        PERKS_UNEQUIPPED = manager.getMessage("PERKS.UNEQUIPPED");
        PERKS_NOT_UNLOCKED = manager.getMessage("PERKS.NOT_UNLOCKED");

        // Sumo
        SUMO_JOINED = manager.getMessage("SUMO.JOINED");
        SUMO_LEFT = manager.getMessage("SUMO.LEFT");
        SUMO_STARTED = manager.getMessage("SUMO.STARTED");
        SUMO_ENDED = manager.getMessage("SUMO.ENDED");
        SUMO_NO_ACTIVE_EVENT = manager.getMessage("SUMO.NO_ACTIVE_EVENT");

        // TNT Tag
        TNTTAG_JOINED = manager.getMessage("TNTTAG.JOINED");
        TNTTAG_LEFT = manager.getMessage("TNTTAG.LEFT");
        TNTTAG_STARTED = manager.getMessage("TNTTAG.STARTED");
        TNTTAG_TAGGED = manager.getMessage("TNTTAG.TAGGED");
        TNTTAG_WINNER = manager.getMessage("TNTTAG.WINNER");
        TNTTAG_NO_ACTIVE_EVENT = manager.getMessage("TNTTAG.NO_ACTIVE_EVENT");

        // Staff
        STAFF_VANISHED = manager.getMessage("STAFF.VANISHED");
        STAFF_UNVANISHED = manager.getMessage("STAFF.UNVANISHED");
        STAFF_FROZEN = manager.getMessage("STAFF.FROZEN");
        STAFF_UNFROZEN = manager.getMessage("STAFF.UNFROZEN");
        STAFF_MODE = manager.getMessage("STAFF.STAFF_MODE");
        STAFF_MODE_OFF = manager.getMessage("STAFF.STAFF_MODE_OFF");

        // Statistics
        STATISTICS_HEADER = manager.getMessage("STATISTICS.HEADER");
        STATISTICS_KILLS = manager.getMessage("STATISTICS.KILLS");
        STATISTICS_DEATHS = manager.getMessage("STATISTICS.DEATHS");
        STATISTICS_KD_RATIO = manager.getMessage("STATISTICS.KD_RATIO");
        STATISTICS_CREDITS = manager.getMessage("STATISTICS.CREDITS");

        // Tier
        TIER_CURRENT_TIER = manager.getMessage("TIER.CURRENT_TIER");
        TIER_TIER_UP = manager.getMessage("TIER.TIER_UP");
        TIER_NOT_ENOUGH_XP = manager.getMessage("TIER.NOT_ENOUGH_XP");

        // Options
        OPTIONS_OPENED = manager.getMessage("OPTIONS.OPENED");
        OPTIONS_SETTING_CHANGED = manager.getMessage("OPTIONS.SETTING_CHANGED");

        // Killstreak
        KILLSTREAK_KILLSTREAK = manager.getMessage("KILLSTREAK.KILLSTREAK");
        KILLSTREAK_REWARD = manager.getMessage("KILLSTREAK.REWARD");

        // Map
        MAP_VOTED = manager.getMessage("MAP.VOTED");
        MAP_CURRENT_MAP = manager.getMessage("MAP.CURRENT_MAP");
        MAP_CHANGED = manager.getMessage("MAP.CHANGED");

        // Juggernaut
        JUGGERNAUT_ACTIVATED = manager.getMessage("JUGGERNAUT.ACTIVATED");
        JUGGERNAUT_DEACTIVATED = manager.getMessage("JUGGERNAUT.DEACTIVATED");
        JUGGERNAUT_NOT_AVAILABLE = manager.getMessage("JUGGERNAUT.NOT_AVAILABLE");

        // Market
        MARKET_LISTED = manager.getMessage("MARKET.LISTED");
        MARKET_PURCHASED = manager.getMessage("MARKET.PURCHASED");
        MARKET_DELISTED = manager.getMessage("MARKET.DELISTED");
        MARKET_NO_LISTINGS = manager.getMessage("MARKET.NO_LISTINGS");
    }

    // Métodos para manejar placeholders
    public static String format(String message, Map<String, String> placeholders) {
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            message = message.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return message;
    }

    public static String format(String message, String... placeholders) {
        for (int i = 0; i < placeholders.length; i += 2) {
            if (i + 1 < placeholders.length) {
                message = message.replace("{" + placeholders[i] + "}", placeholders[i + 1]);
            }
        }
        return message;
    }
}