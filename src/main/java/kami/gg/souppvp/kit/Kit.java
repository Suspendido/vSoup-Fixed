package kami.gg.souppvp.kit;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.kit.ability.KitAbility;
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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

        if (primaryAbility != null) {
            primaryAbility.onKitSelect(player);
            ItemStack abilityItem = primaryAbility.getItem();
            if (abilityItem != null) {
                player.getInventory().addItem(abilityItem);
            }
        }

        if (secondaryAbility != null) {
            secondaryAbility.onKitSelect(player);
            ItemStack abilityItem = secondaryAbility.getItem();
            if (abilityItem != null) {
                player.getInventory().addItem(abilityItem);
            }
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