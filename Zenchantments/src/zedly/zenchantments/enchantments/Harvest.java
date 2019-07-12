package zedly.zenchantments.enchantments;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.event.player.PlayerInteractEvent;
import zedly.zenchantments.CustomEnchantment;
import zedly.zenchantments.Storage;
import zedly.zenchantments.Utilities;
import zedly.zenchantments.enums.Hand;
import zedly.zenchantments.enums.Tool;

import static org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK;
import static zedly.zenchantments.enums.Tool.HOE;

public class Harvest extends CustomEnchantment {

	public static final int ID = 26;

	@Override
	public Builder<Harvest> defaults() {
		return new Builder<>(Harvest::new, ID)
			.maxLevel(3)
			.loreName("Harvest")
			.probability(0)
			.enchantable(new Tool[]{HOE})
			.conflicting(new Class[]{})
			.description("Harvests fully grown crops within a radius when clicked")
			.cooldown(0)
			.power(1.0)
			.handUse(Hand.RIGHT);
	}

	@Override
	public boolean onBlockInteract(PlayerInteractEvent evt, int level, boolean usedHand) {
		if (evt.getAction() != RIGHT_CLICK_BLOCK) {
			return false;
		}
		Location loc = evt.getClickedBlock().getLocation();
		int radiusXZ = (int) Math.round(power * level + 2);
		int radiusY = 1;
		boolean success = false;

		for (int x = -radiusXZ; x <= radiusXZ; x++) {
			for (int y = -radiusY - 1; y <= radiusY - 1; y++) {
				for (int z = -radiusXZ; z <= radiusXZ; z++) {

					final Block block = loc.getBlock().getRelative(x, y, z);
					if (block.getLocation().distanceSquared(loc) < radiusXZ * radiusXZ) {

						if (!Storage.COMPATIBILITY_ADAPTER.GrownCrops().contains(block.getType()) &&
							!Storage.COMPATIBILITY_ADAPTER.GrownMelon().contains(block.getType())) {
							continue;
						}


						BlockData cropState = block.getBlockData();
						boolean harvestReady = !(cropState instanceof Ageable);
						if (!harvestReady) {
							Ageable ag = (Ageable) cropState;
							harvestReady = ag.getAge() == ag.getMaximumAge();
						}

						if (harvestReady) {
							if (ADAPTER.breakBlockNMS(block, evt.getPlayer())) {
								Utilities.damageTool(evt.getPlayer(), 1, usedHand);
								Grab.grabLocs.put(block, evt.getPlayer().getLocation());
								Bukkit.getServer().getScheduler()
								      .scheduleSyncDelayedTask(Storage.zenchantments, () -> {
									      Grab.grabLocs.remove(block);
								      }, 3);
								success = true;
							}
						}
					}
				}
			}
		}
		return success;
	}
}
