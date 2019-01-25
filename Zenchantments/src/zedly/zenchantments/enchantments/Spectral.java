package zedly.zenchantments.enchantments;

import org.bukkit.Material;
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
		boolean tall = false;
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
		} else if (Storage.COMPATIBILITY_ADAPTER.STRIPPED_WOODS.contains(original)) {
			newMat = Storage.COMPATIBILITY_ADAPTER.STRIPPED_WOODS.getNext(original);
		} else if (Storage.COMPATIBILITY_ADAPTER.PLANKSS.contains(original)) {
			newMat = Storage.COMPATIBILITY_ADAPTER.PLANKSS.getNext(original);
		} else if (Storage.COMPATIBILITY_ADAPTER.SANDS.contains(original)) {
			newMat = Storage.COMPATIBILITY_ADAPTER.SANDS.getNext(original);
		} else if (Storage.COMPATIBILITY_ADAPTER.SAPLINGS.contains(original)) {
			newMat = Storage.COMPATIBILITY_ADAPTER.SAPLINGS.getNext(original);
		} else if (Storage.COMPATIBILITY_ADAPTER.LEAVESS.contains(original)) {
			newMat = Storage.COMPATIBILITY_ADAPTER.LEAVESS.getNext(original);
		} else if (Storage.COMPATIBILITY_ADAPTER.WOOD_FENCES.contains(original)) {
			newMat = Storage.COMPATIBILITY_ADAPTER.WOOD_FENCES.getNext(original);
		} else if (Storage.COMPATIBILITY_ADAPTER.WOOD_STAIRSS.contains(original)) {
			newMat = Storage.COMPATIBILITY_ADAPTER.WOOD_STAIRSS.getNext(original);
		} else if (Storage.COMPATIBILITY_ADAPTER.SMALL_FLOWERS.contains(original)) {
			newMat = Storage.COMPATIBILITY_ADAPTER.SMALL_FLOWERS.getNext(original);
		} else if (Storage.COMPATIBILITY_ADAPTER.LOGS.contains(original)) {
			newMat = Storage.COMPATIBILITY_ADAPTER.LOGS.getNext(original);
		} else if (Storage.COMPATIBILITY_ADAPTER.SANDSTONES.contains(original)) {
			newMat = Storage.COMPATIBILITY_ADAPTER.SANDSTONES.getNext(original);
		} else if (Storage.COMPATIBILITY_ADAPTER.LARGE_FLOWERS.contains(original)) {
			// newMat = Storage.COMPATIBILITY_ADAPTER.LARGE_FLOWERS.getNext(original);
			tall = true;
		}

        /*

        QUARTZ, WALLS, STONE, BRICKS, COBBLESTONES, MUSHROOMS, MUSHROOM_BLOCKS, STAIRS
        SLABS, DOUBLE SLABS, GRASS/DIRT, SHORT_GRASS, TALL_PLANTS, FENCE_GATES, DOORS

        Cycle item orientation for certain things (terracotta, doors, fences, stairs, slabs, etc)

        Update block for glass, fences, etc

        */

        /*
                if (evt.getClickedBlock().getRelative(UP).getType().equals(evt.getClickedBlock().getType())) {
                    evt.getClickedBlock().setTypeIdAndData(type.getId(), (byte) data, false);
                    evt.getClickedBlock().getRelative(UP).setTypeIdAndData(type.getId(), (byte) 8, true);
                } else if(evt.getClickedBlock().getRelative(DOWN).getType()
                             .equals(evt.getClickedBlock().getType())) {
                    evt.getClickedBlock().setTypeIdAndData(type.getId(), (byte) 8, false);
                    evt.getClickedBlock().getRelative(DOWN)
                       .setTypeIdAndData(type.getId(), evt.getClickedBlock().getRelative(DOWN).getData(), true);
                }
        */

		if (!newMat.equals(original)) {
			Utilities.damageTool(evt.getPlayer(), 1, usedHand);
			evt.getClickedBlock().setType(newMat);

			if (tall) {
				//if (evt.getClickedBlock().getRelative(UP).getType().equals(evt.getClickedBlock().getType())) {
				//
				//} else if(evt.getClickedBlock().getRelative(DOWN).getType().equals(evt.getClickedBlock().getType()
                // )) {
				//
				//}
			}
			return true;
		}
		return false;
	}

}
