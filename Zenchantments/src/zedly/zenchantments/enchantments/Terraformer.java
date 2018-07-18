package zedly.zenchantments.enchantments;

import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.player.PlayerInteractEvent;
import zedly.zenchantments.CustomEnchantment;
import zedly.zenchantments.Storage;
import zedly.zenchantments.Utilities;
import zedly.zenchantments.compatibility.CompatibilityAdapter;
import zedly.zenchantments.enums.Hand;
import zedly.zenchantments.enums.Tool;

import java.util.LinkedList;
import java.util.List;

import static org.bukkit.Material.*;
import static org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK;
import static zedly.zenchantments.enums.Tool.SHOVEL;

public class Terraformer extends CustomEnchantment {

    private static final BlockFace[] SEARCH_FACES = {
            BlockFace.NORTH,
            BlockFace.SOUTH,
            BlockFace.EAST,
            BlockFace.WEST,
            BlockFace.DOWN,};

    public Terraformer() {
        super(61);
        maxLevel = 1;
        loreName = "Terraformer";
        probability = 0;
        enchantable = new Tool[]{SHOVEL};
        conflicting = new Class[]{};
        description = "Places the leftmost blocks in the players inventory within a 7 block radius";
        cooldown = 0;
        power = -1.0;
        handUse = Hand.RIGHT;
    }

    @Override
    public boolean onBlockInteract(PlayerInteractEvent evt, int level, boolean usedHand) {
        if(evt.getPlayer().isSneaking()) {
            if(evt.getAction().equals(RIGHT_CLICK_BLOCK)) {
                Block start = evt.getClickedBlock().getRelative(evt.getBlockFace());
                List<Block> blocks = bfs(start);

                Material mat = AIR;
                byte bt = 0;
                int c = -1;

                for(int i = 0; i < 9; i++) {
                    if(evt.getPlayer().getInventory().getItem(i) != null) {
                        if(evt.getPlayer().getInventory().getItem(i).getType().isBlock() &&
                           ArrayUtils.contains(CompatibilityAdapter.getTerraformerMaterials(), evt.getPlayer().getInventory().getItem(i).getType())) {
                            mat = evt.getPlayer().getInventory().getItem(i).getType();
                            c = i;
                            bt = evt.getPlayer().getInventory().getItem(i).getData().getData();
                            break;
                        }
                    }
                }
                if(mat == HUGE_MUSHROOM_1 || mat == HUGE_MUSHROOM_2) {
                    bt = 14;
                }

                for(Block b : blocks) {
                    if(b.getType().equals(AIR)) {
                        if(Utilities.removeItemCheck(evt.getPlayer(), mat, bt, 1)) {
                            Storage.COMPATIBILITY_ADAPTER.placeBlock(b, evt.getPlayer(), mat, bt);
                            if(Storage.rnd.nextInt(10) == 5) {
                                Utilities.damageTool(evt.getPlayer(), 1, usedHand);
                            }
                        }
                    }
                }
                evt.getPlayer().updateInventory();
                return true;
            }
        }
        return false;
    }

    private List<Block> bfs(Block start) {
        LinkedList<Block> core = new LinkedList<>();
        LinkedList<Block> perimeter = new LinkedList<>();
        perimeter.add(start);

        while(!perimeter.isEmpty() && core.size() < 64) {
            Block block = perimeter.remove(0);
            for(BlockFace bf : SEARCH_FACES) {
                Block rBlock = block.getRelative(bf);
                if(rBlock.getType() == Material.AIR
                   && rBlock.getLocation().distanceSquared(start.getLocation()) < 25
                   && !perimeter.contains(rBlock)
                   && !core.contains(rBlock)) {
                    perimeter.add(rBlock);
                }
            }
            core.add(block);
        }
        return core;
    }

}
