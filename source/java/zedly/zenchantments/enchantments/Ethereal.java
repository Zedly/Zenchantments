package zedly.zenchantments.enchantments;

import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;

@AZenchantment(runInSlots = Slots.ALL, conflicting = {})
public final class Ethereal extends Zenchantment {
    @Override
    public boolean onScan(final @NotNull Player player, final int level, final EquipmentSlot slot) {
        ItemStack stack = player.getInventory().getItem(slot);
        final int durability = Utilities.getItemStackDamage(stack);
        Utilities.setItemStackDamage(stack, 0);
        if (durability != 0) {
            player.getInventory().setItem(slot, stack);
        }
        return true;
    }
}
