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
			if (adapter.Wools().contains(original)) {
				newMat = adapter.Wools().getNext(original);
			} else if (adapter.StainedGlass().contains(original)) {
				newMat = adapter.StainedGlass().getNext(original);
			} else if (adapter.StainedGlassPanes().contains(original)) {
				newMat = adapter.StainedGlassPanes().getNext(original);
			} else if (adapter.Carpets().contains(original)) {
				newMat = adapter.Carpets().getNext(original);
			} else if (adapter.Terracottas().contains(original)) {
				newMat = adapter.Terracottas().getNext(original);
			} else if (adapter.GlazedTerracottas().contains(original)) {
				newMat = adapter.GlazedTerracottas().getNext(original);
			} else if (adapter.ConcretePowders().contains(original)) {
				newMat = adapter.ConcretePowders().getNext(original);
			} else if (adapter.Concretes().contains(original)) {
				newMat = adapter.Concretes().getNext(original);
			} else if (adapter.Woods().contains(original)) {
				newMat = adapter.Woods().getNext(original);
			} else if (adapter.StrippedLogs().contains(original)) {
				newMat = adapter.StrippedLogs().getNext(original);
			} else if (adapter.Planks().contains(original)) {
				newMat = adapter.Planks().getNext(original);
			} else if (adapter.Sands().contains(original)) {
				newMat = adapter.Sands().getNext(original);
			} else if (adapter.Saplings().contains(original)) {
				newMat = adapter.Saplings().getNext(original);
			} else if (adapter.Leaves().contains(original)) {
				newMat = adapter.Leaves().getNext(original);
			} else if (adapter.WoodFences().contains(original)) {
				newMat = adapter.WoodFences().getNext(original);
			} else if (adapter.WoodStairs().contains(original)) {
				newMat = adapter.WoodStairs().getNext(original);
			} else if (adapter.SmallFlowers().contains(original)) {
				newMat = adapter.SmallFlowers().getNext(original);
			} else if (adapter.Logs().contains(original)) {
				newMat = adapter.Logs().getNext(original);
			} else if (adapter.Sandstones().contains(original)) {
				newMat = adapter.Sandstones().getNext(original);
			} else if (adapter.Dirts().contains(original)) {
				newMat = adapter.Dirts().getNext(original);
			} else if (adapter.Stones().contains(original)) {
				newMat = adapter.Stones().getNext(original);
			} else if (adapter.Netherbricks().contains(original)) {
				newMat = adapter.Netherbricks().getNext(original);
			} else if (adapter.Cobblestones().contains(original)) {
				newMat = adapter.Cobblestones().getNext(original);
			} else if (adapter.Stonebricks().contains(original)) {
				newMat = adapter.Stonebricks().getNext(original);
			} else if (adapter.Ices().contains(original)) {
				newMat = adapter.Ices().getNext(original);
			} else if (adapter.Quartz().contains(original)) {
				newMat = adapter.Quartz().getNext(original);
			} else if (adapter.WoodPressurePlates().contains(original)) {
				newMat = adapter.WoodPressurePlates().getNext(original);
			} else if (adapter.PolishedStones().contains(original)) {
				newMat = adapter.PolishedStones().getNext(original);
			} else if (adapter.Prismarines().contains(original)) {
				newMat = adapter.Prismarines().getNext(original);
			} else if (adapter.StrippedWoods().contains(original)) {
				newMat = adapter.StrippedWoods().getNext(original);
			} else if (adapter.WoodSlabs().contains(original)) {
				newMat = adapter.WoodSlabs().getNext(original);
			} else if (adapter.WoodTrapdoors().contains(original)) {
				newMat = adapter.WoodTrapdoors().getNext(original);
			} else if (adapter.Endstones().contains(original)) {
				newMat = adapter.Endstones().getNext(original);
			} else if (adapter.Purpurs().contains(original)) {
				newMat = adapter.Purpurs().getNext(original);
			} else if (adapter.PrismarineStairs().contains(original)) {
				newMat = adapter.PrismarineStairs().getNext(original);
			} else if (adapter.PrismarineSlabs().contains(original)) {
				newMat = adapter.PrismarineSlabs().getNext(original);
			} else if (adapter.CobblestoneWalls().contains(original)) {
				newMat = adapter.CobblestoneWalls().getNext(original);
			} else if (adapter.CoralBlocks().contains(original)) {
				newMat = adapter.CoralBlocks().getNext(original);
			} else if (adapter.DeadCoralBlocks().contains(original)) {
				newMat = adapter.DeadCoralBlocks().getNext(original);
			} else if (adapter.DeadCorals().contains(original)) {
				newMat = adapter.DeadCorals().getNext(original);
			} else if (adapter.Corals().contains(original)) {
				newMat = adapter.Corals().getNext(original);
			} else if (adapter.CoralFans().contains(original)) {
				newMat = adapter.CoralFans().getNext(original);
			} else if (adapter.DeadCoralFans().contains(original)) {
				newMat = adapter.DeadCoralFans().getNext(original);
			} else if (adapter.DeadCoralWallFans().contains(original)) {
				newMat = adapter.DeadCoralWallFans().getNext(original);
			} else if (adapter.Mushrooms().contains(original)) {
				newMat = adapter.Mushrooms().getNext(original);
			} else if (adapter.MushroomBlocks().contains(original)) {
				newMat = adapter.MushroomBlocks().getNext(original);
			} else if (adapter.ShortGrasses().contains(original)) {
				newMat = adapter.ShortGrasses().getNext(original);
			} else if (adapter.LargeFlowers().contains(original)) {
				newMat = adapter.LargeFlowers().getNext(original);
			} else if (adapter.WoodTrapdoors().contains(original)) {
				newMat = adapter.WoodTrapdoors().getNext(original);
			} else if (adapter.WoodDoors().contains(original)) {
				newMat = adapter.WoodDoors().getNext(original);
			} else if (adapter.FenceGates().contains(original)) {
				newMat = adapter.FenceGates().getNext(original);
			} else if (adapter.WoodButtons().contains(original)) {
				newMat = adapter.WoodButtons().getNext(original);
			} else if (adapter.Beds().contains(original)) {
				newMat = adapter.Beds().getNext(original);
			} else if (adapter.StoneSlabs().contains(original)) {
				newMat = adapter.StoneSlabs().getNext(original);
			} else if (adapter.SandstoneSlabs().contains(original)) {
				newMat = adapter.SandstoneSlabs().getNext(original);
			} else if (adapter.StoneBrickSlabs().contains(original)) {
				newMat = adapter.StoneBrickSlabs().getNext(original);
			} else if (adapter.CobblestoneSlabs().contains(original)) {
				newMat = adapter.CobblestoneSlabs().getNext(original);
			} else if (adapter.QuartzSlabs().contains(original)) {
				newMat = adapter.QuartzSlabs().getNext(original);
			} else if (adapter.NetherBrickSlabs().contains(original)) {
				newMat = adapter.NetherBrickSlabs().getNext(original);
			} else if (adapter.StoneStairs().contains(original)) {
				newMat = adapter.StoneStairs().getNext(original);
			} else if (adapter.StoneBrickStairs().contains(original)) {
				newMat = adapter.StoneBrickStairs().getNext(original);
			} else if (adapter.SandstoneStairs().contains(original)) {
				newMat = adapter.SandstoneStairs().getNext(original);
			} else if (adapter.CobblestoneStairs().contains(original)) {
				newMat = adapter.CobblestoneStairs().getNext(original);
			} else if (adapter.QuartzStairs().contains(original)) {
				newMat = adapter.QuartzStairs().getNext(original);
			} else if (adapter.NetherBrickStairs().contains(original)) {
				newMat = adapter.NetherBrickStairs().getNext(original);
			} else if (adapter.StoneWalls().contains(original)) {
				newMat = adapter.StoneWalls().getNext(original);
			} else if (adapter.StoneBrickWalls().contains(original)) {
				newMat = adapter.StoneBrickWalls().getNext(original);
			} else if (adapter.Beds().contains(original)) {
				newMat = adapter.Beds().getNext(original);
			} else if (adapter.Beds().contains(original)) {
				newMat = adapter.Beds().getNext(original);
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
