package zedly.zenchantments.enchantments;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import zedly.zenchantments.Zenchantment;
import zedly.zenchantments.Storage;
import zedly.zenchantments.Hand;
import zedly.zenchantments.Tool;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;

import static zedly.zenchantments.Tool.*;

public class Grab extends Zenchantment {

	// Locations where Grab has been used on a block and are waiting for the Watcher to handle their teleportation
	public static final Map<Block, Player> grabLocs     = new HashMap<>();
	public static final int                  ID           = 23;

	@Override
	public Builder<Grab> defaults() {
		return new Builder<>(Grab::new, ID)
			.maxLevel(1)
			.name("Grab")
			.probability(0)
			.enchantable(new Tool[]{PICKAXE, SHOVEL, AXE})
			.conflicting(new Class[]{})
			.description("Teleports mined items and XP directly to the player")
			.cooldown(0)
			.power(-1.0)
			.handUse(Hand.LEFT);
	}

	@Override
	public boolean onBlockBreak(final BlockBreakEvent event, int level, boolean usedHand) {
		grabLocs.put(event.getBlock(), event.getPlayer());
		final Block block = event.getBlock();
		//ADAPTER.breakBlockNMS(evt.getBlock(), evt.getPlayer());
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Storage.zenchantments, () -> {
			grabLocs.remove(block);
		}, 3);
		return true;
	}
}
