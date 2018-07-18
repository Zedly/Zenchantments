package zedly.zenchantments.enchantments;

import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import zedly.zenchantments.CustomEnchantment;
import zedly.zenchantments.Storage;
import zedly.zenchantments.Utilities;
import zedly.zenchantments.enums.Hand;
import zedly.zenchantments.enums.Tool;

import static org.bukkit.Material.*;
import static zedly.zenchantments.enums.Tool.AXE;

public class Arborist extends CustomEnchantment {

    public Arborist() {
        super(2);
        maxLevel = 3;
        loreName = "Arborist";
        probability = 0;
        enchantable = new Tool[]{AXE};
        conflicting = new Class[]{};
        description = "Drops more apples, sticks, and saplings when used on leaves and wood";
        cooldown = 0;
        power = 1.0;
        handUse = Hand.LEFT;
    }

    @Override
    public boolean onBlockBreak(BlockBreakEvent evt, int level, boolean usedHand) {
        Block blk = evt.getBlock();
        if(blk.getType() == LOG || blk.getType() == LOG_2 || blk.getType() == LEAVES_2 || blk.getType() == LEAVES) {
            short s = (short) blk.getData();
            if(s >= 8) {
                s -= 8;
            }
            ItemStack stk;
            if(blk.getType() == LOG_2 || blk.getType() == LEAVES_2) {
                stk = new ItemStack(SAPLING, 1, (short) (s + 4));
            } else {
                stk = new ItemStack(SAPLING, 1, s);
            }
            if(Storage.rnd.nextInt(10) >= (9 - level) / (power + .001)) {
                if(Storage.rnd.nextInt(3) % 3 == 0) {
                    evt.getBlock().getWorld()
                       .dropItemNaturally(Utilities.getCenter(evt.getBlock()), stk);
                }
                if(Storage.rnd.nextInt(3) % 3 == 0) {
                    evt.getBlock().getWorld()
                       .dropItemNaturally(Utilities.getCenter(evt.getBlock()), new ItemStack(STICK, 1));
                }
                if(Storage.rnd.nextInt(3) % 3 == 0) {
                    evt.getBlock().getWorld()
                       .dropItemNaturally(Utilities.getCenter(evt.getBlock()), new ItemStack(APPLE, 1));
                }
                if(Storage.rnd.nextInt(65) == 25) {
                    evt.getBlock().getWorld()
                       .dropItemNaturally(Utilities.getCenter(evt.getBlock()), new ItemStack(GOLDEN_APPLE, 1));
                }
                return true;
            }
        }
        return false;
    }
}
