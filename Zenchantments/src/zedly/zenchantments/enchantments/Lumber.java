package zedly.zenchantments.enchantments;

import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import zedly.zenchantments.CustomEnchantment;
import zedly.zenchantments.Storage;
import zedly.zenchantments.enums.Hand;
import zedly.zenchantments.enums.Tool;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import static org.bukkit.Material.*;
import static zedly.zenchantments.enums.Tool.AXE;

public class Lumber extends CustomEnchantment {

    private static final int MAX_BLOCKS = 160;

    public static final  int        ID           = 34;

    @Override
    public Builder<Lumber> defaults() {
        return new Builder<>(Lumber::new, ID)
            .maxLevel(1)
            .loreName("Lumber")
            .probability(0)
            .enchantable(new Tool[]{AXE})
            .conflicting(new Class[]{})
            .description("Breaks the entire tree at once")
            .cooldown(0)
            .power(-1.0)
            .handUse(Hand.LEFT);
    }

    @Override
    public boolean onBlockBreak(BlockBreakEvent evt, int level, boolean usedHand) {
        if(!evt.getPlayer().isSneaking()) {
            return false;
        }
        Block startBlock = evt.getBlock();
        if(!Storage.COMPATIBILITY_ADAPTER.TRUNK_BLOCKS.contains(startBlock.getType())) {
            return false;
        }
        // BFS through the trunk, cancel if forbidden blocks are adjacent or search body becomes too large
        LinkedHashSet<Block> trunk = new LinkedHashSet<>();
        LinkedHashSet<Block> searchBody = new LinkedHashSet<>();
        List<Block> searchPerimeter = new ArrayList<>();
        searchBody.add(startBlock);
        searchPerimeter.add(startBlock);

        while(!searchPerimeter.isEmpty()) {
            Block searchBlock = searchPerimeter.remove(0);

            // If block is a trunk, add all adjacent blocks to search perimeter
            if (Storage.COMPATIBILITY_ADAPTER.TRUNK_BLOCKS.contains(searchBlock.getType())) {
                trunk.add(searchBlock);
                for (int y = -1; y <= 1; y++) {
                    for (int x = -1; x <= 1; x++) {
                        for (int z = -1; z <= 1; z++) {
                            Block testBlock = searchBlock.getRelative(x, y, z);
                            if (!searchBody.contains(testBlock)) {
                                if (Storage.COMPATIBILITY_ADAPTER.LUMBER_WHITELIST.contains(searchBlock.getType())) {
                                    searchPerimeter.add(testBlock);
                                    searchBody.add(testBlock);
                                } else {
                                    // Trunk is adjacent to a forbidden block. Nothing to do here
                                    return false;
                                }
                            }
                        }
                    }
                }

                // If block is not a trunk, add only any nearby trunk blocks to search perimeter
            } else {
                for (int y = -1; y <= 1; y++) {
                    for (int x = -1; x <= 1; x++) {
                        for (int z = -1; z <= 1; z++) {
                            Block testBlock = searchBlock.getRelative(x, y, z);
                            if (!searchBody.contains(testBlock) &&
                                Storage.COMPATIBILITY_ADAPTER.TRUNK_BLOCKS.contains(searchBlock.getType())) {
                                searchPerimeter.add(testBlock);
                                searchBody.add(testBlock);
                            } else if (!Storage.COMPATIBILITY_ADAPTER.LUMBER_WHITELIST.contains(testBlock.getType())) {
                                // Trunk is adjacent to a forbidden block. Nothing to do here
                                return false;
                            }
                        }
                    }
                }
            }
            if(trunk.size() > MAX_BLOCKS) {
                // Allowed trunk size exceeded
                return false;
            }
        }
        for(Block b : trunk) {
            ADAPTER.breakBlockNMS(b, evt.getPlayer());
        }
        return true;
    }
}
