package kami.gg.souppvp.feats.staff;

import lombok.Getter;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Staff {

    private final Player player;
    private final ItemStack[] contents;
    private final ItemStack[] armorContents;
    private final GameMode gameMode;
    private final List<PotionEffect> effects;

    public Staff(Player player, GameMode gameMode) {
        this.player = player;
        this.contents = player.getInventory().getContents();
        this.armorContents = player.getInventory().getArmorContents();
        this.gameMode = gameMode;
        this.effects = new ArrayList<>();
    }
}
