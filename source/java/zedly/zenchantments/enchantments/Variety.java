package zedly.zenchantments.enchantments;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;

import static org.bukkit.Material.AIR;
import static zedly.zenchantments.MaterialList.*;

@AZenchantment(runInSlots = Slots.MAIN_HAND, conflicting = {Fire.class})
public final class Variety extends Zenchantment {
    @Override
    public boolean onBlockBreak(final @NotNull BlockBreakEvent event, final int level, final EquipmentSlot slot) {
        final Block block = event.getBlock();
        final Material material = block.getType();

        if (LOGS.contains(material)) {
            block.setType(AIR);
            block.getWorld().dropItemNaturally(
                block.getLocation(),
                new ItemStack(LOGS.getRandom())
            );

            Utilities.damageItemStackRespectUnbreaking(event.getPlayer(), 1, slot);
        } else if (LEAVES.contains(material)) {
            block.setType(AIR);
            block.getWorld().dropItemNaturally(
                block.getLocation(),
                new ItemStack(LEAVES.getRandom())
            );
            Utilities.damageItemStackRespectUnbreaking(event.getPlayer(), 1, slot);
        }

        return true;
    }
}
