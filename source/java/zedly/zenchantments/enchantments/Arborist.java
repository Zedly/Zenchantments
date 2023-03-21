package zedly.zenchantments.enchantments;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;

import java.util.concurrent.ThreadLocalRandom;

import static org.bukkit.Material.*;
import static zedly.zenchantments.MaterialList.*;

@AZenchantment(runInSlots = Slots.MAIN_HAND , conflicting = {})
public final class Arborist extends Zenchantment {
    @Override
    public boolean onBlockBreak(final @NotNull BlockBreakEvent event, final int level, final EquipmentSlot slot) {
        final Block block = event.getBlock();
        final Material material = block.getType();

        if (!LEAVES.contains(material)) {
            return false;
        }

        // Crudely get the index in the array of materials.
        // TODO: Make this not awful.
        int index = LEAVES.indexOf(material);

        if (!(ThreadLocalRandom.current().nextInt(10) >= (9 - level) / (this.getPower() + 0.001))) {
            return false;
        }

        if (ThreadLocalRandom.current().nextInt(3) % 3 == 0) {
            event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(SAPLINGS.get(index), 1));
        }

        if (ThreadLocalRandom.current().nextInt(3) % 3 == 0) {
            event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(STICK, 1));
        }

        if (ThreadLocalRandom.current().nextInt(3) % 3 == 0) {
            event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(APPLE, 1));
        }

        if (ThreadLocalRandom.current().nextInt(65) == 25) {
            event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(GOLDEN_APPLE, 1));
        }

        return true;
    }
}
