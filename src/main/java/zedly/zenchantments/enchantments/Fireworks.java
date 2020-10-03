package zedly.zenchantments.enchantments;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;
import zedly.zenchantments.Zenchantment;
import zedly.zenchantments.arrows.EnchantedArrow;
import zedly.zenchantments.arrows.enchanted.FireworkArrow;
import zedly.zenchantments.Hand;
import zedly.zenchantments.Tool;

import static zedly.zenchantments.Tool.BOW;

public class Fireworks extends Zenchantment {

	public static final int ID = 15;

	@Override
	public Builder<Fireworks> defaults() {
		return new Builder<>(Fireworks::new, ID)
			.maxLevel(4)
			.name("Fireworks")
			.probability(0)
			.enchantable(new Tool[]{BOW})
			.conflicting(new Class[]{})
			.description("Shoots arrows that burst into fireworks upon impact")
			.cooldown(0)
			.power(-1.0)
			.handUse(Hand.RIGHT);
	}

	@Override
	public boolean onEntityShootBow(EntityShootBowEvent event, int level, boolean usedHand) {
		FireworkArrow arrow = new FireworkArrow((Arrow) event.getProjectile(), level);
		EnchantedArrow.putArrow((Arrow) event.getProjectile(), arrow, (Player) event.getEntity());
		return true;
	}

}
