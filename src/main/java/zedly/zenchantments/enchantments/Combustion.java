package zedly.zenchantments.enchantments;

import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import zedly.zenchantments.Zenchantment;
import zedly.zenchantments.Hand;
import zedly.zenchantments.Tool;

import static zedly.zenchantments.Tool.CHESTPLATE;

public class Combustion extends Zenchantment {

	public static final int ID = 9;

	@Override
	public Builder<Combustion> defaults() {
		return new Builder<>(Combustion::new, ID)
			.maxLevel(4)
			.name("Combustion")
			.probability(0)
			.enchantable(new Tool[]{CHESTPLATE})
			.conflicting(new Class[]{})
			.description("Lights attacking entities on fire when player is attacked")
			.cooldown(0)
			.power(1.0)
			.handUse(Hand.NONE);
	}

	@Override
	public boolean onBeingHit(EntityDamageByEntityEvent event, int level, boolean usedHand) {
		Entity ent;
		if (event.getDamager().getType() == EntityType.ARROW) {
			Arrow arrow = (Arrow) event.getDamager();
			if (arrow.getShooter() instanceof LivingEntity) {
				ent = (Entity) arrow.getShooter();
			} else {
				return false;
			}
		} else {
			ent = event.getDamager();
		}
		return ADAPTER.igniteEntity(ent, (Player) event.getEntity(), (int) (50 * level * power));
	}

	public boolean onCombust(EntityCombustByEntityEvent event, int level, boolean usedHand) {
		if (ADAPTER.isZombie(event.getCombuster())) {
			event.setDuration(0);
		}
		return false;
	}
}
