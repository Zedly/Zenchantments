package zedly.zenchantments.enchantments;

import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;

import java.util.concurrent.ThreadLocalRandom;

import static org.bukkit.Material.*;

@AZenchantment(runInSlots = Slots.MAIN_HAND, conflicting = {})
public final class GoldRush extends Zenchantment {
    @Override
    public boolean onBlockBreak(final @NotNull BlockBreakEvent event, final int level, final EquipmentSlot slot) {
        final Block block = event.getBlock();

        if ((block.getType() == SAND || block.getType() == RED_SAND) && ThreadLocalRandom.current().nextInt(100) >= (100 - (level * this.getPower() * 3))) {
            block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(GOLD_NUGGET));
            return true;
        }

        return false;
    }
}
