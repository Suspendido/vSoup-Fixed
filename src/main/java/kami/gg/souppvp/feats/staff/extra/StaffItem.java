package kami.gg.souppvp.feats.staff.extra;

import kami.gg.souppvp.feats.staff.StaffManager;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

//@Getter
public record StaffItem(StaffManager manager, String name, StaffItemAction action, String replacement, String command, ItemStack item, int slot) {
}
