package zedly.zenchantments.enchantments;

import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import zedly.zenchantments.CustomEnchantment;
import zedly.zenchantments.Storage;
import zedly.zenchantments.Utilities;
import zedly.zenchantments.enums.Hand;
import zedly.zenchantments.enums.Tool;

import java.util.ArrayList;
import java.util.List;

import static org.bukkit.Material.GLOWING_REDSTONE_ORE;
import static org.bukkit.Material.REDSTONE_ORE;
import static org.bukkit.event.block.Action.RIGHT_CLICK_AIR;
import static org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK;
import static zedly.zenchantments.enums.Tool.PICKAXE;

public class Pierce extends CustomEnchantment {

    public Pierce() {
        super(42);
        maxLevel = 1;
        loreName = "Pierce";
        probability = 0;
        enchantable = new Tool[]{PICKAXE};
        conflicting = new Class[]{Anthropomorphism.class, Switch.class, Shred.class};
        description = "Lets the player mine in several modes which can be changed through shift clicking";
        cooldown = 0;
        power = -1.0;
        handUse = Hand.BOTH;
    }

    @Override
    public boolean onBlockInteract(PlayerInteractEvent evt, int level, boolean usedHand) {
        Player player = evt.getPlayer();
        if(!evt.getPlayer().hasMetadata("ze.pierce.mode")) {
            player.setMetadata("ze.pierce.mode", new FixedMetadataValue(Storage.zenchantments, 1));
        }
        if(player.isSneaking() && (evt.getAction() == RIGHT_CLICK_AIR || evt.getAction() == RIGHT_CLICK_BLOCK)) {
            int b = player.getMetadata("ze.pierce.mode").get(0).asInt();
            b = b == 5 ? 1 : b + 1;
            player.setMetadata("ze.pierce.mode", new FixedMetadataValue(Storage.zenchantments, b));
            switch(b) {
                case 1:
                    player.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "1x Normal Mode");
                    break;
                case 2:
                    player.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "3x Wide Mode");
                    break;
                case 3:
                    player.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "3x Long Mode");
                    break;
                case 4:
                    player.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "3x Tall Mode");
                    break;
                case 5:
                    player.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "Ore Mode");
                    break;
            }
        }
        return false;
    }

    @Override
    public boolean onBlockBreak(final BlockBreakEvent evt, int level, boolean usedHand) {
        //1 = normal; 2 = wide; 3 = deep; 4 = tall; 5 = ore
        Player player = evt.getPlayer();
        if(!evt.getPlayer().hasMetadata("ze.pierce.mode")) {
            player.setMetadata("ze.pierce.mode", new FixedMetadataValue(Storage.zenchantments, 1));
        }
        final int mode = player.getMetadata("ze.pierce.mode").get(0).asInt();
        List<Block> total = new ArrayList<>();
        final Location blkLoc = evt.getBlock().getLocation();
        if(mode != 1 && mode != 5) {
            int add = -1;
            boolean b = false;
            int[][] ints = {{0, 0, 0}, {0, 0, 0}, {0, 0, 0}};
            switch(Utilities.getCardinalDirection(evt.getPlayer().getLocation().getYaw(), 0)) {
                case SOUTH:
                    ints = new int[][]{{1, 0, 0}, {0, 0, 1}, {0, 1, 0}};
                    add = 1;
                    b = true;
                    break;
                case WEST:
                    ints = new int[][]{{0, 0, 1}, {1, 0, 0}, {0, 1, 0}};
                    break;
                case NORTH:
                    ints = new int[][]{{1, 0, 0}, {0, 0, 1}, {0, 1, 0}};
                    b = true;
                    break;
                case EAST:
                    ints = new int[][]{{0, 0, 1}, {1, 0, 0}, {0, 1, 0}};
                    add = 1;
                    break;
            }
            int[] rads = ints[mode - 2];
            if(mode == 3) {
                if(b) {
                    blkLoc.setZ(blkLoc.getZ() + add);
                } else {
                    blkLoc.setX(blkLoc.getX() + add);
                }
            }
            if(mode == 4) {
                if(evt.getPlayer().getLocation().getPitch() > 65) {
                    blkLoc.setY(blkLoc.getY() - 1);
                } else if(evt.getPlayer().getLocation().getPitch() < -65) {
                    blkLoc.setY(blkLoc.getY() + 1);
                }
            }
            for(int x = -(rads[0]); x <= rads[0]; x++) {
                for(int y = -(rads[1]); y <= rads[1]; y++) {
                    for(int z = -(rads[2]); z <= rads[2]; z++) {
                        total.add(blkLoc.getBlock().getRelative(x, y, z));
                    }
                }
            }
        } else if(mode == 5) {
            List<Block> used = new ArrayList<>();
            if(ArrayUtils.contains(Storage.ORES, evt.getBlock().getType())) {
                Material mat[];
                if(evt.getBlock().getType() != REDSTONE_ORE && evt.getBlock().getType() != GLOWING_REDSTONE_ORE) {
                    mat = new Material[]{evt.getBlock().getType()};
                } else {
                    mat = new Material[]{REDSTONE_ORE, GLOWING_REDSTONE_ORE};
                }
                oreBFS(evt.getBlock(), used, total, mat, 0);
            } else {
                return false;
            }
        }
        if(total.size() < 128) {
            for(Block b : total) {
                if(ADAPTER.isBlockSafeToBreak(b)) {
                    ADAPTER.breakBlockNMS(b, evt.getPlayer());
                }
            }
        }
        return true;
    }

    private void oreBFS(Block blk, List<Block> bks, List<Block> total, Material[] mat, int i) {
        i++;
        if(i >= 128 || total.size() >= 128) {
            return;
        }
        bks.add(blk);
        Location loc = blk.getLocation().clone();
        for(int x = -1; x <= 1; x++) {
            for(int y = -1; y <= 1; y++) {
                for(int z = -1; z <= 1; z++) {
                    Location loc2 = loc.clone();
                    loc2.setX(loc2.getX() + x);
                    loc2.setY(loc2.getY() + y);
                    loc2.setZ(loc2.getZ() + z);
                    if(!bks.contains(loc2.getBlock()) && (ArrayUtils.contains(mat, loc2.getBlock().getType()))) {
                        oreBFS(loc2.getBlock(), bks, total, mat, i);
                        total.add(loc2.getBlock());
                    }
                }
            }
        }
    }
}
