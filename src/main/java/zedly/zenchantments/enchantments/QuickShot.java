package zedly.zenchantments.enchantments;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;
import zedly.zenchantments.Zenchantment;
import zedly.zenchantments.arrows.EnchantedArrow;
import zedly.zenchantments.arrows.enchanted.QuickArrow;
import zedly.zenchantments.Hand;
import zedly.zenchantments.Tool;

import static zedly.zenchantments.Tool.BOW;

public class QuickShot extends Zenchantment {

	public static final int ID = 46;

	@Override
	public Builder<QuickShot> defaults() {
		return new Builder<>(QuickShot::new, ID)
			.maxLevel(1)
			.loreName("Quick Shot")
			.probability(0)
			.enchantable(new Tool[]{BOW})
			.conflicting(new Class[]{})
			.description("Shoots arrows at full speed, instantly")
			.cooldown(0)
			.power(1.0)
			.handUse(Hand.RIGHT);
	}

	@Override
	public boolean onEntityShootBow(EntityShootBowEvent evt, int level, boolean usedHand) {
		QuickArrow arrow = new QuickArrow((Arrow) evt.getProjectile());
		EnchantedArrow.putArrow((Arrow) evt.getProjectile(), arrow, (Player) evt.getEntity());
		return true;
	}
}
