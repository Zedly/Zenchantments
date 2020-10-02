package zedly.zenchantments.enchantments;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.potion.PotionEffectType;
import zedly.zenchantments.Zenchantment;
import zedly.zenchantments.arrows.EnchantedArrow;
import zedly.zenchantments.arrows.enchanted.PotionArrow;
import zedly.zenchantments.Hand;
import zedly.zenchantments.Tool;

import static zedly.zenchantments.Tool.BOW;

public class Potion extends Zenchantment {

	public static final int ID = 44;
	PotionEffectType[] potions;

	@Override
	public Builder<Potion> defaults() {
		return new Builder<>(Potion::new, ID)
			.maxLevel(3)
			.loreName("Potion")
			.probability(0)
			.enchantable(new Tool[]{BOW})
			.conflicting(new Class[]{})
			.description("Gives the shooter random positive potion effects when attacking")
			.cooldown(0)
			.power(1.0)
			.handUse(Hand.RIGHT);
	}

	@Override
	public boolean onEntityShootBow(EntityShootBowEvent event, int level, boolean usedHand) {
		PotionArrow arrow = new PotionArrow((Arrow) event.getProjectile(), level, power);
		EnchantedArrow.putArrow((Arrow) event.getProjectile(), arrow, (Player) event.getEntity());
		return true;
	}
}
