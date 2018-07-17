package zedly.zenchantments.enchantments;

import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import zedly.zenchantments.CustomEnchantment;
import zedly.zenchantments.Storage;
import zedly.zenchantments.Utilities;
import zedly.zenchantments.enums.Hand;
import zedly.zenchantments.enums.Tool;

import static org.bukkit.Material.*;
import static zedly.zenchantments.enums.Tool.AXE;

public class Variety extends CustomEnchantment {

    ItemStack[] logs   = new ItemStack[]{new ItemStack(LOG, 1, (short) 0), new ItemStack(LOG, 1, (short) 1),
                                         new ItemStack(LOG, 1, (short) 2), new ItemStack(LOG, 1, (short) 3),
                                         new ItemStack(LOG_2, 1, (short) 0),
                                         new ItemStack(LOG_2, 1, (short) 1)};
    ItemStack[] leaves = new ItemStack[]{new ItemStack(LEAVES, 1, (short) 0), new ItemStack(LEAVES, 1, (short) 1),
                                         new ItemStack(LEAVES, 1, (short) 2), new ItemStack(LEAVES, 1, (short) 3),
                                         new ItemStack(LEAVES_2, 1, (short) 0),
                                         new ItemStack(LEAVES_2, 1, (short) 1)};

    public Variety() {
        super(65);
        maxLevel = 1;
        loreName = "Variety";
        probability = 0;
        enchantable = new Tool[]{AXE};
        conflicting = new Class[]{Fire.class};
        description = "Drops random types of wood or leaves";
        cooldown = 0;
        power = -1.0;
        handUse = Hand.LEFT;
    }

    @Override
    public boolean onBlockBreak(BlockBreakEvent evt, int level, boolean usedHand) {
        if(evt.getBlock().getType() == LOG || evt.getBlock().getType() == LOG_2) {
            evt.getBlock().setType(AIR);
            evt.getBlock().getWorld()
               .dropItemNaturally(Utilities.getCenter(evt.getBlock()), logs[Storage.rnd.nextInt(6)]);
            Utilities.damageTool(evt.getPlayer(), 1, usedHand);
        } else if(evt.getBlock().getType() == LEAVES || evt.getBlock().getType() == LEAVES_2) {
            evt.getBlock().setType(AIR);
            evt.getBlock().getWorld()
               .dropItemNaturally(Utilities.getCenter(evt.getBlock()), leaves[Storage.rnd.nextInt(6)]);
            Utilities.damageTool(evt.getPlayer(), 1, usedHand);
        }
        return true;
    }
}
