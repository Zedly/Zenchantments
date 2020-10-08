package zedly.zenchantments.arrows.enchanted;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.Storage;
import zedly.zenchantments.Utilities;
import zedly.zenchantments.arrows.EnchantedArrow;

import static org.bukkit.potion.PotionEffectType.BLINDNESS;

public class ReaperArrow extends EnchantedArrow {

	public ReaperArrow(Arrow entity, int level, double power) {
		super(entity, level, power);
	}

	public boolean onImpact(@NotNull EntityDamageByEntityEvent event) {
		if (Storage.COMPATIBILITY_ADAPTER.attackEntity((LivingEntity) event.getEntity(), (Player) arrow.getShooter(),
			0)) {
			int pow = (int) Math.round(getLevel() * getPower());
			int dur = (int) Math.round(20 + getLevel() * 10 * getPower());
			Utilities.addPotion((LivingEntity) event.getEntity(), PotionEffectType.WITHER, dur, pow);
			Utilities.addPotion((LivingEntity) event.getEntity(), BLINDNESS, dur, pow);
		}
		die();
		return true;
	}

}
