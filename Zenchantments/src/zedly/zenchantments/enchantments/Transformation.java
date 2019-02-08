package zedly.zenchantments.enchantments;

import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import zedly.zenchantments.CustomEnchantment;
import zedly.zenchantments.Storage;
import zedly.zenchantments.Utilities;
import zedly.zenchantments.enums.Hand;
import zedly.zenchantments.enums.Tool;

import static zedly.zenchantments.enums.Tool.SWORD;

public class Transformation extends CustomEnchantment {

	public static final int ID = 64;

	@Override
	public Builder<Transformation> defaults() {
		return new Builder<>(Transformation::new, ID)
			.maxLevel(3)
			.loreName("Transformation")
			.probability(0)
			.enchantable(new Tool[]{SWORD})
			.conflicting(new Class[]{})
			.description("Occasionally causes the attacked mob to be transformed into its similar cousin")
			.cooldown(0)
			.power(1.0)
			.handUse(Hand.LEFT);
	}

	@Override
	public boolean onEntityHit(EntityDamageByEntityEvent evt, int level, boolean usedHand) {
		if (evt.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
			return false;
		}
		if (evt.getEntity() instanceof LivingEntity &&
			ADAPTER.attackEntity((LivingEntity) evt.getEntity(), (Player) evt.getDamager(), 0)) {
			if (Storage.rnd.nextInt(100) > (100 - (level * power * 8))) {
				int position =
					Storage.COMPATIBILITY_ADAPTER.TransformationEntityTypes().indexOf(evt.getEntity().getType());
				if (position != -1) {
					if (evt.getDamage() > ((LivingEntity) evt.getEntity()).getHealth()) {
						evt.setCancelled(true);
					}
					int newPosition = position + 1 - 2 * (position % 2);
					Utilities.display(Utilities.getCenter(evt.getEntity().getLocation()), Particle.HEART, 70, .1f,
						.5f, 2, .5f);

					double originalHealth = ((LivingEntity) evt.getEntity()).getHealth();
					evt.getEntity().remove();

					LivingEntity ent =
						(LivingEntity) (evt.getDamager()).getWorld().spawnEntity(evt.getEntity().getLocation(),
							Storage.COMPATIBILITY_ADAPTER.TransformationEntityTypes().get(newPosition));

					ent.setHealth(Math.max(1,
						Math.min(originalHealth, ent.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue())));

				}
			}
		}
		return true;
	}
}
