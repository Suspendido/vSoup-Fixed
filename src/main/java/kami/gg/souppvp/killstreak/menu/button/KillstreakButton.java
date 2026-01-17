package kami.gg.souppvp.killstreak.menu.button;

import kami.gg.souppvp.killstreak.Killstreak;
import kami.gg.souppvp.util.menu.Button;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class KillstreakButton extends Button {

    private final Killstreak killstreak;

    public KillstreakButton(Killstreak killstreak) {
        this.killstreak = killstreak;
    }

    @Override
    public ItemStack getButtonItem(Player player) {
        return killstreak.getIcon();
    }
}
