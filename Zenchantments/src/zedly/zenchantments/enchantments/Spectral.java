package zedly.zenchantments.enchantments;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.*;
import org.bukkit.block.data.type.*;
import org.bukkit.event.player.PlayerInteractEvent;
import zedly.zenchantments.CustomEnchantment;
import zedly.zenchantments.Storage;
import zedly.zenchantments.Utilities;
import zedly.zenchantments.compatibility.CompatibilityAdapter;
import zedly.zenchantments.enums.Hand;
import zedly.zenchantments.enums.Tool;

import java.util.*;

import static org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK;
import static zedly.zenchantments.enums.Tool.SHOVEL;

public class Spectral extends CustomEnchantment {

	public static final int ID = 54;

	@Override
	public Builder<Spectral> defaults() {
		return new Builder<>(Spectral::new, ID)
			.maxLevel(1)
			.loreName("Spectral")
			.probability(0)
			.enchantable(new Tool[]{SHOVEL})
			.conflicting(new Class[]{})
			.description("Allows for cycling through a block's types")
			.cooldown(0)
			.power(-1.0)
			.handUse(Hand.RIGHT);
	}

	private boolean cycleBlockType(Set<Block> blocks) {
		CompatibilityAdapter adapter = Storage.COMPATIBILITY_ADAPTER;
		boolean change = false;
		for (Block block : blocks) {
			Material original = block.getType();
			Material newMat = original;
			if (adapter.WOOL.contains(original)) {
				newMat = adapter.WOOL.getNext(original);
			} else if (adapter.STAINED_GLASSES.contains(original)) {
				newMat = adapter.STAINED_GLASSES.getNext(original);
			} else if (adapter.STAINED_GLASS_PANES.contains(original)) {
				newMat = adapter.STAINED_GLASS_PANES.getNext(original);
			} else if (adapter.CARPETS.contains(original)) {
				newMat = adapter.CARPETS.getNext(original);
			} else if (adapter.TERRACOTTAS.contains(original)) {
				newMat = adapter.TERRACOTTAS.getNext(original);
			} else if (adapter.GLAZED_TERRACOTTAS.contains(original)) {
				newMat = adapter.GLAZED_TERRACOTTAS.getNext(original);
			} else if (adapter.CONCRETE_POWDERS.contains(original)) {
				newMat = adapter.CONCRETE_POWDERS.getNext(original);
			} else if (adapter.CONCRETES.contains(original)) {
				newMat = adapter.CONCRETES.getNext(original);
			} else if (adapter.WOODS.contains(original)) {
				newMat = adapter.WOODS.getNext(original);
			} else if (adapter.STRIPPED_LOGS.contains(original)) {
				newMat = adapter.STRIPPED_LOGS.getNext(original);
			} else if (adapter.PLANKS_E.contains(original)) {
				newMat = adapter.PLANKS_E.getNext(original);
			} else if (adapter.SANDS.contains(original)) {
				newMat = adapter.SANDS.getNext(original);
			} else if (adapter.SAPLINGS.contains(original)) {
				newMat = adapter.SAPLINGS.getNext(original);
			} else if (adapter.LEAVES_E.contains(original)) {
				newMat = adapter.LEAVES_E.getNext(original);
			} else if (adapter.WOOD_FENCES.contains(original)) {
				newMat = adapter.WOOD_FENCES.getNext(original);
			} else if (adapter.WOOD_STAIRS_E.contains(original)) {
				newMat = adapter.WOOD_STAIRS_E.getNext(original);
			} else if (adapter.SMALL_FLOWERS.contains(original)) {
				newMat = adapter.SMALL_FLOWERS.getNext(original);
			} else if (adapter.LOGS.contains(original)) {
				newMat = adapter.LOGS.getNext(original);
			} else if (adapter.SANDSTONES.contains(original)) {
				newMat = adapter.SANDSTONES.getNext(original);
			} else if (adapter.DIRTS.contains(original)) {
				newMat = adapter.DIRTS.getNext(original);
			} else if (adapter.STONES.contains(original)) {
				newMat = adapter.STONES.getNext(original);
			} else if (adapter.NETHERBRICKS.contains(original)) {
				newMat = adapter.NETHERBRICKS.getNext(original);
			} else if (adapter.COBBLESTONES.contains(original)) {
				newMat = adapter.COBBLESTONES.getNext(original);
			} else if (adapter.STONE_BRICKS_E.contains(original)) {
				newMat = adapter.STONE_BRICKS_E.getNext(original);
			} else if (adapter.ICES.contains(original)) {
				newMat = adapter.ICES.getNext(original);
			} else if (adapter.QUARTZ_E.contains(original)) {
				newMat = adapter.QUARTZ_E.getNext(original);
			} else if (adapter.WOOD_PRESSURE_PLATES.contains(original)) {
				newMat = adapter.WOOD_PRESSURE_PLATES.getNext(original);
			} else if (adapter.POLISHED_STONES.contains(original)) {
				newMat = adapter.POLISHED_STONES.getNext(original);
			} else if (adapter.PRISMARINES.contains(original)) {
				newMat = adapter.PRISMARINES.getNext(original);
			} else if (adapter.STRIPPED_WOODS.contains(original)) {
				newMat = adapter.STRIPPED_WOODS.getNext(original);
			} else if (adapter.WOOD_SLABS.contains(original)) {
				newMat = adapter.WOOD_SLABS.getNext(original);
			} else if (adapter.WOOD_TRAPDOORS.contains(original)) {
				newMat = adapter.WOOD_TRAPDOORS.getNext(original);
			} else if (adapter.SANDSTONE_STAIRS_E.contains(original)) {
				newMat = adapter.SANDSTONE_STAIRS_E.getNext(original);
			} else if (adapter.SANDSTONE_SLABS.contains(original)) {
				newMat = adapter.SANDSTONE_SLABS.getNext(original);
			} else if (adapter.ENDSTONES.contains(original)) {
				newMat = adapter.ENDSTONES.getNext(original);
			} else if (adapter.PURPURS.contains(original)) {
				newMat = adapter.PURPURS.getNext(original);
			} else if (adapter.PRISMARINE_STAIRS_E.contains(original)) {
				newMat = adapter.PRISMARINE_STAIRS_E.getNext(original);
			} else if (adapter.PRISMARINE_SLABS.contains(original)) {
				newMat = adapter.PRISMARINE_SLABS.getNext(original);
			} else if (adapter.COBBLESTONE_WALLS.contains(original)) {
				newMat = adapter.COBBLESTONE_WALLS.getNext(original);
			} else if (adapter.CORAL_BLOCKS.contains(original)) {
				newMat = adapter.CORAL_BLOCKS.getNext(original);
			} else if (adapter.DEAD_CORAL_BLOCKS.contains(original)) {
				newMat = adapter.DEAD_CORAL_BLOCKS.getNext(original);
			} else if (adapter.DEAD_CORALS.contains(original)) {
				newMat = adapter.DEAD_CORALS.getNext(original);
			} else if (adapter.CORALS.contains(original)) {
				newMat = adapter.CORALS.getNext(original);
			} else if (adapter.CORAL_FANS.contains(original)) {
				newMat = adapter.CORAL_FANS.getNext(original);
			} else if (adapter.DEAD_CORAL_FANS.contains(original)) {
				newMat = adapter.DEAD_CORAL_FANS.getNext(original);
			} else if (adapter.DEAD_CORAL_WALL_FANS.contains(original)) {
				newMat = adapter.DEAD_CORAL_WALL_FANS.getNext(original);
			} else if (adapter.MUSHROOMS.contains(original)) {
				newMat = adapter.MUSHROOMS.getNext(original);
			} else if (adapter.MUSHROOM_BLOCKS.contains(original)) {
				newMat = adapter.MUSHROOM_BLOCKS.getNext(original);
			} else if (adapter.MUSHROOM_BLOCKS.contains(original)) {
				newMat = adapter.MUSHROOM_BLOCKS.getNext(original);
			} else if (adapter.SHORT_GRASSES.contains(original)) {
				newMat = adapter.SHORT_GRASSES.getNext(original);
			} else if (adapter.LARGE_FLOWERS.contains(original)) {
				newMat = adapter.LARGE_FLOWERS.getNext(original);
			} else if (adapter.WOOD_TRAPDOORS.contains(original)) {
				newMat = adapter.WOOD_TRAPDOORS.getNext(original);
			} else if (adapter.WOOD_DOORS.contains(original)) {
				newMat = adapter.WOOD_DOORS.getNext(original);
			} else if (adapter.FENCE_GATES.contains(original)) {
				newMat = adapter.FENCE_GATES.getNext(original);
			} else if (adapter.WOOD_BUTTONS.contains(original)) {
				newMat = adapter.WOOD_BUTTONS.getNext(original);
			} else if (adapter.BEDS.contains(original)) {
				newMat = adapter.BEDS.getNext(original);
			}

			if (!newMat.equals(original)) {
				change = true;
				BlockData blockData = block.getBlockData();
				final Material newMatFinal = newMat;
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Storage.zenchantments, () -> {

					block.setType(newMatFinal, false);

					if (blockData instanceof Bisected) {
						Bisected newBlockData = (Bisected) block.getBlockData();
						newBlockData.setHalf(((Bisected) blockData).getHalf());
						block.setBlockData(newBlockData, false);

						// Set the second half's data
						if (block.getRelative(BlockFace.UP).getType().equals(original)) {
							newBlockData.setHalf(Bisected.Half.TOP);
							block.getRelative(BlockFace.UP).setBlockData(newBlockData, false);
						}
						if (block.getRelative(BlockFace.DOWN).getType().equals(original)) {
							newBlockData.setHalf(Bisected.Half.BOTTOM);
							block.getRelative(BlockFace.DOWN).setBlockData(newBlockData, false);
						}
					}

					if (blockData instanceof Bed) {
						Bed newBlockData = (Bed) block.getBlockData();
						newBlockData.setPart(((Bed) blockData).getPart());
						block.setBlockData(newBlockData, false);

						// Set the second bed's part
						BlockFace facing = !newBlockData.getPart().equals(Bed.Part.HEAD)
							? ((Bed) blockData).getFacing()
							: ((Bed) blockData).getFacing().getOppositeFace();
						newBlockData.setPart(((Bed) block.getRelative(facing).getBlockData()).getPart());
						block.getRelative(facing).setBlockData(newBlockData, false);

						// Set the second bed's direction since we never do that later on
						Directional secondaryBlockData = (Directional) block.getRelative(facing).getBlockData();
						secondaryBlockData.setFacing(((Directional) blockData).getFacing());
						block.getRelative(facing).setBlockData(secondaryBlockData, true);

					}

					if (blockData instanceof Gate) {
						Gate newBlockData = (Gate) block.getBlockData();
						newBlockData.setInWall(((Gate) blockData).isInWall());
						block.setBlockData(newBlockData, true);
					}

					if (blockData instanceof Door) {
						Door newBlockData = (Door) block.getBlockData();
						newBlockData.setHinge(((Door) blockData).getHinge());
						block.setBlockData(newBlockData, true);
					}

					if (blockData instanceof Orientable) {
						Orientable newBlockData = (Orientable) block.getBlockData();
						newBlockData.setAxis(((Orientable) blockData).getAxis());
						block.setBlockData(newBlockData, true);
					}

					if (blockData instanceof Powerable) {
						Powerable newBlockData = (Powerable) block.getBlockData();
						newBlockData.setPowered(((Powerable) blockData).isPowered());
						block.setBlockData(newBlockData, true);
					}

					if (blockData instanceof Openable) {
						Openable newBlockData = (Openable) block.getBlockData();
						newBlockData.setOpen(((Openable) blockData).isOpen());
						block.setBlockData(newBlockData, true);
					}

					if (blockData instanceof Stairs) {
						Stairs newBlockData = (Stairs) block.getBlockData();
						newBlockData.setShape(((Stairs) blockData).getShape());
						block.setBlockData(newBlockData, true);
					}

					if (blockData instanceof Slab) {
						Slab newBlockData = (Slab) block.getBlockData();
						newBlockData.setType(((Slab) blockData).getType());
						block.setBlockData(newBlockData, true);
					}
					if (blockData instanceof MultipleFacing) {
						MultipleFacing newBlockData = (MultipleFacing) block.getBlockData();
						for (BlockFace bf : ((MultipleFacing) blockData).getFaces()) {
							newBlockData.setFace(bf, true);
						}
						block.setBlockData(newBlockData, true);
					}
					if (blockData instanceof Directional) {
						Directional newBlockData = (Directional) block.getBlockData();
						newBlockData.setFacing(((Directional) blockData).getFacing());
						block.setBlockData(newBlockData, true);
					}
					if (blockData instanceof Waterlogged) {
						Waterlogged newBlockData = (Waterlogged) block.getBlockData();
						newBlockData.setWaterlogged(((Waterlogged) blockData).isWaterlogged());
						block.setBlockData(newBlockData, true);
					}
				}, 0);
			}

		}
		return change;
	}


	public boolean doEvent(PlayerInteractEvent evt, int level, boolean usedHand) {
		if (evt.getClickedBlock() == null) {
			return false;
		}

		if (evt.getAction() != RIGHT_CLICK_BLOCK) {
			return false;
		}
		Set<Block> blocks = new HashSet<>();
		blocks.add(evt.getClickedBlock());
		if (evt.getPlayer().isSneaking()) {
			Set<Block> bfsResult = new HashSet<>();
			Set<Material> whitelist = new HashSet<>();
			whitelist.add(evt.getClickedBlock().getType());
			BFS(evt.getClickedBlock(), new HashSet<>(), bfsResult, whitelist, new HashSet<>(), 1024);
			blocks.addAll(bfsResult);
		}

		boolean result = cycleBlockType(blocks);
		if (result) {
			Utilities.damageTool(evt.getPlayer(), (int) Math.ceil(Math.log(blocks.size() + 1) / Math.log(2)),
				usedHand);
		}
		evt.setCancelled(true);
		return result;
	}

	@Override
	public boolean onBlockInteractInteractable(PlayerInteractEvent evt, int level, boolean usedHand) {
		return doEvent(evt, level, usedHand);
	}

	@Override
	public boolean onBlockInteract(PlayerInteractEvent evt, int level, boolean usedHand) {
		return doEvent(evt, level, usedHand);
	}

	private boolean BFS(Block blk, Set<Block> searched, Set<Block> discovered, Set<Material> whitelist,
		Set<Material> blacklist, int maxCount) {

		Queue<Block> q = new ArrayDeque<>();
		searched.add(blk);
		q.add(blk);

		while (!q.isEmpty()) {
			if (discovered.size() >= maxCount || searched.size() >= 10000) {
				return true;
			}

			blk = q.poll();
			for (int x = -1; x <= 1; x++) {
				for (int y = -1; y <= 1; y++) {
					for (int z = -1; z <= 1; z++) {
						if (!searched.contains(blk.getRelative(x, y, z))) {
							searched.add(blk.getRelative(x, y, z));
							if (whitelist.contains(blk.getRelative(x, y, z).getType())) {
								q.add(blk.getRelative(x, y, z));
								discovered.add(blk.getRelative(x, y, z));
							} else if (blacklist.contains(blk.getRelative(x, y, z).getType())) {
								discovered.clear();
								return false;
							}
						}
					}
				}
			}
		}
		return true;
	}

}
