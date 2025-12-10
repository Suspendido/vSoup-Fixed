package kami.gg.souppvp.util;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.kit.Kit;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.profile.ProfileState;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

public class PlayerUtil {

    /*
    *
    *   EXPLOSION_NORMAL( "explode" ),
        EXPLOSION_LARGE( "largeexplode" ),
        EXPLOSION_HUGE( "hugeexplosion" ),
        FIREWORKS_SPARK( "fireworksSpark" ),
        WATER_BUBBLE( "bubble" ),
        WATER_SPLASH( "splash" ),
        WATER_WAKE( "wake" ),
        SUSPENDED( "suspended" ),
        SUSPENDED_DEPTH( "depthsuspend" ),
        CRIT( "crit" ),
        CRIT_MAGIC( "magicCrit" ),
        SMOKE_NORMAL( "smoke" ),
        SMOKE_LARGE( "largesmoke" ),
        SPELL( "spell" ),
        SPELL_INSTANT( "instantSpell" ),
        SPELL_MOB( "mobSpell" ),
        SPELL_MOB_AMBIENT( "mobSpellAmbient" ),
        SPELL_WITCH( "witchMagic" ),
        DRIP_WATER( "dripWater" ),
        DRIP_LAVA( "dripLava" ),
        VILLAGER_ANGRY( "angryVillager" ),
        VILLAGER_HAPPY( "happyVillager" ),
        TOWN_AURA( "townaura" ),
        NOTE( "note" ),
        PORTAL( "portal" ),
        ENCHANTMENT_TABLE( "enchantmenttable" ),
        FLAME( "flame" ),
        LAVA( "lava" ),
        FOOTSTEP( "footstep" ),
        CLOUD( "cloud" ),
        REDSTONE( "reddust" ),
        SNOWBALL( "snowballpoof" ),
        SNOW_SHOVEL( "snowshovel" ),
        SLIME( "slime" ),
        HEART( "heart" ),
        BARRIER( "barrier" ),
        ICON_CRACK( "iconcrack", 2 ),
        BLOCK_CRACK( "blockcrack", 1 ),
        BLOCK_DUST( "blockdust", 1 ),
        WATER_DROP( "droplet" ),
        ITEM_TAKE( "take" ),
        MOB_APPEARANCE( "mobappearance" );
    *
    * */

    public static void giveSoup(Player player){
        for (ItemStack itemStack : player.getInventory().getContents()){
            if (itemStack == null){
                ItemStack soup = new ItemBuilder(Material.MUSHROOM_SOUP).build();
                player.getInventory().addItem(soup);
            }
        }
    }

    public static void resetPlayer(Player player) {
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());

        profile.setProfileState(ProfileState.SPAWN);

        player.setHealth(20);
        player.resetMaxHealth();
        player.setFoodLevel(20);
        player.setGameMode(GameMode.SURVIVAL);

        Location spawn = Bukkit.getWorlds().getFirst().getSpawnLocation();
        spawn.add(0.5, 0, 0.5);
        player.teleport(spawn);

        player.getInventory().clear();
        player.getInventory().setArmorContents(new ItemStack[4]);

        player.getInventory().setItem(0, SpawnItems.KITS_SELECTOR);
        player.getInventory().setItem(1, SpawnItems.HOST_EVENTS);
        player.getInventory().setItem(2, SpawnItems.GAME_PERKS);
        player.getInventory().setItem(4, SpawnItems.YOUR_STATISTICS);
        player.getInventory().setItem(7, SpawnItems.PREVIOUS_KIT);
        player.getInventory().setItem(8, SpawnItems.YOUR_OPTIONS);

        player.updateInventory();
        player.setLevel(0);
        player.setExp(0);
        player.setTotalExperience(0);
        player.setFireTicks(0);

        player.setMetadata("noFall", new FixedMetadataValue(SoupPvP.getInstance(), "noFall"));
        Kit current = SoupPvP.getInstance().getKitsHandler().getKitByName(profile.getCurrentKit());

        if (current == SoupPvP.getInstance().getKitsHandler().getKitByName("CopyCat")) {
            player.removeMetadata("CopyCat", SoupPvP.getInstance());
        }
        if (profile.isJuggernaut()) {
            profile.setJuggernaut(false);
        }

        for (PotionEffect potionEffect : player.getActivePotionEffects()) {
            player.removePotionEffect(potionEffect.getType());
        }

        SoupPvP.getInstance().getTimersHandler().removeAllPlayerTimers(player.getUniqueId());
        XPBarTimer.remove(player);
        SoupPvP.getInstance().getCombatTagsHandler().getCombatTags().remove(player.getUniqueId());
        SoupPvP.getInstance().getNoFallDamageHandler().getNoFallDamage().remove(player.getUniqueId());
        SoupPvP.getInstance().getSpawnTeleportationHandler().getSpawnTeleporataion().remove(player.getUniqueId());

    }

    public static void repairPlayer(Player player) {
        for (ItemStack items : player.getEquipment().getArmorContents()) {
            if (items != null){
                items.setDurability((short)0);
            }
        }
        for (ItemStack items : player.getInventory().getContents()) {
            if (items != null){
                items.setDurability((short) 0);
            }
        }
    }

    public static void playSound(Player player, Sound sound){
        player.playSound(player.getLocation(), sound, 1.0F, 1.0F);
    }

    public static List<Player> convertUUIDListToPlayerList(List<UUID> list) {
        return list.stream().map(Bukkit::getPlayer).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public static void denyMovement(Player player) {
        player.setMetadata("denyMovement", new FixedMetadataValue(SoupPvP.getInstance(), "denyMovement"));
    }

    public static void allowMovement(Player player) {
        if (player.hasMetadata("denyMovement")){
            player.removeMetadata("denyMovement", SoupPvP.getInstance());
        }
    }

}
