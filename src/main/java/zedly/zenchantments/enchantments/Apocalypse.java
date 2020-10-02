package zedly.zenchantments.enchantments;

import org.bukkit.entity.Player;
import org.bukkit.entity.Arrow;
import org.bukkit.event.entity.EntityShootBowEvent;
import zedly.zenchantments.Zenchantment;
import zedly.zenchantments.arrows.EnchantedArrow;
import zedly.zenchantments.arrows.admin.ApocalypseArrow;
import zedly.zenchantments.Hand;
import zedly.zenchantments.Tool;

import static zedly.zenchantments.Tool.BOW;

public class Apocalypse extends Zenchantment {

	public static final int ID = 69;

	@Override
	public Builder<Apocalypse> defaults() {
		return new Builder<>(Apocalypse::new, ID)
			.maxLevel(1)
			.loreName("Apocalypse")
			.probability(0)
			.enchantable(new Tool[]{BOW})
			.conflicting(new Class[]{})
			.description("Unleashes hell")
			.cooldown(0)
			.power(-1.0)
			.handUse(Hand.RIGHT);
	}

	@Override
	public boolean onEntityShootBow(EntityShootBowEvent evt, int level, boolean usedHand) {
		ApocalypseArrow arrow = new ApocalypseArrow((Arrow) evt.getProjectile());
		EnchantedArrow.putArrow((Arrow) evt.getProjectile(), arrow, (Player) evt.getEntity());
		return true;
	}
}
