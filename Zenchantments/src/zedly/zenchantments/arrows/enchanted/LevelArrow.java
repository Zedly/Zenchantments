package zedly.zenchantments.arrows.enchanted;

import org.bukkit.entity.Arrow;
import org.bukkit.event.entity.EntityDeathEvent;
import zedly.zenchantments.arrows.EnchantedArrow;

public class LevelArrow extends EnchantedArrow {

	public LevelArrow(Arrow entity, int level, double power) {
		super(entity, level, power);
	}

	public void onKill(EntityDeathEvent evt) {
		evt.setDroppedExp((int) (evt.getDroppedExp() * (1.3 + (getLevel() * getPower() * .5))));
		die();
	}
}
