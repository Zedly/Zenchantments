package zedly.zenchantments.enchantments;

import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;
import zedly.zenchantments.CustomEnchantment;
import zedly.zenchantments.Utilities;
import zedly.zenchantments.enums.Hand;
import zedly.zenchantments.enums.Tool;

import static org.bukkit.Material.*;
import static org.bukkit.block.BlockFace.DOWN;
import static org.bukkit.block.BlockFace.UP;
import static org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK;
import static zedly.zenchantments.enums.Tool.SHOVEL;

public class Spectral extends CustomEnchantment {

    private static int increase(int old, int add) {
        if(old < add) {
            return ++old;
        } else {
            return 0;
        }
    }

    public Spectral() {
        super(54);
        maxLevel = 1;
        loreName = "Spectral";
        probability = 0;
        enchantable = new Tool[]{SHOVEL};
        conflicting = new Class[]{};
        description = "Allows for cycling through a block's types";
        cooldown = 0;
        power = -1.0;
        handUse = Hand.RIGHT;
    }

    @Override
    public boolean onBlockInteract(PlayerInteractEvent evt, int level, boolean usedHand) {
        if(evt.getClickedBlock() == null) {
            return false;
        }
        Material original = evt.getClickedBlock().getType();
        int originalInt = evt.getClickedBlock().getData();
        if(evt.getAction() != RIGHT_CLICK_BLOCK) {
            return false;
        }
        int data = evt.getClickedBlock().getData();
        switch(evt.getClickedBlock().getType()) {
            case WOOL:
            case STAINED_GLASS:
            case STAINED_GLASS_PANE:
            case CARPET:
            case STAINED_CLAY:
                data = increase(data, 15);
                break;
            case WOOD:
            case WOOD_STEP:
            case WOOD_DOUBLE_STEP:
            case SAPLING:
                data = increase(data, 6);
                break;
            case RED_SANDSTONE:
                if(data < 2) {
                    data++;
                } else {/**/
                    data = 0;
                    evt.getClickedBlock().setType(SANDSTONE);
                }
                break;
            case SANDSTONE:
                if(data < 2) {
                    data++;
                } else {
                    data = 0;
                    evt.getClickedBlock().setType(RED_SANDSTONE);
                }
                break;
            case RED_SANDSTONE_STAIRS:
                evt.getClickedBlock().setType(SANDSTONE_STAIRS);
                break;
            case SANDSTONE_STAIRS:
                evt.getClickedBlock().setType(RED_SANDSTONE_STAIRS);
                break;
            case SAND:
                data = increase(data, 2);
                break;
            case LONG_GRASS:
                data = increase(data, 3);
                break;
            case QUARTZ_BLOCK:
                data = increase(data, 4);
                break;
            case COBBLE_WALL:
                data = increase(data, 2);
                break;
            case STONE:
                data = increase(data, 7);
                break;
            case SMOOTH_BRICK:
                data = increase(data, 4);
                break;
            case COBBLESTONE:
                evt.getClickedBlock().setType(MOSSY_COBBLESTONE);
                break;
            case MOSSY_COBBLESTONE:
                evt.getClickedBlock().setType(COBBLESTONE);
                break;
            case BROWN_MUSHROOM:
                evt.getClickedBlock().setType(RED_MUSHROOM);
                break;
            case RED_MUSHROOM:
                evt.getClickedBlock().setType(BROWN_MUSHROOM);
                break;
            case HUGE_MUSHROOM_1:
                evt.getClickedBlock().setType(HUGE_MUSHROOM_2);
                break;
            case HUGE_MUSHROOM_2:
                evt.getClickedBlock().setType(HUGE_MUSHROOM_1);
                break;
            case STEP:
                if(evt.getClickedBlock().getData() == 1) {
                    evt.getClickedBlock().setType(STONE_SLAB2);
                    data = 0;
                }
                break;
            case STONE_SLAB2:
                if(evt.getClickedBlock().getData() == 0) {
                    evt.getClickedBlock().setType(STEP);
                    data = 1;
                }
                break;
            case DOUBLE_STEP:
                if(evt.getClickedBlock().getData() == 1) {
                    evt.getClickedBlock().setType(DOUBLE_STONE_SLAB2);
                    data = 0;
                }
                break;
            case DOUBLE_STONE_SLAB2:
                if(evt.getClickedBlock().getData() == 0) {
                    evt.getClickedBlock().setType(DOUBLE_STEP);
                    data = 1;
                }
                break;
            case DOUBLE_PLANT:
                if(evt.getClickedBlock().getRelative(DOWN).getType().equals(DOUBLE_PLANT)) {
                    evt.getClickedBlock().getRelative(DOWN)
                       .setData((byte) increase(evt.getClickedBlock().getRelative(DOWN).getData(), 6));
                } else if(evt.getClickedBlock().getRelative(UP).getType().equals(DOUBLE_PLANT)) {
                    data = increase(data, 6);
                }
                break;
            case LEAVES:
                if((data + 1) % 4 != 0 || data == 0) {
                    data++;
                } else {
                    data -= 3;
                    evt.getClickedBlock().setType(LEAVES_2);
                }
                break;
            case LEAVES_2:
                if((data + 1) % 2 != 0 || data == 0) {
                    data++;
                } else {
                    evt.getClickedBlock().setType(LEAVES);
                    data -= 1;
                }
                break;
            case LOG:
                if((data + 1) % 4 != 0 || data == 0) {
                    data++;
                } else {
                    data -= 3;
                    evt.getClickedBlock().setType(LOG_2);
                }
                break;
            case LOG_2:
                if((data + 1) % 2 != 0 || data == 0) {
                    data++;
                } else {
                    evt.getClickedBlock().setType(LOG);
                    data -= 1;
                }
                break;
            case YELLOW_FLOWER:
                evt.getClickedBlock().setType(RED_ROSE);
                break;
            case RED_ROSE:
                if(data < 8) {
                    data++;
                } else {
                    data = 0;
                    evt.getClickedBlock().setType(YELLOW_FLOWER);
                }
                break;
	        case GRASS_PATH:
		        evt.getClickedBlock().setType(GRASS);
		        break;
            case GRASS:
	            evt.getClickedBlock().setType(GRASS_PATH);
                break;
            case DIRT:
                if(data < 2) {
                    data++;
                } else {
                    data = 0;
                }
                break;
            case FENCE:
            case SPRUCE_FENCE:
            case BIRCH_FENCE:
            case JUNGLE_FENCE:
            case DARK_OAK_FENCE:
            case ACACIA_FENCE: {
                Material[] mats = new Material[]{FENCE, SPRUCE_FENCE, BIRCH_FENCE,
                                                 JUNGLE_FENCE, DARK_OAK_FENCE, ACACIA_FENCE};
                int index = ArrayUtils.indexOf(mats, evt.getClickedBlock().getType());
                if(index < mats.length - 1) {
                    evt.getClickedBlock().setType(mats[index + 1]);
                } else {
                    evt.getClickedBlock().setType(mats[0]);
                }
                break;
            }
            case FENCE_GATE:
            case SPRUCE_FENCE_GATE:
            case BIRCH_FENCE_GATE:
            case JUNGLE_FENCE_GATE:
            case DARK_OAK_FENCE_GATE:
            case ACACIA_FENCE_GATE: {
                Material[] mats = new Material[]{FENCE_GATE, SPRUCE_FENCE_GATE,
                                                 BIRCH_FENCE_GATE, JUNGLE_FENCE_GATE, DARK_OAK_FENCE_GATE,
                                                 ACACIA_FENCE_GATE};
                int index = ArrayUtils.indexOf(mats, evt.getClickedBlock().getType());
                if(index < mats.length - 1) {
                    evt.getClickedBlock().setType(mats[index + 1]);
                } else {
                    evt.getClickedBlock().setType(mats[0]);
                }
                break;
            }
            case WOOD_STAIRS:
            case SPRUCE_WOOD_STAIRS:
            case BIRCH_WOOD_STAIRS:
            case JUNGLE_WOOD_STAIRS:
            case DARK_OAK_STAIRS:
            case ACACIA_STAIRS: {
                Material[] mats = new Material[]{WOOD_STAIRS, SPRUCE_WOOD_STAIRS,
                                                 BIRCH_WOOD_STAIRS, JUNGLE_WOOD_STAIRS, DARK_OAK_STAIRS,
                                                 ACACIA_STAIRS};
                int index = ArrayUtils.indexOf(mats, evt.getClickedBlock().getType());
                if(index < mats.length - 1) {
                    evt.getClickedBlock().setType(mats[index + 1]);
                } else {
                    evt.getClickedBlock().setType(mats[0]);
                }
                break;
            }
            case WOODEN_DOOR:
            case SPRUCE_DOOR:
            case BIRCH_DOOR:
            case JUNGLE_DOOR:
            case DARK_OAK_DOOR:
            case ACACIA_DOOR: {
                Material type;
                Material[] mats = new Material[]{WOODEN_DOOR, SPRUCE_DOOR,
                                                 BIRCH_DOOR, JUNGLE_DOOR, DARK_OAK_DOOR, ACACIA_DOOR};
                int index = ArrayUtils.indexOf(mats, evt.getClickedBlock().getType());
                if(index < mats.length - 1) {
                    type = mats[index + 1];
                } else {
                    type = mats[0];
                }
                if(evt.getClickedBlock().getRelative(UP).getType().equals(evt.getClickedBlock().getType())) {
                    evt.getClickedBlock().setTypeIdAndData(type.getId(), (byte) data, false);
                    evt.getClickedBlock().getRelative(UP).setTypeIdAndData(type.getId(), (byte) 8, true);
                } else if(evt.getClickedBlock().getRelative(DOWN).getType()
                             .equals(evt.getClickedBlock().getType())) {
                    evt.getClickedBlock().setTypeIdAndData(type.getId(), (byte) 8, false);
                    evt.getClickedBlock().getRelative(DOWN)
                       .setTypeIdAndData(type.getId(), evt.getClickedBlock().getRelative(DOWN).getData(), true);
                }
                break;
            }
        }
        if(!evt.getClickedBlock().getType().equals(original) || data != originalInt) {
            evt.getClickedBlock().setData((byte) data);
            Utilities.damageTool(evt.getPlayer(), 1, usedHand);
            return true;
        }
        return false;
    }

}
