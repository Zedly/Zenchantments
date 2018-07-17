package zedly.zenchantments.enchantments;

import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import zedly.zenchantments.CustomEnchantment;
import zedly.zenchantments.enums.Hand;
import zedly.zenchantments.enums.Tool;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import static org.bukkit.Material.*;
import static zedly.zenchantments.enums.Tool.AXE;

public class Lumber extends CustomEnchantment {

    private static final int MAX_BLOCKS = 160;

    private static final Material[] ACCEPTED_NEARBY_BLOCKS = {
            LOG, LOG_2, LEAVES, LEAVES_2, DIRT, GRASS, VINE, SNOW, COCOA, AIR, RED_ROSE, YELLOW_FLOWER, LONG_GRASS,
            GRAVEL, STONE, DOUBLE_PLANT, WATER, STATIONARY_WATER,
            SAND, SAPLING, BROWN_MUSHROOM, RED_MUSHROOM, MOSSY_COBBLESTONE, CLAY, HUGE_MUSHROOM_1, HUGE_MUSHROOM_2,
            SUGAR_CANE_BLOCK, MYCEL, TORCH
    };

    private static final Material[] TRUNK_BLOCKS = {
            LOG, LOG_2, HUGE_MUSHROOM_1, HUGE_MUSHROOM_2
    };

    public Lumber() {
        super(34);
        maxLevel = 1;
        loreName = "Lumber";
        probability = 0;
        enchantable = new Tool[]{AXE};
        conflicting = new Class[]{};
        description = "Breaks the entire tree at once";
        cooldown = 0;
        power = -1.0;
        handUse = Hand.LEFT;
    }

    @Override
    public boolean onBlockBreak(BlockBreakEvent evt, int level, boolean usedHand) {
        if(!evt.getPlayer().isSneaking()) {
            return false;
        }
        Block startBlock = evt.getBlock();
        if(!ArrayUtils.contains(TRUNK_BLOCKS, startBlock.getType())) {
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
            if(ArrayUtils.contains(TRUNK_BLOCKS, searchBlock.getType())) {
                trunk.add(searchBlock);
                for(int y = -1; y <= 1; y++) {
                    for(int x = -1; x <= 1; x++) {
                        for(int z = -1; z <= 1; z++) {
                            Block testBlock = searchBlock.getRelative(x, y, z);
                            if(!searchBody.contains(testBlock)) {
                                if(ArrayUtils.contains(ACCEPTED_NEARBY_BLOCKS, searchBlock.getType())) {
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
                for(int y = -1; y <= 1; y++) {
                    for(int x = -1; x <= 1; x++) {
                        for(int z = -1; z <= 1; z++) {
                            Block testBlock = searchBlock.getRelative(x, y, z);
                            if(!searchBody.contains(testBlock) &&
                               ArrayUtils.contains(TRUNK_BLOCKS, searchBlock.getType())) {
                                searchPerimeter.add(testBlock);
                                searchBody.add(testBlock);
                            } else if(!ArrayUtils.contains(ACCEPTED_NEARBY_BLOCKS, testBlock.getType())) {
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
