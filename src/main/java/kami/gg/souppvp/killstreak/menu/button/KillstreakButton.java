package kami.gg.souppvp.killstreak.menu.button;

import kami.gg.souppvp.killstreak.ConfigurableKillstreak;
import kami.gg.souppvp.util.menu.Button;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class KillstreakButton extends Button {

    private final ConfigurableKillstreak killstreak;

    public KillstreakButton(ConfigurableKillstreak killstreak) {
        this.killstreak = killstreak;
    }

    @Override
    public ItemStack getButtonItem(Player player) {
        return killstreak.getIcon();
    }
}
