package zedly.zenchantments.arrows.enchanted;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import zedly.zenchantments.arrows.EnchantedArrow;

import java.util.List;

public class QuickArrow extends EnchantedArrow {

	public QuickArrow(Arrow entity) {
		super(entity);
	}

	public void onLaunch(LivingEntity player, List<String> lore) {
		arrow.setVelocity(arrow.getVelocity().normalize().multiply(3.5f));
	}

	public void onImpact() {
	}

}
