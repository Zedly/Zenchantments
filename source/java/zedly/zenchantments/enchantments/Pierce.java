package zedly.zenchantments.enchantments;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;

import java.util.HashSet;
import java.util.Set;

import static org.bukkit.event.block.Action.RIGHT_CLICK_AIR;
import static org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK;

@AZenchantment(runInSlots = Slots.MAIN_HAND, conflicting = {Anthropomorphism.class, Switch.class, Shred.class})
public final class Pierce extends Zenchantment {
    private static final int     MAX_BLOCKS   = 128;
    private static final int[][] SEARCH_FACES = Utilities.DEFAULT_SEARCH_FACES;

    // PIERCE MODES
    // 1 = normal
    // 2 = wide
    // 3 = deep
    // 4 = tall
    // 5 = ore

    @Override
    public boolean onBlockInteract(final @NotNull PlayerInteractEvent event, final int level, final EquipmentSlot slot) {
        final Player player = event.getPlayer();

        if (!player.hasMetadata("ze.pierce.mode")) {
            player.setMetadata("ze.pierce.mode", new FixedMetadataValue(ZenchantmentsPlugin.getInstance(), 1));
        }

        if (!player.isSneaking() || (event.getAction() != RIGHT_CLICK_AIR && event.getAction() != RIGHT_CLICK_BLOCK)) {
            return false;
        }

        int mode = player.getMetadata("ze.pierce.mode").get(0).asInt();
        mode = mode == 5 ? 1 : mode + 1;
        player.setMetadata("ze.pierce.mode", new FixedMetadataValue(ZenchantmentsPlugin.getInstance(), mode));

        switch (mode) {
            case 1:
                player.sendMessage(ChatColor.GRAY.toString() + ChatColor.ITALIC + "1x Normal Mode");
                break;
            case 2:
                player.sendMessage(ChatColor.GRAY.toString() + ChatColor.ITALIC + "3x Wide Mode");
                break;
            case 3:
                player.sendMessage(ChatColor.GRAY.toString() + ChatColor.ITALIC + "3x Long Mode");
                break;
            case 4:
                player.sendMessage(ChatColor.GRAY.toString() + ChatColor.ITALIC + "3x Tall Mode");
                break;
            case 5:
                player.sendMessage(ChatColor.GRAY.toString() + ChatColor.ITALIC + "Ore Mode");
                break;
        }

        return false;
    }

    @Override
    public boolean onBlockBreak(final @NotNull BlockBreakEvent event, final int level, final EquipmentSlot slot) {
        final Player player = event.getPlayer();

        if (!player.hasMetadata("ze.pierce.mode")) {
            player.setMetadata("ze.pierce.mode", new FixedMetadataValue(ZenchantmentsPlugin.getInstance(), 1));
        }

        final int mode = player.getMetadata("ze.pierce.mode").get(0).asInt();
        final Location blockLocation = event.getBlock().getLocation();
        final Location playerLocation = player.getLocation();
        final Set<Block> total = new HashSet<>();

        if (mode != 1 && mode != 5) {
            int add = -1;
            boolean b = false;
            int[][] ints = { { 0, 0, 0 }, { 0, 0, 0 }, { 0, 0, 0 } };
            switch (Utilities.getCardinalDirection(playerLocation.getYaw(), 0)) {
                case SOUTH:
                    ints = new int[][] { { 1, 0, 0 }, { 0, 0, 1 }, { 0, 1, 0 } };
                    add = 1;
                    b = true;
                    break;
                case WEST:
                    ints = new int[][] { { 0, 0, 1 }, { 1, 0, 0 }, { 0, 1, 0 } };
                    break;
                case NORTH:
                    ints = new int[][] { { 1, 0, 0 }, { 0, 0, 1 }, { 0, 1, 0 } };
                    b = true;
                    break;
                case EAST:
                    ints = new int[][] { { 0, 0, 1 }, { 1, 0, 0 }, { 0, 1, 0 } };
                    add = 1;
                    break;
            }
            final int[] rads = ints[mode - 2];
            if (mode == 3) {
                if (b) {
                    blockLocation.setZ(blockLocation.getZ() + add);
                } else {
                    blockLocation.setX(blockLocation.getX() + add);
                }
            }
            if (mode == 4) {
                if (playerLocation.getPitch() > 65) {
                    blockLocation.setY(blockLocation.getY() - 1);
                } else if (playerLocation.getPitch() < -65) {
                    blockLocation.setY(blockLocation.getY() + 1);
                }
            }
            for (int x = -(rads[0]); x <= rads[0]; x++) {
                for (int y = -(rads[1]); y <= rads[1]; y++) {
                    for (int z = -(rads[2]); z <= rads[2]; z++) {
                        total.add(blockLocation.getBlock().getRelative(x, y, z));
                    }
                }
            }
        } else if (mode == 5) {
            if (!MaterialList.ORES.contains(event.getBlock().getType())) {
                return false;
            }

            total.addAll(
                Utilities.bfs(
                    event.getBlock(),
                    MAX_BLOCKS,
                    false,
                    Float.MAX_VALUE,
                    SEARCH_FACES,
                    new MaterialList(event.getBlock().getType()),
                    MaterialList.EMPTY,
                    false,
                    true
                )
            );
        }

       for (final Block block : total) {
            if (WorldInteractionUtil.isBlockSafeToBreak(block)) {
                WorldInteractionUtil.breakBlock(block, event.getPlayer());
            }
        }

        return true;
    }

    @Override
    public boolean onBlockPlaceOtherHand(final @NotNull BlockPlaceEvent event, final int level, final EquipmentSlot slot) {
        if(event.getPlayer().isSneaking()) {
            event.setCancelled(true);
        }
        return false;
    }
}
