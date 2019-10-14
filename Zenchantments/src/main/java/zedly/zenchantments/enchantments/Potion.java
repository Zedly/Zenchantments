package zedly.zenchantments.enchantments;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.potion.PotionEffectType;
import zedly.zenchantments.CustomEnchantment;
import zedly.zenchantments.arrows.EnchantedArrow;
import zedly.zenchantments.arrows.enchanted.PotionArrow;
import zedly.zenchantments.enums.Hand;
import zedly.zenchantments.enums.Tool;

import static zedly.zenchantments.enums.Tool.BOW;

public class Potion extends CustomEnchantment {

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
	public boolean onEntityShootBow(EntityShootBowEvent evt, int level, boolean usedHand) {
		PotionArrow arrow = new PotionArrow((Arrow) evt.getProjectile(), level, power);
		EnchantedArrow.putArrow((Arrow) evt.getProjectile(), arrow, (Player) evt.getEntity());
		return true;
	}
}
