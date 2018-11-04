package zedly.zenchantments.enchantments;

import org.bukkit.Material;
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

    public static final int ID = 65;

    @Override
    public Builder<Variety> defaults() {
        return new Builder<>(Variety::new, ID)
            .maxLevel(1)
            .loreName("Variety")
            .probability(0)
            .enchantable(new Tool[]{AXE})
            .conflicting(new Class[]{Fire.class})
            .description("Drops random types of wood or leaves")
            .cooldown(0)
            .power(-1.0)
            .handUse(Hand.LEFT);
    }

    @Override
    public boolean onBlockBreak(BlockBreakEvent evt, int level, boolean usedHand) {
        Material mat = evt.getBlock().getType();
        if(Storage.COMPATIBILITY_ADAPTER.LOGS.contains(mat)) {
            evt.getBlock().setType(AIR);
            evt.getBlock().getWorld()
               .dropItemNaturally(Utilities.getCenter(evt.getBlock()), new ItemStack(Storage.COMPATIBILITY_ADAPTER.LOGS.getRandom()));
            Utilities.damageTool(evt.getPlayer(), 1, usedHand);
        } else if(Storage.COMPATIBILITY_ADAPTER.LEAVESS.contains(mat)) {
            evt.getBlock().setType(AIR);
            evt.getBlock().getWorld()
               .dropItemNaturally(Utilities.getCenter(evt.getBlock()), new ItemStack(Storage.COMPATIBILITY_ADAPTER.LEAVESS.getRandom()));
            Utilities.damageTool(evt.getPlayer(), 1, usedHand);
        }
        return true;
    }
}
