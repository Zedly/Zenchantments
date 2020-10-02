package zedly.zenchantments.enchantments;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.potion.PotionEffectType;
import zedly.zenchantments.Zenchantment;
import zedly.zenchantments.arrows.EnchantedArrow;
import zedly.zenchantments.Utilities;
import zedly.zenchantments.arrows.enchanted.ReaperArrow;
import zedly.zenchantments.enums.Hand;
import zedly.zenchantments.enums.Tool;

import static org.bukkit.potion.PotionEffectType.BLINDNESS;
import static zedly.zenchantments.enums.Tool.BOW;
import static zedly.zenchantments.enums.Tool.SWORD;

public class Reaper extends Zenchantment {

	public static final int ID = 49;

	@Override
	public Builder<Reaper> defaults() {
		return new Builder<>(Reaper::new, ID)
			.maxLevel(4)
			.loreName("Reaper")
			.probability(0)
			.enchantable(new Tool[]{BOW, SWORD})
			.conflicting(new Class[]{})
			.description("Gives the target temporary wither effect and blindness")
			.cooldown(0)
			.power(1.0)
			.handUse(Hand.BOTH);
	}

	@Override
	public boolean onEntityShootBow(EntityShootBowEvent evt, int level, boolean usedHand) {
		ReaperArrow arrow = new ReaperArrow((Arrow) evt.getProjectile(), level, power);
		EnchantedArrow.putArrow((Arrow) evt.getProjectile(), arrow, (Player) evt.getEntity());
		return true;
	}

	@Override
	public boolean onEntityHit(EntityDamageByEntityEvent evt, int level, boolean usedHand) {
		if (evt.getEntity() instanceof LivingEntity &&
			ADAPTER.attackEntity((LivingEntity) evt.getEntity(), (Player) evt.getDamager(), 0)) {
			int pow = (int) Math.round(level * power);
			int dur = (int) Math.round(10 + level * 20 * power);
			Utilities.addPotion((LivingEntity) evt.getEntity(), PotionEffectType.WITHER, dur, pow);
			Utilities.addPotion((LivingEntity) evt.getEntity(), BLINDNESS, dur, pow);
		}
		return true;
	}
}
