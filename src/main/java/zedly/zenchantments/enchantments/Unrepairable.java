package zedly.zenchantments.enchantments;

import zedly.zenchantments.Zenchantment;
import zedly.zenchantments.Hand;
import zedly.zenchantments.Tool;

import static zedly.zenchantments.Tool.ALL;

public class Unrepairable extends Zenchantment {

	public static final int ID = 73;

	@Override
	public Builder<Unrepairable> defaults() {
		return new Builder<>(Unrepairable::new, ID)
			.maxLevel(1)
			.name("Unrepairable")
			.probability(0)
			.enchantable(new Tool[]{ALL})
			.conflicting(new Class[]{})
			.description("Prevents an item from being repaired")
			.cooldown(0)
			.power(-1.0)
			.handUse(Hand.NONE);
	}
}
