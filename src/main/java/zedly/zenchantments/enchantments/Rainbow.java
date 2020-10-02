package zedly.zenchantments.enchantments;

import org.bukkit.Material;
import org.bukkit.entity.Sheep;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.inventory.ItemStack;
import zedly.zenchantments.Zenchantment;
import zedly.zenchantments.Storage;
import zedly.zenchantments.Utilities;
import zedly.zenchantments.enums.Hand;
import zedly.zenchantments.enums.Tool;

import static org.bukkit.Material.*;
import static org.bukkit.block.BlockFace.DOWN;
import static zedly.zenchantments.enums.Tool.SHEAR;

public class Rainbow extends Zenchantment {

	public static final int ID = 47;

	@Override
	public Builder<Rainbow> defaults() {
		return new Builder<>(Rainbow::new, ID)
			.maxLevel(1)
			.loreName("Rainbow")
			.probability(0)
			.enchantable(new Tool[]{SHEAR})
			.conflicting(new Class[]{})
			.description("Drops random flowers and wool colors when used")
			.cooldown(0)
			.power(-1.0)
			.handUse(Hand.BOTH);
	}

	@Override
	public boolean onBlockBreak(BlockBreakEvent evt, int level, boolean usedHand) {
		Material dropMaterial;
		if (Storage.COMPATIBILITY_ADAPTER.SmallFlowers().contains(evt.getBlock().getType())) {
			dropMaterial = Storage.COMPATIBILITY_ADAPTER.SmallFlowers().getRandom();
		} else if (Storage.COMPATIBILITY_ADAPTER.LargeFlowers().contains(evt.getBlock().getType())) {
			dropMaterial = Storage.COMPATIBILITY_ADAPTER.LargeFlowers().getRandom();
		} else {
			return false;
		}
		evt.setCancelled(true);
		if (Storage.COMPATIBILITY_ADAPTER.LargeFlowers().contains(evt.getBlock().getRelative(DOWN).getType())) {
			evt.getBlock().getRelative(DOWN).setType(AIR);
		}
		evt.getBlock().setType(AIR);
		Utilities.damageTool(evt.getPlayer(), 1, usedHand);
		evt.getPlayer().getWorld().dropItem(Utilities.getCenter(evt.getBlock()), new ItemStack(dropMaterial, 1));
		return true;
	}

	@Override
	public boolean onShear(PlayerShearEntityEvent evt, int level, boolean usedHand) {
		Sheep sheep = (Sheep) evt.getEntity();
		if (!sheep.isSheared()) {
			int count = Storage.rnd.nextInt(3) + 1;
			Utilities.damageTool(evt.getPlayer(), 1, usedHand);
			evt.setCancelled(true);
			sheep.setSheared(true);
			evt.getEntity().getWorld().dropItemNaturally(evt.getEntity().getLocation(),
				new ItemStack(Storage.COMPATIBILITY_ADAPTER.Wools().getRandom(), count));
		}
		return true;
	}
}
