package zedly.zenchantments;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import zedly.zenchantments.arrows.ZenchantedArrow;

import java.util.Map;

// This is the watcher used by the EnchantArrow class. Each method checks for certain events
// and conditions and will call the relevant methods defined in the AdvancedArrow interface
public class WatcherArrow implements Listener {
    // Called when an arrow hits a block
    @EventHandler
    public void impact(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof Arrow)) {
            return;
        }

        Arrow entity = (Arrow) event.getEntity();

        if (!ZenchantedArrow.ADVANCED_PROJECTILES.containsKey(entity)) {
            return;
        }

        for (ZenchantedArrow arrow : ZenchantedArrow.ADVANCED_PROJECTILES.get(entity)) {
            arrow.onImpact();
        }
    }

    // Called when an arrow hits an entity
    @EventHandler
    public void entityHit(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Arrow)) {
            return;
        }

        Arrow damager = (Arrow) event.getDamager();

        if (!ZenchantedArrow.ADVANCED_PROJECTILES.containsKey(damager)) {
            return;
        }

        for (ZenchantedArrow arrow : ZenchantedArrow.ADVANCED_PROJECTILES.remove(damager)) {
            if (event.getEntity() instanceof LivingEntity) {
                if (!arrow.onImpact(event)) {
                    event.setDamage(0);
                }
            }

            if (event.getEntity() instanceof LivingEntity
                && event.getDamage() >= ((LivingEntity) event.getEntity()).getHealth()
            ) {
                ZenchantedArrow.KILLED_ENTITIES.put(event.getEntity(), arrow);
            }
        }
    }

    // Called when an arrow kills an entity; the advanced arrow is removed after this event
    @EventHandler
    public void entityDeath(EntityDeathEvent event) {
        Entity entity = event.getEntity();
        Map<Entity, ZenchantedArrow> killedEntities = ZenchantedArrow.KILLED_ENTITIES;

        if (killedEntities.containsKey(entity)) {
            killedEntities.remove(entity).onKill(event);
        }
    }
}