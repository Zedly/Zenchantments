package zedly.zenchantments.enchantments;

import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import zedly.zenchantments.Zenchantment;
import zedly.zenchantments.Utilities;
import zedly.zenchantments.Hand;
import zedly.zenchantments.Tool;

import static org.bukkit.potion.PotionEffectType.SLOW;
import static zedly.zenchantments.Tool.SWORD;

public class IceAspect extends Zenchantment {

	public static final int ID = 29;

	@Override
	public Builder<IceAspect> defaults() {
		return new Builder<>(IceAspect::new, ID)
			.maxLevel(2)
			.name("Ice Aspect")
			.probability(0)
			.enchantable(new Tool[]{SWORD})
			.conflicting(new Class[]{})
			.description("Temporarily freezes the target")
			.cooldown(0)
			.power(1.0)
			.handUse(Hand.LEFT);
	}

	@Override
	public boolean onEntityHit(EntityDamageByEntityEvent event, int level, boolean usedHand) {
		Utilities.addPotion((LivingEntity) event.getEntity(), SLOW,
			(int) Math.round(40 + level * power * 40), (int) Math.round(power * level * 2));
		Utilities.display(Utilities.getCenter(event.getEntity().getLocation()), Particle.CLOUD, 10, .1f, 1f, 2f, 1f);
		return true;
	}
}
