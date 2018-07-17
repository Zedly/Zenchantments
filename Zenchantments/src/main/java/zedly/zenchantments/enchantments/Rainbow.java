package zedly.zenchantments.enchantments;

import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.Material;
import org.bukkit.entity.Sheep;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.inventory.ItemStack;
import zedly.zenchantments.CustomEnchantment;
import zedly.zenchantments.Storage;
import zedly.zenchantments.Utilities;
import zedly.zenchantments.enums.Hand;
import zedly.zenchantments.enums.Tool;

import static org.bukkit.Material.*;
import static org.bukkit.block.BlockFace.DOWN;
import static zedly.zenchantments.enums.Tool.SHEAR;

public class Rainbow extends CustomEnchantment {

    private static final short[] FLOWER_DATA_VALUES = new short[]{0, 1, 2, 3, 4, 5, 10};

    public Rainbow() {
        super(47);
        maxLevel = 1;
        loreName = "Rainbow";
        probability = 0;
        enchantable = new Tool[]{SHEAR};
        conflicting = new Class[]{};
        description = "Drops random flowers and wool colors when used";
        cooldown = 0;
        power = -1.0;
        handUse = Hand.BOTH;
    }

    @Override
    public boolean onBlockBreak(BlockBreakEvent evt, int level, boolean usedHand) {
        short blockData;
        Material dropMaterial;
        if(evt.getBlock().getType() == RED_ROSE || evt.getBlock().getType() == YELLOW_FLOWER) {
            short sh = (short) Storage.rnd.nextInt(9);
            dropMaterial = (sh == 7) ? YELLOW_FLOWER : RED_ROSE;
            blockData = (sh == 7) ? 0 : (short) Storage.rnd.nextInt(9);
        } else if(evt.getBlock().getType() == DOUBLE_PLANT
                  && (ArrayUtils.contains(FLOWER_DATA_VALUES, evt.getBlock().getData()))) {
            dropMaterial = DOUBLE_PLANT;
            blockData = (short) Storage.rnd.nextInt(6);
            blockData = FLOWER_DATA_VALUES[blockData];
        } else {
            return false;
        }
        evt.setCancelled(true);
        if(evt.getBlock().getRelative(DOWN).getType() == DOUBLE_PLANT) {

            evt.getBlock().getRelative(DOWN).setType(AIR);
        }
        evt.getBlock().setType(AIR);
        Utilities.damageTool(evt.getPlayer(), 1, usedHand);
        evt.getPlayer().getWorld()
           .dropItem(Utilities.getCenter(evt.getBlock()), new ItemStack(dropMaterial, 1, blockData));
        return true;
    }

    @Override
    public boolean onShear(PlayerShearEntityEvent evt, int level, boolean usedHand) {
        Sheep sheep = (Sheep) evt.getEntity();
        if(!sheep.isSheared()) {
            int color = Storage.rnd.nextInt(16);
            int number = Storage.rnd.nextInt(3) + 1;
            Utilities.damageTool(evt.getPlayer(), 1, usedHand);
            evt.setCancelled(true);
            sheep.setSheared(true);
            evt.getEntity().getWorld().dropItemNaturally(Utilities.getCenter(evt.getEntity().getLocation()),
                                                         new ItemStack(WOOL, number, (short) color));
        }
        return true;
    }
}
