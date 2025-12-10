package kami.gg.souppvp.kit.kits;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.kit.Kit;
import kami.gg.souppvp.kit.KitRarity;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.profile.ProfileState;
import kami.gg.souppvp.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class ProKit extends Kit {

    @Override
    public String getName() {
        return "Pro";
    }

    @Override
    public KitRarity getRarityType() {
        return KitRarity.COMMON;
    }

    @Override
    public Integer getPrice() {
        return getRarityType().getPrice();
    }

    @Override
    public ItemStack getIcon() {
        return new ItemBuilder(Material.DIAMOND).build();
    }

    @Override
    public List<String> getDescription() {
        List<String> description = new ArrayList<>();
        description.add("&7Although this kit has no special abilities like the default");
        description.add("&7kit, upon every kill, you are guaranteed double credits.");
        return description;
    }

    @Override
    public List<ItemStack> getCombatEquipments() {
        List<ItemStack> itemStacks = new ArrayList<>();
        itemStacks.add(new ItemBuilder(Material.DIAMOND_SWORD).build());
        return itemStacks;
    }

    @Override
    public ItemStack[] getArmor() {
        return new ItemStack[]{
                new ItemBuilder(Material.IRON_BOOTS).build(),
                new ItemBuilder(Material.IRON_LEGGINGS).build(),
                new ItemBuilder(Material.IRON_CHESTPLATE).build(),
                new ItemBuilder(Material.IRON_HELMET).build()
        };
    }

    @Override
    public List<PotionEffect> getPotionEffects() {
        List<PotionEffect> potionEffects = new ArrayList<>();
        potionEffects.add(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0));
        return potionEffects;
    }

    @Override
    public void onSelect(Player player) {

    }

    @EventHandler
    public void onPlayerDeathEvent(PlayerDeathEvent event){
        if (event.getEntity() != null && event.getEntity().getKiller() != null){
            Player killer = (Player) event.getEntity().getKiller();
            Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(killer.getUniqueId());
            Kit kit = SoupPvP.getInstance().getKitsHandler().getKitByName("Pro");
            if (profile.isInEvent() || profile.getProfileState() == ProfileState.SPAWN) return;
            if (profile.getCurrentKit().equals(kit)){
                profile.setCredits(profile.getCredits() + 17);
            }
        }
    }

}
