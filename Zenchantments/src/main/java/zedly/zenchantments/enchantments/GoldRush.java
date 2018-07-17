package zedly.zenchantments.enchantments;

import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import zedly.zenchantments.CustomEnchantment;
import zedly.zenchantments.Storage;
import zedly.zenchantments.Utilities;
import zedly.zenchantments.enums.Hand;
import zedly.zenchantments.enums.Tool;

import static org.bukkit.Material.GOLD_NUGGET;
import static org.bukkit.Material.SAND;
import static zedly.zenchantments.enums.Tool.SHOVEL;

public class GoldRush extends CustomEnchantment {

    public GoldRush() {
        super(22);
        maxLevel = 3;
        loreName = "Gold Rush";
        probability = 0;
        enchantable = new Tool[]{SHOVEL};
        conflicting = new Class[]{};
        description = "Randomly drops gold nuggets when mining sand";
        cooldown = 0;
        power = 1.0;
        handUse = Hand.LEFT;
    }

    @Override
    public boolean onBlockBreak(BlockBreakEvent evt, int level, boolean usedHand) {
        if(evt.getBlock().getType() == SAND && Storage.rnd.nextInt(100) >= (100 - (level * power * 3))) {
            evt.getBlock().getWorld()
               .dropItemNaturally(Utilities.getCenter(evt.getBlock()), new ItemStack(GOLD_NUGGET));
            return true;
        }
        return false;
    }
}
