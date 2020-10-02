package zedly.zenchantments.enchantments;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.player.PlayerInteractEvent;
import zedly.zenchantments.Zenchantment;
import zedly.zenchantments.Storage;
import zedly.zenchantments.Utilities;
import zedly.zenchantments.compatibility.EnumStorage;
import zedly.zenchantments.Hand;
import zedly.zenchantments.Tool;

import static org.bukkit.Material.*;
import static org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK;
import static zedly.zenchantments.Tool.SHOVEL;

public class Terraformer extends Zenchantment {

	public static int[][] SEARCH_FACES = new int[][]{new int[]{-1, 0, 0}, new int[]{1, 0, 0}, new int[]{0, -1, 0}, new int[]{0, 0, -1}, new int[]{0, 0, 1}};

	private static final int MAX_BLOCKS = 64;

	public static final int ID = 61;

	@Override
	public Builder<Terraformer> defaults() {
		return new Builder<>(Terraformer::new, ID)
			.maxLevel(1)
			.loreName("Terraformer")
			.probability(0)
			.enchantable(new Tool[]{SHOVEL})
			.conflicting(new Class[]{})
			.description("Places the leftmost blocks in the players inventory within a 7 block radius")
			.cooldown(0)
			.power(-1.0)
			.handUse(Hand.RIGHT);
	}

	@Override
	public boolean onBlockInteract(PlayerInteractEvent event, int level, boolean usedHand) {
		if (event.getPlayer().isSneaking()) {
			if (event.getAction().equals(RIGHT_CLICK_BLOCK)) {
				Block start = event.getClickedBlock().getRelative(event.getBlockFace());
				Material mat = AIR;

				for (int i = 0; i < 9; i++) {
					if (event.getPlayer().getInventory().getItem(i) != null) {
						if (event.getPlayer().getInventory().getItem(i).getType().isBlock() &&
							Storage.COMPATIBILITY_ADAPTER.TerraformerMaterials().contains(
								event.getPlayer().getInventory().getItem(i).getType())) {
							mat = event.getPlayer().getInventory().getItem(i).getType();
							break;
						}
					}
				}

				for (Block b : Utilities.bfs(start, MAX_BLOCKS, false, 5.f, SEARCH_FACES,
					Storage.COMPATIBILITY_ADAPTER.Airs(), new EnumStorage<>(new Material[]{}), false, true)) {
					if (b.getType().equals(AIR)) {
						if (Utilities.hasItem(event.getPlayer(), mat, 1)) {
							if (Storage.COMPATIBILITY_ADAPTER.placeBlock(b, event.getPlayer(), mat, null)) {
								Utilities.removeItem(event.getPlayer(), mat, 1);
								if (Storage.rnd.nextInt(10) == 5) {
									Utilities.damageTool(event.getPlayer(), 1, usedHand);
								}
							}
						}
					}
				}
				return true;
			}
		}
		return false;
	}


}
