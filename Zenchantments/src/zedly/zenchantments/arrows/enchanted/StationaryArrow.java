package zedly.zenchantments.arrows.enchanted;

import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import zedly.zenchantments.Storage;
import zedly.zenchantments.arrows.EnchantedArrow;

/**
 * Description
 *
 * @author rfrowe
 */
public class StationaryArrow extends EnchantedArrow {

    public StationaryArrow(Arrow entity) {
        super(entity);
    }

    public boolean onImpact(EntityDamageByEntityEvent evt) {
        if (Storage.COMPATIBILITY_ADAPTER.attackEntity((LivingEntity) evt.getEntity(), (Player) arrow.getShooter(), 0)) {
            LivingEntity ent = (LivingEntity) evt.getEntity();
            if (evt.getDamage() < ent.getHealth()) {
                evt.setCancelled(true);
                ((LivingEntity) evt.getEntity()).damage(evt.getDamage());
                if (evt.getDamager().getType() == EntityType.ARROW) {
                    evt.getDamager().remove();
                }
            }
        }
        die();
        return true;
    }
}
