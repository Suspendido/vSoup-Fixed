package kami.gg.souppvp.kit;

import kami.gg.souppvp.SoupPvP;
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

import java.util.List;

@Getter @Setter
public abstract class Kit implements Listener {

    public abstract String getName();
    public abstract KitRarity getRarityType();
    public abstract Integer getPrice();
    public abstract ItemStack getIcon();
    public abstract List<String> getDescription();
    public abstract List<ItemStack> getCombatEquipments();
    public abstract ItemStack[] getArmor();
    public abstract List<PotionEffect> getPotionEffects();

    public abstract void onSelect(Player player);

    public void setup() {

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

        PlayerUtil.giveSoup(player);

        for (PotionEffect potionEffect : this.getPotionEffects()) {
            player.addPotionEffect(potionEffect);
        }

        onSelect(player);
        setup();
        SoupPvP.getInstance().getKitProgressManager().handleKitUse(profile);
        player.sendMessage(CC.translate("&aSuccessfully given you the kit &r" + getRarityType().getColor() + getName() + "&a."));
    }

}