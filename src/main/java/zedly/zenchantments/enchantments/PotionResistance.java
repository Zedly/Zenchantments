package zedly.zenchantments.enchantments;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.inventory.ItemStack;
import zedly.zenchantments.Zenchantment;
import zedly.zenchantments.enums.Hand;
import zedly.zenchantments.enums.Tool;

import java.util.Map;

import static zedly.zenchantments.enums.Tool.*;

public class PotionResistance extends Zenchantment {

	public static final int ID = 45;

	@Override
	public Builder<PotionResistance> defaults() {
		return new Builder<>(PotionResistance::new, ID)
			.maxLevel(4)
			.loreName("Potion Resistance")
			.probability(0)
			.enchantable(new Tool[]{HELMET, CHESTPLATE, LEGGINGS, BOOTS})
			.conflicting(new Class[]{})
			.description("Lessens the effects of all potions on players")
			.cooldown(0)
			.power(1.0)
			.handUse(Hand.NONE);
	}

	@Override
	public boolean onPotionSplash(PotionSplashEvent evt, int level, boolean usedHand) {
		for (LivingEntity ent : evt.getAffectedEntities()) {
			if (ent instanceof Player) {
				int effect = 0;
				for (ItemStack stk : ((Player) ent).getInventory().getArmorContents()) {
					Map<Zenchantment, Integer> map = Zenchantment.getEnchants(stk, ent.getWorld());
					for (Zenchantment e : map.keySet()) {
						if (e.equals(this)) {
							effect += map.get(e);
						}
					}
				}
				evt.setIntensity(ent, evt.getIntensity(ent) / ((effect * power + 1.3) / 2));
			}
		}
		return true;
	}
}
