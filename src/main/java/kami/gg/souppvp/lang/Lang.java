package kami.gg.souppvp.lang;

import kami.gg.souppvp.util.CC;

import java.util.List;
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

    public static List<String> TREASURE_CHEST_HOLOGRAM;
    public static List<String> TREASURE_CHEST_BUTTON_LORE;
    public static List<String> TREASURE_CHEST_BROADCAST;
    public static String TREASURE_CHEST_BUTTON_ADMIN;
    public static String TREASURE_CHEST_BUTTON_NO_CHESTS;
    public static String TREASURE_CHEST_BUTTON_HAS_CHESTS;
    public static String TREASURE_CHEST_HEADER;
    public static String TREASURE_CHEST_TITLE;
    public static String TREASURE_CHEST_OPENED_BY;
    public static String TREASURE_CHEST_REWARDS_HEADER;
    public static String TREASURE_CHEST_FOOTER;
    public static String TREASURE_CHEST_UNLOCKED_AT;
    public static String TREASURE_CHEST_ALREADY_USING;
    public static String TREASURE_CHEST_ALREADY_OPENED;
    public static String TREASURE_CHEST_ALL_OPENED;
    public static String TREASURE_CHEST_REWARD_HOLOGRAM;
    public static String TREASURE_CHEST_IN_USE;
    public static String TREASURE_CHEST_NO_CHESTS;
    public static String TREASURE_CHEST_ALREADY_OPENING;
    public static String TREASURE_CHEST_CHESTS_APPEARED;
    public static String TREASURE_CHEST_NO_REWARDS;
    public static String TREASURE_CHEST_WON_BROADCAST;
    public static String TREASURE_CHEST_CMD_END_CURRENT;
    public static String TREASURE_CHEST_CMD_RELOAD;
    public static String TREASURE_CHEST_CMD_NOT_FOUND;
    public static List<String> TREASURE_CHEST_CMD_GIVE_TARGET;
    public static String TREASURE_CHEST_CMD_GIVE_SENDER;
    public static String TREASURE_CHEST_CMD_TAKE_TARGET;
    public static String TREASURE_CHEST_CMD_TAKE_SENDER;
    public static String TREASURE_CHEST_CMD_SHOW;
    public static String TREASURE_CHEST_CMD_INVALID_MATERIAL;
    public static String TREASURE_CHEST_CMD_INVALID_SLOT;
    public static String TREASURE_CHEST_CMD_ALREADY_EXISTS;
    public static String TREASURE_CHEST_CMD_CREATED;
    public static String TREASURE_CHEST_CMD_DELETED;
    public static String TREASURE_CHEST_CMD_SETUP_CENTRAL;
    public static String TREASURE_CHEST_CMD_NEEDS_CENTRAL;
    public static String TREASURE_CHEST_CMD_INVALID_COUNT;
    public static String TREASURE_CHEST_CMD_MAX_CHESTS;
    public static String TREASURE_CHEST_CMD_INVALID_RADIUS;
    public static String TREASURE_CHEST_CMD_SKIPPED_OCCUPIED;
    public static String TREASURE_CHEST_CMD_SKIPPED_ADJACENT;
    public static String TREASURE_CHEST_CMD_GENERATED;

    public static void load(LangManager manager) {
        // General
        NO_PERMISSION = manager.getString("GENERAL.NO_PERMISSION");
        ONLY_PLAYERS = manager.getString("GENERAL.ONLY_PLAYERS");
        PLAYER_NOT_FOUND = manager.getString("GENERAL.PLAYER_NOT_FOUND");
        INVALID_NUMBER = manager.getString("GENERAL.INVALID_NUMBER");
        INVALID_PROFILE = manager.getString("GENERAL.INVALID_PROFILE");
        SUCCESSFULLY_UPDATED = manager.getString("GENERAL.SUCCESSFULLY_UPDATED");

        // Kits
        KITS_NOT_IN_SPAWN = manager.getString("KITS.NOT_IN_SPAWN");
        KITS_NO_KIT_SELECTED = manager.getString("KITS.NO_KIT_SELECTED");
        KITS_KIT_EQUIPPED = manager.getString("KITS.KIT_EQUIPPED");
        KITS_KIT_LOCKED = manager.getString("KITS.KIT_LOCKED");
        KITS_KIT_COOLDOWN = manager.getString("KITS.KIT_COOLDOWN");

        // Spawn
        SPAWN_TELEPORTED = manager.getString("SPAWN.TELEPORTED");
        SPAWN_CANNOT_TELEPORT = manager.getString("SPAWN.CANNOT_TELEPORT");

        // Combat
        COMBAT_TAGGED = manager.getString("COMBAT.TAGGED");
        COMBAT_NOT_TAGGED = manager.getString("COMBAT.NOT_TAGGED");

        // Coinflip
        COINFLIP_CREATED = manager.getString("COINFLIP.CREATED");
        COINFLIP_ACCEPTED = manager.getString("COINFLIP.ACCEPTED");
        COINFLIP_CANCELLED = manager.getString("COINFLIP.CANCELLED");
        COINFLIP_WINNER = manager.getString("COINFLIP.WINNER");
        COINFLIP_NO_ACTIVE_WAGERS = manager.getString("COINFLIP.NO_ACTIVE_WAGERS");

        // Bounty
        BOUNTY_PLACED = manager.getString("BOUNTY.PLACED");
        BOUNTY_RECEIVED = manager.getString("BOUNTY.RECEIVED");
        BOUNTY_CLAIMED = manager.getString("BOUNTY.CLAIMED");

        // Repair
        REPAIR_REPAIRED = manager.getString("REPAIR.REPAIRED");
        REPAIR_NOT_ENOUGH_CREDITS = manager.getString("REPAIR.NOT_ENOUGH_CREDITS");
        REPAIR_NO_ITEMS = manager.getString("REPAIR.NO_ITEMS");

        // Shop
        SHOP_PURCHASED = manager.getString("SHOP.PURCHASED");
        SHOP_NOT_ENOUGH_CREDITS = manager.getString("SHOP.NOT_ENOUGH_CREDITS");
        SHOP_INVENTORY_FULL = manager.getString("SHOP.INVENTORY_FULL");

        // Perks
        PERKS_EQUIPPED = manager.getString("PERKS.EQUIPPED");
        PERKS_UNEQUIPPED = manager.getString("PERKS.UNEQUIPPED");
        PERKS_NOT_UNLOCKED = manager.getString("PERKS.NOT_UNLOCKED");

        // Sumo
        SUMO_JOINED = manager.getString("SUMO.JOINED");
        SUMO_LEFT = manager.getString("SUMO.LEFT");
        SUMO_STARTED = manager.getString("SUMO.STARTED");
        SUMO_ENDED = manager.getString("SUMO.ENDED");
        SUMO_NO_ACTIVE_EVENT = manager.getString("SUMO.NO_ACTIVE_EVENT");

        // TNT Tag
        TNTTAG_JOINED = manager.getString("TNTTAG.JOINED");
        TNTTAG_LEFT = manager.getString("TNTTAG.LEFT");
        TNTTAG_STARTED = manager.getString("TNTTAG.STARTED");
        TNTTAG_TAGGED = manager.getString("TNTTAG.TAGGED");
        TNTTAG_WINNER = manager.getString("TNTTAG.WINNER");
        TNTTAG_NO_ACTIVE_EVENT = manager.getString("TNTTAG.NO_ACTIVE_EVENT");

        // Staff
        STAFF_VANISHED = manager.getString("STAFF.VANISHED");
        STAFF_UNVANISHED = manager.getString("STAFF.UNVANISHED");
        STAFF_FROZEN = manager.getString("STAFF.FROZEN");
        STAFF_UNFROZEN = manager.getString("STAFF.UNFROZEN");
        STAFF_MODE = manager.getString("STAFF.STAFF_MODE");
        STAFF_MODE_OFF = manager.getString("STAFF.STAFF_MODE_OFF");

        // Statistics
        STATISTICS_HEADER = manager.getString("STATISTICS.HEADER");
        STATISTICS_KILLS = manager.getString("STATISTICS.KILLS");
        STATISTICS_DEATHS = manager.getString("STATISTICS.DEATHS");
        STATISTICS_KD_RATIO = manager.getString("STATISTICS.KD_RATIO");
        STATISTICS_CREDITS = manager.getString("STATISTICS.CREDITS");

        // Tier
        TIER_CURRENT_TIER = manager.getString("TIER.CURRENT_TIER");
        TIER_TIER_UP = manager.getString("TIER.TIER_UP");
        TIER_NOT_ENOUGH_XP = manager.getString("TIER.NOT_ENOUGH_XP");

        // Options
        OPTIONS_OPENED = manager.getString("OPTIONS.OPENED");
        OPTIONS_SETTING_CHANGED = manager.getString("OPTIONS.SETTING_CHANGED");

        // Killstreak
        KILLSTREAK_KILLSTREAK = manager.getString("KILLSTREAK.KILLSTREAK");
        KILLSTREAK_REWARD = manager.getString("KILLSTREAK.REWARD");

        // Map
        MAP_VOTED = manager.getString("MAP.VOTED");
        MAP_CURRENT_MAP = manager.getString("MAP.CURRENT_MAP");
        MAP_CHANGED = manager.getString("MAP.CHANGED");

        // Juggernaut
        JUGGERNAUT_ACTIVATED = manager.getString("JUGGERNAUT.ACTIVATED");
        JUGGERNAUT_DEACTIVATED = manager.getString("JUGGERNAUT.DEACTIVATED");
        JUGGERNAUT_NOT_AVAILABLE = manager.getString("JUGGERNAUT.NOT_AVAILABLE");

        // Market
        MARKET_LISTED = manager.getString("MARKET.LISTED");
        MARKET_PURCHASED = manager.getString("MARKET.PURCHASED");
        MARKET_DELISTED = manager.getString("MARKET.DELISTED");
        MARKET_NO_LISTINGS = manager.getString("MARKET.NO_LISTINGS");

        TREASURE_CHEST_HOLOGRAM = manager.getStringList("TREASURE_CHEST.HOLOGRAM");
        TREASURE_CHEST_BROADCAST = manager.getStringList("TREASURE_CHEST.BROADCAST");
        TREASURE_CHEST_BUTTON_LORE = manager.getStringList("TREASURE_CHEST.BUTTON.LORE");
        TREASURE_CHEST_CMD_GIVE_TARGET = manager.getStringList("TREASURE_CHEST.CMD.GIVE_TARGET");

        TREASURE_CHEST_BUTTON_ADMIN = manager.getString("TREASURE_CHEST.BUTTON.LORE_ADMIN");
        TREASURE_CHEST_BUTTON_NO_CHESTS = manager.getString("TREASURE_CHEST.BUTTON.LORE_NO_CHESTS");
        TREASURE_CHEST_BUTTON_HAS_CHESTS = manager.getString("TREASURE_CHEST.BUTTON.LORE_HAS_CHESTS");
        TREASURE_CHEST_HEADER = manager.getString("TREASURE_CHEST.HEADER");
        TREASURE_CHEST_TITLE = manager.getString("TREASURE_CHEST.TITLE");
        TREASURE_CHEST_OPENED_BY = manager.getString("TREASURE_CHEST.OPENED_BY");
        TREASURE_CHEST_REWARDS_HEADER = manager.getString("TREASURE_CHEST.REWARDS_HEADER");
        TREASURE_CHEST_FOOTER = manager.getString("TREASURE_CHEST.FOOTER");
        TREASURE_CHEST_UNLOCKED_AT = manager.getString("TREASURE_CHEST.UNLOCKED_AT");
        TREASURE_CHEST_ALREADY_USING = manager.getString("TREASURE_CHEST.ALREADY_USING");
        TREASURE_CHEST_ALREADY_OPENED = manager.getString("TREASURE_CHEST.ALREADY_OPENED");
        TREASURE_CHEST_ALL_OPENED = manager.getString("TREASURE_CHEST.ALL_OPENED");
        TREASURE_CHEST_REWARD_HOLOGRAM = manager.getString("TREASURE_CHEST.REWARD_HOLOGRAM");
        TREASURE_CHEST_IN_USE = manager.getString("TREASURE_CHEST.IN_USE");
        TREASURE_CHEST_NO_CHESTS = manager.getString("TREASURE_CHEST.NO_CHESTS");
        TREASURE_CHEST_ALREADY_OPENING = manager.getString("TREASURE_CHEST.ALREADY_OPENING");
        TREASURE_CHEST_CHESTS_APPEARED = manager.getString("TREASURE_CHEST.CHESTS_APPEARED");
        TREASURE_CHEST_WON_BROADCAST = manager.getString("TREASURE_CHEST.WON_BROADCAST");
        TREASURE_CHEST_NO_REWARDS = manager.getString("TREASURE_CHEST.NO_REWARDS");
        TREASURE_CHEST_CMD_END_CURRENT = manager.getString("TREASURE_CHEST.CMD.END_CURRENT");
        TREASURE_CHEST_CMD_RELOAD = manager.getString("TREASURE_CHEST.CMD.RELOAD");
        TREASURE_CHEST_CMD_NOT_FOUND = manager.getString("TREASURE_CHEST.CMD.NOT_FOUND");
        TREASURE_CHEST_CMD_GIVE_SENDER = manager.getString("TREASURE_CHEST.CMD.GIVE_SENDER");
        TREASURE_CHEST_CMD_TAKE_TARGET = manager.getString("TREASURE_CHEST.CMD.TAKE_TARGET");
        TREASURE_CHEST_CMD_TAKE_SENDER = manager.getString("TREASURE_CHEST.CMD.TAKE_SENDER");
        TREASURE_CHEST_CMD_SHOW = manager.getString("TREASURE_CHEST.CMD.SHOW");
        TREASURE_CHEST_CMD_INVALID_MATERIAL = manager.getString("TREASURE_CHEST.CMD.INVALID_MATERIAL");
        TREASURE_CHEST_CMD_INVALID_SLOT = manager.getString("TREASURE_CHEST.CMD.INVALID_SLOT");
        TREASURE_CHEST_CMD_ALREADY_EXISTS = manager.getString("TREASURE_CHEST.CMD.ALREADY_EXISTS");
        TREASURE_CHEST_CMD_CREATED = manager.getString("TREASURE_CHEST.CMD.CREATED");
        TREASURE_CHEST_CMD_DELETED = manager.getString("TREASURE_CHEST.CMD.DELETED");
        TREASURE_CHEST_CMD_SETUP_CENTRAL = manager.getString("TREASURE_CHEST.CMD.SETUP_CENTRAL");
        TREASURE_CHEST_CMD_NEEDS_CENTRAL = manager.getString("TREASURE_CHEST.CMD.NEEDS_CENTRAL");
        TREASURE_CHEST_CMD_INVALID_COUNT = manager.getString("TREASURE_CHEST.CMD.INVALID_COUNT");
        TREASURE_CHEST_CMD_MAX_CHESTS = manager.getString("TREASURE_CHEST.CMD.MAX_CHESTS");
        TREASURE_CHEST_CMD_INVALID_RADIUS = manager.getString("TREASURE_CHEST.CMD.INVALID_RADIUS");
        TREASURE_CHEST_CMD_SKIPPED_OCCUPIED = manager.getString("TREASURE_CHEST.CMD.SKIPPED_OCCUPIED");
        TREASURE_CHEST_CMD_SKIPPED_ADJACENT = manager.getString("TREASURE_CHEST.CMD.SKIPPED_ADJACENT");
        TREASURE_CHEST_CMD_GENERATED = manager.getString("TREASURE_CHEST.CMD.GENERATED");
    }

    // Métodos para manejar placeholders
    public static String format(String message, Map<String, String> placeholders) {
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            message = message.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return CC.t(message);
    }

    public static String format(String message, String... placeholders) {
        for (int i = 0; i < placeholders.length; i += 2) {
            if (i + 1 < placeholders.length) {
                message = message.replace("{" + placeholders[i] + "}", placeholders[i + 1]);
            }
        }
        return CC.t(message);
    }
}