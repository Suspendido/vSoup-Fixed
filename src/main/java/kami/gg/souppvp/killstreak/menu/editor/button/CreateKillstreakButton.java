package kami.gg.souppvp.killstreak.menu.editor.button;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.killstreak.ConfigurableKillstreak;
import kami.gg.souppvp.killstreak.KillstreaksHandler;
import kami.gg.souppvp.killstreak.menu.editor.KillstreakCreateMenu;
import kami.gg.souppvp.killstreak.menu.editor.KillstreakEditorMenu;
import kami.gg.souppvp.util.ItemBuilder;
import kami.gg.souppvp.util.menu.Button;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class CreateKillstreakButton extends Button {

    private final KillstreakCreateMenu createMenu;

    public CreateKillstreakButton(KillstreakCreateMenu createMenu) {
        this.createMenu = createMenu;
    }

    @Override
    public ItemStack getButtonItem(Player player) {
        return new ItemBuilder(Material.EMERALD)
                .name("&a&lCreate Killstreak")
                .lore("&aClick to create")
                .build();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
        if (createMenu.getName().isEmpty()) {
            sendMessage(player, "&cPlease set a name first.");
            playFail(player);
            return;
        }

        ConfigurableKillstreak killstreak = new ConfigurableKillstreak(
                0,
                createMenu.getName(),
                createMenu.getRequiredKills(),
                createMenu.getRewardType(),
                createMenu.getRewardData(),
                createMenu.getIconMaterial(),
                createMenu.getLore()
        );

        KillstreaksHandler handler = SoupPvP.getInstance().getKillstreaksHandler();
        handler.addKillstreak(killstreak);

        sendMessage(player, "&aKillstreak created successfully!");
        playSuccess(player);
        new KillstreakEditorMenu(player).open();
    }
}
