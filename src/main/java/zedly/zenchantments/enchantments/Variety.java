package zedly.zenchantments.enchantments;

import org.bukkit.Material;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import zedly.zenchantments.Zenchantment;
import zedly.zenchantments.Storage;
import zedly.zenchantments.Utilities;
import zedly.zenchantments.Hand;
import zedly.zenchantments.Tool;

import static org.bukkit.Material.*;
import static zedly.zenchantments.Tool.AXE;

public class Variety extends Zenchantment {

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
	public boolean onBlockBreak(BlockBreakEvent event, int level, boolean usedHand) {
		Material mat = event.getBlock().getType();
		if (Storage.COMPATIBILITY_ADAPTER.Logs().contains(mat)) {
			event.getBlock().setType(AIR);
			event.getBlock().getWorld()
			   .dropItemNaturally(event.getBlock().getLocation(),
				   new ItemStack(Storage.COMPATIBILITY_ADAPTER.Logs().getRandom()));
			Utilities.damageTool(event.getPlayer(), 1, usedHand);
		} else if (Storage.COMPATIBILITY_ADAPTER.Leaves().contains(mat)) {
			event.getBlock().setType(AIR);
			event.getBlock().getWorld()
			   .dropItemNaturally(event.getBlock().getLocation(),
				   new ItemStack(Storage.COMPATIBILITY_ADAPTER.Leaves().getRandom()));
			Utilities.damageTool(event.getPlayer(), 1, usedHand);
		}
		return true;
	}
}
