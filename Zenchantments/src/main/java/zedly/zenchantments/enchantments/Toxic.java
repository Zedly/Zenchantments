package zedly.zenchantments.enchantments;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import zedly.zenchantments.CustomEnchantment;
import zedly.zenchantments.EnchantArrow;
import zedly.zenchantments.Storage;
import zedly.zenchantments.Utilities;
import zedly.zenchantments.annotations.EffectTask;
import zedly.zenchantments.enums.Frequency;
import zedly.zenchantments.enums.Hand;
import zedly.zenchantments.enums.Tool;

import java.util.Iterator;

import static org.bukkit.potion.PotionEffectType.CONFUSION;
import static org.bukkit.potion.PotionEffectType.HUNGER;
import static zedly.zenchantments.enums.Tool.BOW;
import static zedly.zenchantments.enums.Tool.SWORD;

public class Toxic extends CustomEnchantment {

    public Toxic() {
	    super(62);
	    maxLevel = 4;
	    loreName = "Toxic";
	    probability = 0;
	    enchantable = new Tool[]{BOW, SWORD};
	    conflicting = new Class[]{};
	    description = "Sickens the target, making them nauseous and unable to eat";
	    cooldown = 0;
	    power = 1.0;
	    handUse = Hand.BOTH;
    }

    @Override
    public boolean onEntityShootBow(EntityShootBowEvent evt, int level, boolean usedHand) {
        EnchantArrow.ArrowEnchantToxic arrow =
                new EnchantArrow.ArrowEnchantToxic((Projectile) evt.getProjectile(), level, power);
        Utilities.putArrow(evt.getProjectile(), arrow, (Player) evt.getEntity());
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
                Storage.hungerPlayers.put((Player) evt.getEntity(), (1 + value) * 100);
            }
        }
        return true;
    }

	@EffectTask(Frequency.HIGH)
	// Manages time left for players affected by Toxic enchantment
	public static void hunger() {
		Iterator it = Storage.hungerPlayers.keySet().iterator();
		while (it.hasNext()) {
			Player player = (Player) it.next();
			if (Storage.hungerPlayers.get(player) < 1) {
				it.remove();
			} else {
				Storage.hungerPlayers.put(player, Storage.hungerPlayers.get(player) - 1);
			}
		}
	}
}
