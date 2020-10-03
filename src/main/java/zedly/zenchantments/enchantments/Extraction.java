package zedly.zenchantments.enchantments;

import org.bukkit.GameMode;
import org.bukkit.Particle;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import zedly.zenchantments.Zenchantment;
import zedly.zenchantments.Storage;
import zedly.zenchantments.Utilities;
import zedly.zenchantments.Hand;
import zedly.zenchantments.Tool;

import static org.bukkit.Material.*;
import static org.bukkit.entity.EntityType.EXPERIENCE_ORB;
import static zedly.zenchantments.Tool.PICKAXE;

public class Extraction extends Zenchantment {

	public static final int ID = 12;

	@Override
	public Builder<Extraction> defaults() {
		return new Builder<>(Extraction::new, ID)
			.maxLevel(3)
			.name("Extraction")
			.probability(0)
			.enchantable(new Tool[]{PICKAXE})
			.conflicting(new Class[]{Switch.class})
			.description("Smelts and yields more product from ores")
			.cooldown(0)
			.power(1.0)
			.handUse(Hand.LEFT);
	}

	@Override
	public boolean onBlockBreak(BlockBreakEvent event, final int level, boolean usedHand) {
		if (event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
			return false;
		}
		if (event.getBlock().getType() == GOLD_ORE || event.getBlock().getType() == IRON_ORE) {
			Utilities.damageTool(event.getPlayer(), 1, usedHand);
			for (int x = 0; x < Storage.rnd.nextInt((int) Math.round(power * level + 1)) + 1; x++) {
				event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(),
					new ItemStack(event.getBlock().getType() == GOLD_ORE ?
						GOLD_INGOT : IRON_INGOT));
			}
			ExperienceOrb o = (ExperienceOrb) event.getBlock().getWorld()
			                                     .spawnEntity(event.getBlock().getLocation(), EXPERIENCE_ORB);
			o.setExperience(
				event.getBlock().getType() == IRON_ORE ? Storage.rnd.nextInt(5) + 1 : Storage.rnd.nextInt(5) + 3);
			event.getBlock().setType(AIR);
			Utilities.display(event.getBlock().getLocation(), Particle.FLAME, 10, .1f, .5f, .5f, .5f);
			return true;
		}
		return false;
	}
}
