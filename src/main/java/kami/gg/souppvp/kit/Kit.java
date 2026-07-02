package kami.gg.souppvp.kit;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.kit.ability.KitAbility;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.profile.ProfileState;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.PlayerUtil;
import kami.gg.souppvp.util.XPBarTimer;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public abstract class Kit implements Listener {

    protected String name;
    protected KitRarity rarityType;
    protected Integer price;
    protected ItemStack icon;
    protected List<String> description;
    protected List<ItemStack> combatEquipments;
    protected ItemStack[] armor;
    protected List<PotionEffect> potionEffects;
    protected KitAbility primaryAbility;
    protected KitAbility secondaryAbility;
    protected boolean enabled = true;

    public Kit() {
        this.description = new ArrayList<>();
        this.combatEquipments = new ArrayList<>();
        this.potionEffects = new ArrayList<>();
        this.armor = new ItemStack[4];
    }

    public abstract void onSelect(Player player);

    public void setup() {

    }

    private boolean shouldDisplayItem(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return true;
        List<String> lore = item.getItemMeta().getLore();
        if (lore == null) return true;
        for (String line : lore) {
            if (line != null && line.contains("Dont Display")) return false;
        }
        return true;
    }

    public void equipKit(Player player) {
        XPBarTimer.remove(player.getPlayer());
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        profile.setProfileState(ProfileState.COMBAT);

        player.getInventory().clear();
        player.getInventory().setArmorContents(this.getArmor());

        int i = 0;
        for (ItemStack item : this.getCombatEquipments()) {
            player.getInventory().setItem(i++, item);
        }

        if (primaryAbility != null) {
            primaryAbility.onKitSelect(player);
            ItemStack abilityItem = primaryAbility.getItem();

            if (shouldDisplayItem(abilityItem)) {
                player.getInventory().setItem(i++, abilityItem);
            }
        }

        if (secondaryAbility != null) {
            secondaryAbility.onKitSelect(player);
            ItemStack abilityItem = secondaryAbility.getItem();

            if (shouldDisplayItem(abilityItem)) {
                player.getInventory().setItem(i++, abilityItem);
            }
        }

        profile.restoreExtraItems(player);
        PlayerUtil.giveSoup(player);

        for (PotionEffect potionEffect : this.getPotionEffects()) {
            player.addPotionEffect(potionEffect);
        }

        onSelect(player);
        setup();
        SoupPvP.getInstance().getKitProgressManager().handleKitUse(profile);
        player.sendMessage(CC.t("&aSuccessfully given you the kit &r" + getRarityType().getColor() + getName() + "&a."));
    }

}