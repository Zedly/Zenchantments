package zedly.zenchantments.enchantments;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.*;
import org.bukkit.block.data.type.Slab;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.event.player.PlayerInteractEvent;
import zedly.zenchantments.CustomEnchantment;
import zedly.zenchantments.Storage;
import zedly.zenchantments.Utilities;
import zedly.zenchantments.enums.Hand;
import zedly.zenchantments.enums.Tool;

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

	@Override
	public boolean onBlockInteract(PlayerInteractEvent evt, int level, boolean usedHand) {
		if (evt.getClickedBlock() == null) {
			return false;
		}

		Material original = evt.getClickedBlock().getType();
		if (evt.getAction() != RIGHT_CLICK_BLOCK) {
			return false;
		}

		Material newMat = original;
		if (Storage.COMPATIBILITY_ADAPTER.WOOL.contains(original)) {
			newMat = Storage.COMPATIBILITY_ADAPTER.WOOL.getNext(original);
		} else if (Storage.COMPATIBILITY_ADAPTER.STAINED_GLASSES.contains(original)) {
			newMat = Storage.COMPATIBILITY_ADAPTER.STAINED_GLASSES.getNext(original);
		} else if (Storage.COMPATIBILITY_ADAPTER.STAINED_GLASS_PANES.contains(original)) {
			newMat = Storage.COMPATIBILITY_ADAPTER.STAINED_GLASS_PANES.getNext(original);
		} else if (Storage.COMPATIBILITY_ADAPTER.CARPETS.contains(original)) {
			newMat = Storage.COMPATIBILITY_ADAPTER.CARPETS.getNext(original);
		} else if (Storage.COMPATIBILITY_ADAPTER.TERRACOTTAS.contains(original)) {
			newMat = Storage.COMPATIBILITY_ADAPTER.TERRACOTTAS.getNext(original);
		} else if (Storage.COMPATIBILITY_ADAPTER.GLAZED_TERRACOTTAS.contains(original)) {
			newMat = Storage.COMPATIBILITY_ADAPTER.GLAZED_TERRACOTTAS.getNext(original);
		} else if (Storage.COMPATIBILITY_ADAPTER.CONCRETE_POWDERS.contains(original)) {
			newMat = Storage.COMPATIBILITY_ADAPTER.CONCRETE_POWDERS.getNext(original);
		} else if (Storage.COMPATIBILITY_ADAPTER.CONCRETES.contains(original)) {
			newMat = Storage.COMPATIBILITY_ADAPTER.CONCRETES.getNext(original);
		} else if (Storage.COMPATIBILITY_ADAPTER.WOODS.contains(original)) {
			newMat = Storage.COMPATIBILITY_ADAPTER.WOODS.getNext(original);
		} else if (Storage.COMPATIBILITY_ADAPTER.STRIPPED_LOGS.contains(original)) {
			newMat = Storage.COMPATIBILITY_ADAPTER.STRIPPED_LOGS.getNext(original);
		} else if (Storage.COMPATIBILITY_ADAPTER.PLANKS_E.contains(original)) {
			newMat = Storage.COMPATIBILITY_ADAPTER.PLANKS_E.getNext(original);
		} else if (Storage.COMPATIBILITY_ADAPTER.SANDS.contains(original)) {
			newMat = Storage.COMPATIBILITY_ADAPTER.SANDS.getNext(original);
		} else if (Storage.COMPATIBILITY_ADAPTER.SAPLINGS.contains(original)) {
			newMat = Storage.COMPATIBILITY_ADAPTER.SAPLINGS.getNext(original);
		} else if (Storage.COMPATIBILITY_ADAPTER.LEAVES_E.contains(original)) {
			newMat = Storage.COMPATIBILITY_ADAPTER.LEAVES_E.getNext(original);
		} else if (Storage.COMPATIBILITY_ADAPTER.WOOD_FENCES.contains(original)) {
			newMat = Storage.COMPATIBILITY_ADAPTER.WOOD_FENCES.getNext(original);
		} else if (Storage.COMPATIBILITY_ADAPTER.WOOD_STAIRS_E.contains(original)) {
			newMat = Storage.COMPATIBILITY_ADAPTER.WOOD_STAIRS_E.getNext(original);
		} else if (Storage.COMPATIBILITY_ADAPTER.SMALL_FLOWERS.contains(original)) {
			newMat = Storage.COMPATIBILITY_ADAPTER.SMALL_FLOWERS.getNext(original);
		} else if (Storage.COMPATIBILITY_ADAPTER.LOGS.contains(original)) {
			newMat = Storage.COMPATIBILITY_ADAPTER.LOGS.getNext(original);
		} else if (Storage.COMPATIBILITY_ADAPTER.SANDSTONES.contains(original)) {
			newMat = Storage.COMPATIBILITY_ADAPTER.SANDSTONES.getNext(original);
		} else if (Storage.COMPATIBILITY_ADAPTER.DIRTS.contains(original)) {
			newMat = Storage.COMPATIBILITY_ADAPTER.DIRTS.getNext(original);
		} else if (Storage.COMPATIBILITY_ADAPTER.STONES.contains(original)) {
			newMat = Storage.COMPATIBILITY_ADAPTER.STONES.getNext(original);
		} else if (Storage.COMPATIBILITY_ADAPTER.NETHERBRICKS.contains(original)) {
			newMat = Storage.COMPATIBILITY_ADAPTER.NETHERBRICKS.getNext(original);
		} else if (Storage.COMPATIBILITY_ADAPTER.COBBLESTONES.contains(original)) {
			newMat = Storage.COMPATIBILITY_ADAPTER.COBBLESTONES.getNext(original);
		} else if (Storage.COMPATIBILITY_ADAPTER.STONE_BRICKS_E.contains(original)) {
			newMat = Storage.COMPATIBILITY_ADAPTER.STONE_BRICKS_E.getNext(original);
		} else if (Storage.COMPATIBILITY_ADAPTER.ICES.contains(original)) {
			newMat = Storage.COMPATIBILITY_ADAPTER.ICES.getNext(original);
		} else if (Storage.COMPATIBILITY_ADAPTER.QUARTZ_E.contains(original)) {
			newMat = Storage.COMPATIBILITY_ADAPTER.QUARTZ_E.getNext(original);
		} else if (Storage.COMPATIBILITY_ADAPTER.WOOD_PRESSURE_PLATES.contains(original)) {
			newMat = Storage.COMPATIBILITY_ADAPTER.WOOD_PRESSURE_PLATES.getNext(original);
		} else if (Storage.COMPATIBILITY_ADAPTER.POLISHED_STONES.contains(original)) {
			newMat = Storage.COMPATIBILITY_ADAPTER.POLISHED_STONES.getNext(original);
		} else if (Storage.COMPATIBILITY_ADAPTER.PRISMARINES.contains(original)) {
			newMat = Storage.COMPATIBILITY_ADAPTER.PRISMARINES.getNext(original);
		} else if (Storage.COMPATIBILITY_ADAPTER.STRIPPED_WOODS.contains(original)) {
			newMat = Storage.COMPATIBILITY_ADAPTER.STRIPPED_WOODS.getNext(original);
		} else if (Storage.COMPATIBILITY_ADAPTER.WOOD_SLABS.contains(original)) {
			newMat = Storage.COMPATIBILITY_ADAPTER.WOOD_SLABS.getNext(original);
		} else if (Storage.COMPATIBILITY_ADAPTER.WOOD_TRAPDOORS.contains(original)) {
			newMat = Storage.COMPATIBILITY_ADAPTER.WOOD_TRAPDOORS.getNext(original);
		} else if (Storage.COMPATIBILITY_ADAPTER.SANDSTONE_STAIRS_E.contains(original)) {
			newMat = Storage.COMPATIBILITY_ADAPTER.SANDSTONE_STAIRS_E.getNext(original);
		} else if (Storage.COMPATIBILITY_ADAPTER.SANDSTONE_SLABS.contains(original)) {
			newMat = Storage.COMPATIBILITY_ADAPTER.SANDSTONE_SLABS.getNext(original);
		} else if (Storage.COMPATIBILITY_ADAPTER.ENDSTONES.contains(original)) {
			newMat = Storage.COMPATIBILITY_ADAPTER.ENDSTONES.getNext(original);
		} else if (Storage.COMPATIBILITY_ADAPTER.PURPURS.contains(original)) {
			newMat = Storage.COMPATIBILITY_ADAPTER.PURPURS.getNext(original);
		} else if (Storage.COMPATIBILITY_ADAPTER.PRISMARINE_STAIRS_E.contains(original)) {
			newMat = Storage.COMPATIBILITY_ADAPTER.PRISMARINE_STAIRS_E.getNext(original);
		} else if (Storage.COMPATIBILITY_ADAPTER.PRISMARINE_SLABS.contains(original)) {
			newMat = Storage.COMPATIBILITY_ADAPTER.PRISMARINE_SLABS.getNext(original);
		} else if (Storage.COMPATIBILITY_ADAPTER.COBBLESTONE_WALLS.contains(original)) {
			newMat = Storage.COMPATIBILITY_ADAPTER.COBBLESTONE_WALLS.getNext(original);
		} else if (Storage.COMPATIBILITY_ADAPTER.CORAL_BLOCKS.contains(original)) {
			newMat = Storage.COMPATIBILITY_ADAPTER.CORAL_BLOCKS.getNext(original);
		} else if (Storage.COMPATIBILITY_ADAPTER.DEAD_CORAL_BLOCKS.contains(original)) {
			newMat = Storage.COMPATIBILITY_ADAPTER.DEAD_CORAL_BLOCKS.getNext(original);
		} else if (Storage.COMPATIBILITY_ADAPTER.DEAD_CORALS.contains(original)) {
			newMat = Storage.COMPATIBILITY_ADAPTER.DEAD_CORALS.getNext(original);
		}else if (Storage.COMPATIBILITY_ADAPTER.CORALS.contains(original)) {
			newMat = Storage.COMPATIBILITY_ADAPTER.CORALS.getNext(original);
		} else if (Storage.COMPATIBILITY_ADAPTER.CORAL_FANS.contains(original)) {
			newMat = Storage.COMPATIBILITY_ADAPTER.CORAL_FANS.getNext(original);
		} else if (Storage.COMPATIBILITY_ADAPTER.DEAD_CORAL_FANS.contains(original)) {
			newMat = Storage.COMPATIBILITY_ADAPTER.DEAD_CORAL_FANS.getNext(original);
		} else if (Storage.COMPATIBILITY_ADAPTER.DEAD_CORAL_WALL_FANS.contains(original)) {
			newMat = Storage.COMPATIBILITY_ADAPTER.DEAD_CORAL_WALL_FANS.getNext(original);
		} else if (Storage.COMPATIBILITY_ADAPTER.MUSHROOMS.contains(original)) {
			newMat = Storage.COMPATIBILITY_ADAPTER.MUSHROOMS.getNext(original);
		} else if (Storage.COMPATIBILITY_ADAPTER.MUSHROOM_BLOCKS.contains(original)) {
			newMat = Storage.COMPATIBILITY_ADAPTER.MUSHROOM_BLOCKS.getNext(original);
		} else if (Storage.COMPATIBILITY_ADAPTER.MUSHROOM_BLOCKS.contains(original)) {
			newMat = Storage.COMPATIBILITY_ADAPTER.MUSHROOM_BLOCKS.getNext(original);
		} else if (Storage.COMPATIBILITY_ADAPTER.SHORT_GRASSES.contains(original)) {
			newMat = Storage.COMPATIBILITY_ADAPTER.SHORT_GRASSES.getNext(original);
		} else if (Storage.COMPATIBILITY_ADAPTER.LARGE_FLOWERS.contains(original)) {
			newMat = Storage.COMPATIBILITY_ADAPTER.LARGE_FLOWERS.getNext(original);
		}

		if (!newMat.equals(original)) {
			Utilities.damageTool(evt.getPlayer(), 1, usedHand);
			BlockData blockData = evt.getClickedBlock().getBlockData();
			evt.getClickedBlock().setType(newMat, true);

			if (blockData instanceof Orientable){
				Orientable newBlockData = (Orientable) evt.getClickedBlock().getBlockData();
				newBlockData.setAxis(((Orientable) blockData).getAxis());
				evt.getClickedBlock().setBlockData(newBlockData, true);
			}
			if (blockData instanceof Stairs){
				Stairs newBlockData = (Stairs) evt.getClickedBlock().getBlockData();
				newBlockData.setShape(((Stairs) blockData).getShape());
				evt.getClickedBlock().setBlockData(newBlockData, true);
			}
			if (blockData instanceof Bisected){
				Bukkit.createBlockData(newMat);
				/*Bisected newBlockData = (Bisected) evt.getClickedBlock().getBlockData();
				newBlockData.setHalf(((Bisected) blockData).getHalf());
				evt.getClickedBlock().setBlockData(newBlockData, true);

				if (evt.getClickedBlock().getRelative(BlockFace.UP).getType().equals(original)) {
					BlockData blockDataUp = evt.getClickedBlock().getRelative(BlockFace.UP).getBlockData();
					evt.getClickedBlock().getRelative(BlockFace.UP).setType(newMat, true);
					Bisected newBlockDataUp = (Bisected) evt.getClickedBlock().getRelative(BlockFace.UP).getBlockData();
					newBlockDataUp.setHalf(((Bisected) blockDataUp).getHalf());
					evt.getClickedBlock().getRelative(BlockFace.UP).setBlockData(newBlockData, true);
				}

				if (evt.getClickedBlock().getRelative(BlockFace.DOWN).getType().equals(original)) {
					BlockData blockDataUp = evt.getClickedBlock().getRelative(BlockFace.DOWN).getBlockData();
					evt.getClickedBlock().getRelative(BlockFace.DOWN).setType(newMat, true);
					Bisected newBlockDataUp = (Bisected) evt.getClickedBlock().getRelative(BlockFace.DOWN).getBlockData();
					newBlockDataUp.setHalf(((Bisected) blockDataUp).getHalf());
					evt.getClickedBlock().getRelative(BlockFace.DOWN).setBlockData(newBlockData, true);
				}*/
			}
			if (blockData instanceof Slab){
				Slab newBlockData = (Slab) evt.getClickedBlock().getBlockData();
				newBlockData.setType(((Slab) blockData).getType());
				evt.getClickedBlock().setBlockData(newBlockData, true);
			}
			if (blockData instanceof MultipleFacing){
				MultipleFacing newBlockData = (MultipleFacing) evt.getClickedBlock().getBlockData();
				for (BlockFace bf: ((MultipleFacing)blockData).getFaces()) {
					newBlockData.setFace(bf, true);
				}
				evt.getClickedBlock().setBlockData(newBlockData, true);
			}
			if (blockData instanceof Directional){
				Directional newBlockData = (Directional) evt.getClickedBlock().getBlockData();
				newBlockData.setFacing(((Directional) blockData).getFacing());
				evt.getClickedBlock().setBlockData(newBlockData, true);
			}
			if (blockData instanceof Waterlogged){
				Waterlogged newBlockData = (Waterlogged) evt.getClickedBlock().getBlockData();
				newBlockData.setWaterlogged(((Waterlogged) blockData).isWaterlogged());
				evt.getClickedBlock().setBlockData(newBlockData, true);
			}
			return true;
		}
		return false;
	}

}
