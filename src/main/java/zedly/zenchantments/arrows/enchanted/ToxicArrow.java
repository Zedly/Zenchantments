package zedly.zenchantments.arrows.enchanted;

import org.bukkit.Bukkit;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.Storage;
import zedly.zenchantments.Utilities;
import zedly.zenchantments.arrows.EnchantedArrow;
import zedly.zenchantments.enchantments.Toxic;

import static org.bukkit.potion.PotionEffectType.CONFUSION;
import static org.bukkit.potion.PotionEffectType.HUNGER;

public class ToxicArrow extends EnchantedArrow {

	public ToxicArrow(Arrow entity, int level, double power) {
		super(entity, level, power);
	}

	public boolean onImpact(final @NotNull EntityDamageByEntityEvent event) {
		if (Storage.COMPATIBILITY_ADAPTER.attackEntity((LivingEntity) event.getEntity(), (Player) arrow.getShooter(),
			0)) {
			final int value = (int) Math.round(getLevel() * getPower());
			Utilities.addPotion((LivingEntity) event.getEntity(), CONFUSION, 80 + 60 * value, 4);
			Utilities.addPotion((LivingEntity) event.getEntity(), HUNGER, 40 + 60 * value, 4);
			if (event.getEntity() instanceof Player) {
				Bukkit.getScheduler().scheduleSyncDelayedTask(Storage.zenchantments, () -> {
					((LivingEntity) event.getEntity()).removePotionEffect(HUNGER);
					Utilities.addPotion((LivingEntity) event.getEntity(), HUNGER, 60 + 40 * value, 0);
				}, 20 + 60 * value);
				Toxic.hungerPlayers.put((Player) event.getEntity(), (1 + value) * 100);
			}
		}
		die();
		return true;
	}
}
