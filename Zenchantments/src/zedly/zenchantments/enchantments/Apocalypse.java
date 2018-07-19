package zedly.zenchantments.enchantments;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.entity.Arrow;
import org.bukkit.event.entity.EntityShootBowEvent;
import zedly.zenchantments.CustomEnchantment;
import zedly.zenchantments.arrows.admin.ApocalypseArrow;
import zedly.zenchantments.Utilities;
import zedly.zenchantments.enums.Hand;
import zedly.zenchantments.enums.Tool;

import static zedly.zenchantments.enums.Tool.BOW;

public class Apocalypse extends CustomEnchantment {
	@Override
	public Builder<Apocalypse> defaults() {
		return new Builder<>(Apocalypse::new, 69)
			.maxLevel(1)
			.loreName("Apocalypse")
			.probability(0)
			.enchantable(new Tool[]{BOW})
			.conflicting(new Class[]{})
			.description("Unleashes hell")
			.cooldown(0)
			.power(-1.0)
			.handUse(Hand.RIGHT);
    }

	@Override
	public boolean onEntityShootBow(EntityShootBowEvent evt, int level, boolean usedHand) {
		ApocalypseArrow arrow = new ApocalypseArrow((Arrow) evt.getProjectile());
		Utilities.putArrow((Arrow) evt.getProjectile(), arrow, (Player) evt.getEntity());
		return true;
	}
}
