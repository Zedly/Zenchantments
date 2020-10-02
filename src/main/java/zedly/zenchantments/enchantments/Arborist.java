package zedly.zenchantments.enchantments;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import zedly.zenchantments.Zenchantment;
import zedly.zenchantments.Storage;
import zedly.zenchantments.Hand;
import zedly.zenchantments.Tool;

import static org.bukkit.Material.*;
import static zedly.zenchantments.Tool.AXE;

public class Arborist extends Zenchantment {

	public static final int ID = 2;

	@Override
	public Builder<Arborist> defaults() {
		return new Builder<>(Arborist::new, ID)
			.maxLevel(3)
			.loreName("Arborist")
			.probability(0)
			.enchantable(new Tool[]{AXE})
			.conflicting(new Class[]{})
			.description("Drops more apples, sticks, and saplings when used on leaves")
			.cooldown(0)
			.power(1.0)
			.handUse(Hand.LEFT);
	}

	@Override
	public boolean onBlockBreak(BlockBreakEvent event, int level, boolean usedHand) {
		Block blk = event.getBlock();
		Material mat = blk.getType();
		if (Storage.COMPATIBILITY_ADAPTER.Leaves().contains(mat)) {
			// Crudely get the index in the array of materials

			int index = Math.max(Storage.COMPATIBILITY_ADAPTER.Leaves().indexOf(mat),
				Storage.COMPATIBILITY_ADAPTER.Leaves().indexOf(mat));

			ItemStack stk = new ItemStack(Storage.COMPATIBILITY_ADAPTER.Saplings().get(index), 1);

			if (Storage.rnd.nextInt(10) >= (9 - level) / (power + .001)) {
				if (Storage.rnd.nextInt(3) % 3 == 0) {
					event.getBlock().getWorld()
					   .dropItemNaturally(event.getBlock().getLocation(), stk);
				}
				if (Storage.rnd.nextInt(3) % 3 == 0) {
					event.getBlock().getWorld()
					   .dropItemNaturally(event.getBlock().getLocation(), new ItemStack(STICK, 1));
				}
				if (Storage.rnd.nextInt(3) % 3 == 0) {
					event.getBlock().getWorld()
					   .dropItemNaturally(event.getBlock().getLocation(), new ItemStack(APPLE, 1));
				}
				if (Storage.rnd.nextInt(65) == 25) {
					event.getBlock().getWorld()
					   .dropItemNaturally(event.getBlock().getLocation(), new ItemStack(GOLDEN_APPLE, 1));
				}
				return true;
			}
		}
		return false;
	}
}
