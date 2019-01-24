package zedly.zenchantments.enchantments;

import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import zedly.zenchantments.CustomEnchantment;
import zedly.zenchantments.enums.Hand;
import zedly.zenchantments.enums.Tool;

import static zedly.zenchantments.enums.Tool.ROD;

public class LongCast extends CustomEnchantment {

	public static final int ID = 33;

	@Override
	public Builder<LongCast> defaults() {
		return new Builder<>(LongCast::new, ID)
			.maxLevel(2)
			.loreName("Long Cast")
			.probability(0)
			.enchantable(new Tool[]{ROD})
			.conflicting(new Class[]{ShortCast.class})
			.description("Launches fishing hooks farther out when casting")
			.cooldown(0)
			.power(1.0)
			.handUse(Hand.RIGHT);
	}

	@Override
	public boolean onProjectileLaunch(ProjectileLaunchEvent evt, int level, boolean usedHand) {
		if (evt.getEntity().getType() == EntityType.FISHING_HOOK) {
			evt.getEntity().setVelocity(
				evt.getEntity().getVelocity().normalize().multiply(Math.min(1.9 + (power * level - 1.2), 2.7)));
		}
		return true;
	}
}
