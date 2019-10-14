package zedly.zenchantments.enchantments;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import zedly.zenchantments.CustomEnchantment;
import zedly.zenchantments.Storage;
import zedly.zenchantments.enums.Hand;
import zedly.zenchantments.enums.Tool;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static zedly.zenchantments.enums.Tool.*;

public class Grab extends CustomEnchantment {

	// Locations where Grab has been used on a block and are waiting for the Watcher to handle their teleportation
	public static final Set<Block>           fireDropLocs = new HashSet<>();
	// Locations where Grab has been used on a block and are waiting for the Watcher to handle their teleportation
	public static final Map<Block, Location> grabLocs     = new HashMap<>();
	public static final int                  ID           = 23;

	@Override
	public Builder<Grab> defaults() {
		return new Builder<>(Grab::new, ID)
			.maxLevel(1)
			.loreName("Grab")
			.probability(0)
			.enchantable(new Tool[]{PICKAXE, SHOVEL, AXE})
			.conflicting(new Class[]{})
			.description("Teleports mined items and XP directly to the player")
			.cooldown(0)
			.power(-1.0)
			.handUse(Hand.LEFT);
	}

	@Override
	public boolean onBlockBreak(final BlockBreakEvent evt, int level, boolean usedHand) {
		grabLocs.put(evt.getBlock(), evt.getPlayer().getLocation());
		final Block block = evt.getBlock();
		ADAPTER.breakBlockNMS(evt.getBlock(), evt.getPlayer());
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Storage.zenchantments, () -> {
			grabLocs.remove(block);
		}, 15);
		return true;
	}
}
