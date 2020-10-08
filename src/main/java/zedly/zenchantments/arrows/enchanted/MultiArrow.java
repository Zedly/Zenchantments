package zedly.zenchantments.arrows.enchanted;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.Storage;
import zedly.zenchantments.arrows.EnchantedArrow;

public class MultiArrow extends EnchantedArrow {

	public MultiArrow(Arrow entity) {
		super(entity);
	}

	public boolean onImpact(@NotNull EntityDamageByEntityEvent event) {
		final LivingEntity e = (LivingEntity) event.getEntity();
		int temp = e.getMaximumNoDamageTicks();
		e.setMaximumNoDamageTicks(0);
		e.setNoDamageTicks(0);
		e.setMaximumNoDamageTicks(temp);
		die();
		return true;
	}

	public void onImpact() {
		Arrow p = arrow.getWorld().spawnArrow(arrow.getLocation(), arrow.getVelocity(),
			(float) (arrow.getVelocity().length() / 10), 0);
		p.setFireTicks(arrow.getFireTicks());
		p.getLocation().setDirection(arrow.getLocation().getDirection());
		p.setMetadata("ze.arrow", new FixedMetadataValue(Storage.zenchantments, null));
		this.arrow.remove();
	}
}
