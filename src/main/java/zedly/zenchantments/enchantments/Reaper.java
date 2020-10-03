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
import zedly.zenchantments.Hand;
import zedly.zenchantments.Tool;

import static org.bukkit.potion.PotionEffectType.BLINDNESS;
import static zedly.zenchantments.Tool.BOW;
import static zedly.zenchantments.Tool.SWORD;

public class Reaper extends Zenchantment {

	public static final int ID = 49;

	@Override
	public Builder<Reaper> defaults() {
		return new Builder<>(Reaper::new, ID)
			.maxLevel(4)
			.name("Reaper")
			.probability(0)
			.enchantable(new Tool[]{BOW, SWORD})
			.conflicting(new Class[]{})
			.description("Gives the target temporary wither effect and blindness")
			.cooldown(0)
			.power(1.0)
			.handUse(Hand.BOTH);
	}

	@Override
	public boolean onEntityShootBow(EntityShootBowEvent event, int level, boolean usedHand) {
		ReaperArrow arrow = new ReaperArrow((Arrow) event.getProjectile(), level, power);
		EnchantedArrow.putArrow((Arrow) event.getProjectile(), arrow, (Player) event.getEntity());
		return true;
	}

	@Override
	public boolean onEntityHit(EntityDamageByEntityEvent event, int level, boolean usedHand) {
		if (event.getEntity() instanceof LivingEntity &&
			ADAPTER.attackEntity((LivingEntity) event.getEntity(), (Player) event.getDamager(), 0)) {
			int pow = (int) Math.round(level * power);
			int dur = (int) Math.round(10 + level * 20 * power);
			Utilities.addPotion((LivingEntity) event.getEntity(), PotionEffectType.WITHER, dur, pow);
			Utilities.addPotion((LivingEntity) event.getEntity(), BLINDNESS, dur, pow);
		}
		return true;
	}
}
