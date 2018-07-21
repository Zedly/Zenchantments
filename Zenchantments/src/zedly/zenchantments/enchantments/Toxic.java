package zedly.zenchantments.enchantments;

import org.bukkit.Bukkit;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import zedly.zenchantments.CustomEnchantment;
import zedly.zenchantments.Storage;
import zedly.zenchantments.Utilities;
import zedly.zenchantments.annotations.EffectTask;
import zedly.zenchantments.arrows.EnchantedArrow;
import zedly.zenchantments.arrows.enchanted.ToxicArrow;
import zedly.zenchantments.enums.Frequency;
import zedly.zenchantments.enums.Hand;
import zedly.zenchantments.enums.Tool;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static org.bukkit.potion.PotionEffectType.CONFUSION;
import static org.bukkit.potion.PotionEffectType.HUNGER;
import static zedly.zenchantments.enums.Tool.BOW;
import static zedly.zenchantments.enums.Tool.SWORD;

public class Toxic extends CustomEnchantment {

	// Players that have been affected by the Toxic enchantment who cannot currently eat
	public static final Map<Player, Integer> hungerPlayers = new HashMap<>();

	@Override
	public Builder<Toxic> defaults() {
		return new Builder<>(Toxic::new, 62)
			.maxLevel(4)
			.loreName("Toxic")
			.probability(0)
			.enchantable(new Tool[]{BOW, SWORD})
			.conflicting(new Class[]{})
			.description("Sickens the target, making them nauseous and unable to eat")
			.cooldown(0)
			.power(1.0)
			.handUse(Hand.BOTH);
    }

    @Override
    public boolean onEntityShootBow(EntityShootBowEvent evt, int level, boolean usedHand) {
	    ToxicArrow arrow = new ToxicArrow((Arrow) evt.getProjectile(), level, power);
	    EnchantedArrow.putArrow((Arrow) evt.getProjectile(), arrow, (Player) evt.getEntity());
        return true;
    }

    @Override
    public boolean onEntityHit(final EntityDamageByEntityEvent evt, int level, boolean usedHand) {
        if(!(evt.getEntity() instanceof LivingEntity) ||
           !ADAPTER.attackEntity((LivingEntity) evt.getEntity(), (Player) evt.getDamager(), 0)) {
            final int value = (int) Math.round(level * power);
            Utilities.addPotion((LivingEntity) evt.getEntity(), CONFUSION, 80 + 60 * value, 4);
            Utilities.addPotion((LivingEntity) evt.getEntity(), HUNGER, 40 + 60 * value, 4);
            if(evt.getEntity() instanceof Player) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(Storage.zenchantments, () -> {
                    ((LivingEntity) evt.getEntity()).removePotionEffect(HUNGER);
                    Utilities.addPotion((LivingEntity) evt.getEntity(), HUNGER, 60 + 40 * value, 0);
                }, 20 + 60 * value);
                hungerPlayers.put((Player) evt.getEntity(), (1 + value) * 100);
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
