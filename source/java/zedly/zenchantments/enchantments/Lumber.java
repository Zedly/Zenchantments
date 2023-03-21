package zedly.zenchantments.enchantments;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;

import java.util.List;

@AZenchantment(runInSlots = Slots.MAIN_HAND, conflicting = {})
public final class Lumber extends Zenchantment {
    private static final int     MAX_BLOCKS   = 200;
    @Override
    public boolean onBlockBreak(final @NotNull BlockBreakEvent event, final int level, final EquipmentSlot slot) {
        if (!event.getPlayer().isSneaking()) {
            return false;
        }

        final Block startBlock = event.getBlock();

        if (!MaterialList.TRUNKS.contains(startBlock.getType())) {
            return false;
        }

        final List<Block> blocks = Utilities.bfs(
            startBlock,
            MAX_BLOCKS,
            true,
            Float.MAX_VALUE,
            Utilities.DEFAULT_SEARCH_FACES,
            MaterialList.TRUNKS,
            MaterialList.LUMBER_WHITELIST,
            true,
            false
        );

        for (final Block block : blocks) {
            if (block.getType() == Material.MANGROVE_ROOTS) {
                if (((Waterlogged) block.getBlockData()).isWaterlogged()) {
                    continue;
                }
            }
            if (!WorldInteractionUtil.breakBlock(block, event.getPlayer())) {
                break;
            }
        }

        return !blocks.isEmpty();
    }
}
