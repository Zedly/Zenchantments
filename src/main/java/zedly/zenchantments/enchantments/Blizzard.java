package zedly.zenchantments.enchantments;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;
import zedly.zenchantments.Zenchantment;
import zedly.zenchantments.arrows.EnchantedArrow;
import zedly.zenchantments.arrows.enchanted.BlizzardArrow;
import zedly.zenchantments.Hand;
import zedly.zenchantments.Tool;

import static zedly.zenchantments.Tool.BOW;

public class Blizzard extends Zenchantment {

	public static final int ID = 6;

	@Override
	public Builder<Blizzard> defaults() {
		return new Builder<>(Blizzard::new, ID)
			.maxLevel(3)
			.name("Blizzard")
			.probability(0)
			.enchantable(new Tool[]{BOW})
			.conflicting(new Class[]{Firestorm.class})
			.description("Spawns a blizzard where the arrow strikes freezing nearby entities")
			.cooldown(0)
			.power(1.0)
			.handUse(Hand.RIGHT);
	}

	@Override
	public boolean onEntityShootBow(EntityShootBowEvent event, int level, boolean usedHand) {
		BlizzardArrow arrow = new BlizzardArrow((Arrow) event.getProjectile(), level, power);
		EnchantedArrow.putArrow((Arrow) event.getProjectile(), arrow, (Player) event.getEntity());
		return true;
	}
}
