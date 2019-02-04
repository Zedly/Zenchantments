package zedly.zenchantments.enchantments;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import zedly.zenchantments.CustomEnchantment;
import zedly.zenchantments.Storage;
import zedly.zenchantments.Utilities;
import zedly.zenchantments.enums.Hand;
import zedly.zenchantments.enums.Tool;

import static org.bukkit.Material.*;
import static org.bukkit.entity.EntityType.EXPERIENCE_ORB;
import static zedly.zenchantments.enums.Tool.*;

public class Fire extends CustomEnchantment {

	public static final int ID = 13;

	@Override
	public Builder<Fire> defaults() {
		return new Builder<>(Fire::new, ID)
			.maxLevel(1)
			.loreName("Fire")
			.probability(0)
			.enchantable(new Tool[]{PICKAXE, AXE, SHOVEL})
			.conflicting(new Class[]{Switch.class, Variety.class})
			.description("Drops the smelted version of the block broken")
			.cooldown(0)
			.power(-1.0)
			.handUse(Hand.LEFT);
	}

	@Override
	public boolean onBlockBreak(BlockBreakEvent evt, int level, boolean usedHand) {
		if (evt.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
			return false;
		}

		ItemStack hand = Utilities.usedStack(evt.getPlayer(), usedHand);
		Material original = evt.getBlock().getType();
		Material mat = AIR;
		if (Tool.PICKAXE.contains(hand)) {
			if (Storage.COMPATIBILITY_ADAPTER.FireRaw().contains(original)) {
				mat = Storage.COMPATIBILITY_ADAPTER.FireCooked().get(
					Storage.COMPATIBILITY_ADAPTER.FireRaw().indexOf(original));
			}
			if (original == GOLD_ORE || original == IRON_ORE) {
				ExperienceOrb o =
					(ExperienceOrb) evt.getBlock().getWorld().spawnEntity(Utilities.getCenter(evt.getBlock()),
						EXPERIENCE_ORB);
				o.setExperience(original == IRON_ORE ? Storage.rnd.nextInt(5) + 1 : Storage.rnd.nextInt(5) + 3);
			}
		}

		if (original == WET_SPONGE) {
			mat = SPONGE;
		} else if (Storage.COMPATIBILITY_ADAPTER.Sands().contains(original)) {
			mat = GLASS;
		} else if (Storage.COMPATIBILITY_ADAPTER.Logs().contains(original)
			|| Storage.COMPATIBILITY_ADAPTER.StrippedLogs().contains(original)
			|| Storage.COMPATIBILITY_ADAPTER.StrippedWoods().contains(original)
			|| Storage.COMPATIBILITY_ADAPTER.Woods().contains(original)) {
			mat = CHARCOAL;
		} else if (original == CLAY) {
			Utilities.display(Utilities.getCenter(evt.getBlock()), Particle.FLAME, 10, .1f, .5f, .5f, .5f);
			for (int x = 0; x < 4; x++) {
				evt.getBlock().getWorld()
				   .dropItemNaturally(Utilities.getCenter(evt.getBlock()), new ItemStack(BRICK));
			}

			Block affectedBlock = evt.getBlock();
			Grab.fireDropLocs.add(affectedBlock);
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Storage.zenchantments, () -> {
				Grab.fireDropLocs.remove(affectedBlock);
			}, 5);

			return true;
		} else if (original == CACTUS) {
			Location location = evt.getBlock().getLocation().clone();
			double height = location.getY();
			for (double i = location.getY(); i <= 256; i++) {
				location.setY(i);
				if (location.getBlock().getType() == CACTUS) {
					height++;
				} else {
					break;
				}
			}
			for (double i = height - 1; i >= evt.getBlock().getLocation().getY(); i--) {
				location.setY(i);
				Utilities.display(Utilities.getCenter(evt.getBlock()), Particle.FLAME, 10, .1f, .5f, .5f, .5f);

				evt.getBlock().getWorld().dropItemNaturally(Utilities.getCenter(location),
					new ItemStack(CACTUS_GREEN, 1));
				Block affectedBlock = location.getBlock();
				Grab.fireDropLocs.add(affectedBlock);

				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Storage.zenchantments, () -> {
					Grab.fireDropLocs.remove(affectedBlock);
				}, 5);
			}
			return true;
		}
		if (mat != AIR) {
			evt.getBlock().getWorld().dropItemNaturally(Utilities.getCenter(evt.getBlock()), new ItemStack((mat), 1));
			Utilities.display(Utilities.getCenter(evt.getBlock()), Particle.FLAME, 10, .1f, .5f, .5f, .5f);

			Block affectedBlock = evt.getBlock();
			Grab.fireDropLocs.add(affectedBlock);
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Storage.zenchantments, () -> {
				Grab.fireDropLocs.remove(affectedBlock);
			}, 5);

			return true;
		}
		return false;
	}
}
