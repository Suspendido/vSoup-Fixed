package kami.gg.souppvp.killstreak.menu.editor.button;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.killstreak.ConfigurableKillstreak;
import kami.gg.souppvp.killstreak.KillstreaksHandler;
import kami.gg.souppvp.killstreak.menu.editor.KillstreakEditMenu;
import kami.gg.souppvp.killstreak.menu.editor.KillstreakEditorMenu;
import kami.gg.souppvp.util.ItemBuilder;
import kami.gg.souppvp.util.menu.Button;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class SaveKillstreakButton extends Button {

    private final KillstreakEditMenu editMenu;
    private final ConfigurableKillstreak killstreak;

    public SaveKillstreakButton(KillstreakEditMenu editMenu, ConfigurableKillstreak killstreak) {
        this.editMenu = editMenu;
        this.killstreak = killstreak;
    }

    @Override
    public ItemStack getButtonItem(Player player) {
        return new ItemBuilder(Material.EMERALD)
                .name("&a&lSave Changes")
                .lore("&aClick to save")
                .build();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
        KillstreaksHandler handler = SoupPvP.getInstance().getKillstreaksHandler();
        handler.updateKillstreak(killstreak);
        sendMessage(player, "&aKillstreak saved successfully!");
        playSuccess(player);
        new KillstreakEditorMenu(player).open();
    }
}
