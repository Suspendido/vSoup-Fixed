package kami.gg.souppvp.kit;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.profile.ProfileState;
import kami.gg.souppvp.timer.Timer;
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
import java.util.UUID;

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
        player.sendMessage(CC.t("&aSuccessfully given you the kit &r" + getRarityType().getColor() + getName() + "&a."));
    }

    public boolean isInSpawn(Player player, Profile profile) {
        return profile.getProfileState() == ProfileState.SPAWN && SoupPvP.getInstance().getSpawnHandler().getCuboid().contains(player);
    }

    public boolean hasTimer(UUID uuid) {
        return SoupPvP.getInstance().getTimersHandler().hasTimer(uuid, getName() + " Charge", true);
    }

    public long getRemaining(UUID uuid) {
        return SoupPvP.getInstance().getTimersHandler().getRemaining(uuid, getName() + " Charge", true);
    }

    public void addTimer(UUID uuid, long cooldown) {
        SoupPvP.getInstance().getTimersHandler().addPlayerTimer(uuid, new Timer(getName() + " Charge", cooldown), true);
    }

}