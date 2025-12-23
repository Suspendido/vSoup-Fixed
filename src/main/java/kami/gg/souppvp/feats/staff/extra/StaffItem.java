package kami.gg.souppvp.feats.staff.extra;

import kami.gg.souppvp.feats.staff.StaffManager;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

@Getter
public class StaffItem {
    private final StaffManager manager;
    private final String name;
    private final StaffItemAction action;
    private final String replacement;
    private final String command;
    private final ItemStack item;
    private final int slot;

    public StaffItem(StaffManager manager, String name, StaffItemAction action, String replacement, String command, ItemStack item, int slot) {
        this.manager = manager;
        this.name = name;
        this.action = action;
        this.replacement = replacement;
        this.command = command;
        this.item = item;
        this.slot = slot;
    }
}
