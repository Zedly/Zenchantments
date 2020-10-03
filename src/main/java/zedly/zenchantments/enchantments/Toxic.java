package zedly.zenchantments.enchantments;

import org.bukkit.Bukkit;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import zedly.zenchantments.Zenchantment;
import zedly.zenchantments.Storage;
import zedly.zenchantments.Utilities;
import zedly.zenchantments.task.EffectTask;
import zedly.zenchantments.arrows.EnchantedArrow;
import zedly.zenchantments.arrows.enchanted.ToxicArrow;
import zedly.zenchantments.task.Frequency;
import zedly.zenchantments.Hand;
import zedly.zenchantments.Tool;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static org.bukkit.potion.PotionEffectType.CONFUSION;
import static org.bukkit.potion.PotionEffectType.HUNGER;
import static zedly.zenchantments.Tool.BOW;
import static zedly.zenchantments.Tool.SWORD;

public class Toxic extends Zenchantment {

	// Players that have been affected by the Toxic enchantment who cannot currently eat
	public static final Map<Player, Integer> hungerPlayers = new HashMap<>();
	public static final int                  ID            = 62;

	@Override
	public Builder<Toxic> defaults() {
		return new Builder<>(Toxic::new, ID)
			.maxLevel(4)
			.name("Toxic")
			.probability(0)
			.enchantable(new Tool[]{BOW, SWORD})
			.conflicting(new Class[]{})
			.description("Sickens the target, making them nauseous and unable to eat")
			.cooldown(0)
			.power(1.0)
			.handUse(Hand.BOTH);
	}

	@Override
	public boolean onEntityShootBow(EntityShootBowEvent event, int level, boolean usedHand) {
		ToxicArrow arrow = new ToxicArrow((Arrow) event.getProjectile(), level, power);
		EnchantedArrow.putArrow((Arrow) event.getProjectile(), arrow, (Player) event.getEntity());
		return true;
	}

	@Override
	public boolean onEntityHit(final EntityDamageByEntityEvent event, int level, boolean usedHand) {
		if (!(event.getEntity() instanceof LivingEntity) ||
			!ADAPTER.attackEntity((LivingEntity) event.getEntity(), (Player) event.getDamager(), 0)) {
			final int value = (int) Math.round(level * power);
			Utilities.addPotion((LivingEntity) event.getEntity(), CONFUSION, 80 + 60 * value, 4);
			Utilities.addPotion((LivingEntity) event.getEntity(), HUNGER, 40 + 60 * value, 4);
			if (event.getEntity() instanceof Player) {
				Bukkit.getScheduler().scheduleSyncDelayedTask(Storage.zenchantments, () -> {
					((LivingEntity) event.getEntity()).removePotionEffect(HUNGER);
					Utilities.addPotion((LivingEntity) event.getEntity(), HUNGER, 60 + 40 * value, 0);
				}, 20 + 60 * value);
				hungerPlayers.put((Player) event.getEntity(), (1 + value) * 100);
			}
		}
		return true;
	}

	@EffectTask(Frequency.HIGH)
	// Manages time left for players affected by Toxic enchantment
	public static void hunger() {
		Iterator it = hungerPlayers.keySet().iterator();
		while (it.hasNext()) {
			Player player = (Player) it.next();
			if (hungerPlayers.get(player) < 1) {
				it.remove();
			} else {
				hungerPlayers.put(player, hungerPlayers.get(player) - 1);
			}
		}
	}
}
