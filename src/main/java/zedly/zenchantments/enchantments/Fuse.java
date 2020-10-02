package zedly.zenchantments.enchantments;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;
import zedly.zenchantments.Zenchantment;
import zedly.zenchantments.arrows.EnchantedArrow;
import zedly.zenchantments.arrows.enchanted.FuseArrow;
import zedly.zenchantments.Hand;
import zedly.zenchantments.Tool;

import static zedly.zenchantments.Tool.BOW;

public class Fuse extends Zenchantment {

	public static final int ID = 18;

	@Override
	public Builder<Fuse> defaults() {
		return new Builder<>(Fuse::new, ID)
			.maxLevel(1)
			.loreName("Fuse")
			.probability(0)
			.enchantable(new Tool[]{BOW})
			.conflicting(new Class[]{})
			.description("Instantly ignites anything explosive")
			.cooldown(0)
			.power(-1.0)
			.handUse(Hand.RIGHT);
	}

	@Override
	public boolean onEntityShootBow(EntityShootBowEvent event, int level, boolean usedHand) {
		FuseArrow arrow = new FuseArrow((Arrow) event.getProjectile());
		EnchantedArrow.putArrow((Arrow) event.getProjectile(), arrow, (Player) event.getEntity());
		return true;
	}

}
