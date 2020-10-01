package zedly.zenchantments.enchantments;

import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import zedly.zenchantments.CustomEnchantment;
import zedly.zenchantments.Storage;
import zedly.zenchantments.Utilities;
import zedly.zenchantments.enums.Hand;
import zedly.zenchantments.enums.Tool;

import java.util.List;

import static zedly.zenchantments.enums.Tool.AXE;

public class Lumber extends CustomEnchantment {

	private static final int MAX_BLOCKS = 200;

	public static int[][] SEARCH_FACES = new int[][]{new int[]{}};

	public static final int ID = 34;

	@Override
	public Builder<Lumber> defaults() {
		return new Builder<>(Lumber::new, ID)
			.maxLevel(1)
			.loreName("Lumber")
			.probability(0)
			.enchantable(new Tool[]{AXE})
			.conflicting(new Class[]{})
			.description("Breaks the entire tree at once")
			.cooldown(0)
			.power(-1.0)
			.handUse(Hand.LEFT);


	}

	@Override
	public boolean onBlockBreak(BlockBreakEvent evt, int level, boolean usedHand) {
		if (!evt.getPlayer().isSneaking()) {
			return false;
		}
		Block startBlock = evt.getBlock();
		if (!Storage.COMPATIBILITY_ADAPTER.TrunkBlocks().contains(startBlock.getType())) {
			return false;
		}
		List<Block> blocks = Utilities.bfs(startBlock, MAX_BLOCKS, true, Float.MAX_VALUE, SEARCH_FACES,
			Storage.COMPATIBILITY_ADAPTER.TrunkBlocks(), Storage.COMPATIBILITY_ADAPTER.LumberWhitelist(),
			true, false);
		for (Block b : blocks) {
			ADAPTER.breakBlockNMS(b, evt.getPlayer());
		}
		return !blocks.isEmpty();
	}
}
