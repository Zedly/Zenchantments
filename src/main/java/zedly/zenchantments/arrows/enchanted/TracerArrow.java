package zedly.zenchantments.arrows.enchanted;

import org.bukkit.entity.Arrow;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.arrows.EnchantedArrow;
import zedly.zenchantments.enchantments.Tracer;

public class TracerArrow extends EnchantedArrow {

	public TracerArrow(Arrow entity, int level, double power) {
		super(entity, level, power);
		Tracer.tracer.put(entity, (int) Math.round(level * power));
	}

	@Override
	public boolean onImpact(@NotNull EntityDamageByEntityEvent event) {
		if (event.isCancelled()) {
			Tracer.tracer.remove(arrow);
			die();
		}
		return true;
	}
}
