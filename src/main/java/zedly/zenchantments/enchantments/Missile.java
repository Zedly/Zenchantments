package zedly.zenchantments.enchantments;

import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;
import zedly.zenchantments.Zenchantment;
import zedly.zenchantments.Utilities;
import zedly.zenchantments.arrows.EnchantedArrow;
import zedly.zenchantments.arrows.admin.MissileArrow;
import zedly.zenchantments.Hand;
import zedly.zenchantments.Tool;

import static zedly.zenchantments.Tool.BOW;

public class Missile extends Zenchantment {

	public static final int ID = 71;

	@Override
	public Builder<Missile> defaults() {
		return new Builder<>(Missile::new, ID)
			.maxLevel(1)
			.name("Missile")
			.probability(0)
			.enchantable(new Tool[]{BOW})
			.conflicting(new Class[]{})
			.description("Shoots a missile from the bow")
			.cooldown(0)
			.power(-1.0)
			.handUse(Hand.RIGHT);
	}

	@Override
	public boolean onEntityShootBow(EntityShootBowEvent event, int level, boolean usedHand) {
		MissileArrow arrow = new MissileArrow((Arrow) event.getProjectile());
		EnchantedArrow.putArrow((Arrow) event.getProjectile(), arrow, (Player) event.getEntity());
		event.setCancelled(true);
		Utilities.damageTool((Player) event.getEntity(), 1, usedHand);
		Utilities.removeItem(((Player) event.getEntity()), Material.ARROW, 1);
		return true;
	}
}
