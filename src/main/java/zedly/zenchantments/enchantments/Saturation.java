package zedly.zenchantments.enchantments;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import zedly.zenchantments.Zenchantment;
import zedly.zenchantments.Storage;
import zedly.zenchantments.Hand;
import zedly.zenchantments.Tool;

import static zedly.zenchantments.Tool.LEGGINGS;

public class Saturation extends Zenchantment {

	public static final int ID = 50;

	@Override
	public Builder<Saturation> defaults() {
		return new Builder<>(Saturation::new, ID)
			.maxLevel(3)
			.loreName("Saturation")
			.probability(0)
			.enchantable(new Tool[]{LEGGINGS})
			.conflicting(new Class[]{})
			.description("Uses less of the player's hunger")
			.cooldown(0)
			.power(1.0)

			.handUse(Hand.NONE);
	}

	@Override
	public boolean onHungerChange(FoodLevelChangeEvent event, int level, boolean usedHand) {
		if (event.getFoodLevel() < ((Player) event.getEntity()).getFoodLevel() &&
			Storage.rnd.nextInt(10) > 10 - 2 * level * power) {
			event.setCancelled(true);
		}
		return true;
	}
}
