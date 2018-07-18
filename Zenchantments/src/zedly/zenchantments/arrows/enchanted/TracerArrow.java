package zedly.zenchantments.arrows.enchanted;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import zedly.zenchantments.Storage;
import zedly.zenchantments.arrows.EnchantedArrow;

/**
 * Description
 *
 * @author rfrowe
 */
public class TracerArrow extends EnchantedArrow {

    public TracerArrow(Arrow entity, int level, double power) {
        super(entity, level, power);
        Storage.tracer.put(entity, (int) Math.round(level * power));
    }

    @Override
    public boolean onImpact(EntityDamageByEntityEvent evt) {
        if (evt.isCancelled()) {
            Storage.tracer.remove(arrow);
            die();
        }
        return true;
    }
}
