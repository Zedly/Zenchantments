package zedly.zenchantments.arrows.enchanted;

import org.bukkit.entity.Arrow;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import zedly.zenchantments.arrows.EnchantedArrow;
import zedly.zenchantments.enchantments.Tracer;

public class TracerArrow extends EnchantedArrow {

	public TracerArrow(Arrow entity, int level, double power) {
		super(entity, level, power);
		Tracer.tracer.put(entity, (int) Math.round(level * power));
	}

	@Override
	public boolean onImpact(EntityDamageByEntityEvent evt) {
		if (evt.isCancelled()) {
			Tracer.tracer.remove(arrow);
			die();
		}
		return true;
	}
}
