package zedly.zenchantments.enchantments;

import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import zedly.zenchantments.Zenchantment;
import zedly.zenchantments.Storage;
import zedly.zenchantments.Hand;
import zedly.zenchantments.Tool;

import static org.bukkit.Material.GOLD_NUGGET;
import static org.bukkit.Material.SAND;
import static zedly.zenchantments.Tool.SHOVEL;

public class GoldRush extends Zenchantment {

	public static final int ID = 22;

	@Override
	public Builder<GoldRush> defaults() {
		return new Builder<>(GoldRush::new, ID)
			.maxLevel(3)
			.name("Gold Rush")
			.probability(0)
			.enchantable(new Tool[]{SHOVEL})
			.conflicting(new Class[]{})
			.description("Randomly drops gold nuggets when mining sand")
			.cooldown(0)
			.power(1.0)
			.handUse(Hand.LEFT);
	}

	@Override
	public boolean onBlockBreak(BlockBreakEvent event, int level, boolean usedHand) {
		if (event.getBlock().getType() == SAND && Storage.rnd.nextInt(100) >= (100 - (level * power * 3))) {
			event.getBlock().getWorld()
			   .dropItemNaturally(event.getBlock().getLocation(), new ItemStack(GOLD_NUGGET));
			return true;
		}
		return false;
	}
}
