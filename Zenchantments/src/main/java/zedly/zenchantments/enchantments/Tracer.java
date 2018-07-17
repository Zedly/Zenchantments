package zedly.zenchantments.enchantments;

import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityShootBowEvent;
import zedly.zenchantments.CustomEnchantment;
import zedly.zenchantments.EnchantArrow;
import zedly.zenchantments.Storage;
import zedly.zenchantments.Utilities;
import zedly.zenchantments.annotations.EffectTask;
import zedly.zenchantments.enums.Frequency;
import zedly.zenchantments.enums.Hand;
import zedly.zenchantments.enums.Tool;

import static zedly.zenchantments.enums.Tool.BOW;

public class Tracer extends CustomEnchantment {

    public Tracer() {
	    super(63);
	    maxLevel = 4;
	    loreName = "Tracer";
	    probability = 0;
	    enchantable = new Tool[]{BOW};
	    conflicting = new Class[]{};
	    description = "Guides the arrow to targets and then attacks";
	    cooldown = 0;
	    power = 1.0;
	    handUse = Hand.RIGHT;
    }

    @Override
    public boolean onEntityShootBow(EntityShootBowEvent evt, int level, boolean usedHand) {
        EnchantArrow.ArrowEnchantTracer arrow =
                new EnchantArrow.ArrowEnchantTracer((Projectile) evt.getProjectile(), level, power);
        Utilities.putArrow(evt.getProjectile(), arrow, (Player) evt.getEntity());
        return true;
    }

	@EffectTask(Frequency.HIGH)
	// Moves Tracer arrows towards a target
	public static void tracer() {
		for (Arrow e : Storage.tracer.keySet()) {
			Entity close = null;
			double distance = 100;
			int level = Storage.tracer.get(e);
			level += 2;
			for (Entity e1 : e.getNearbyEntities(level, level, level)) {
				if (e1.getLocation().getWorld().equals(e.getLocation().getWorld())) {
					double d = e1.getLocation().distance(e.getLocation());
					if (e.getLocation().getWorld().equals(((Entity) e.getShooter()).getLocation().getWorld())) {
						if (d < distance && e1 instanceof LivingEntity
								&& !e1.equals(e.getShooter())
								&& e.getLocation().distance(((Entity) e.getShooter()).getLocation()) > 15) {
							distance = d;
							close = e1;
						}
					}
				}
			}
			if (close != null) {
				Location location = close.getLocation();
				org.bukkit.util.Vector v = new org.bukkit.util.Vector(0D, 0D, 0D);
				Location pos = e.getLocation();
				double its = location.distance(pos);
				if (its == 0) {
					its = 1;
				}
				v.setX((location.getX() - pos.getX()) / its);
				v.setY((location.getY() - pos.getY()) / its);
				v.setZ((location.getZ() - pos.getZ()) / its);
				v.add(e.getLocation().getDirection().multiply(.1));
				e.setVelocity(v.multiply(2));
			}
		}
	}
}
