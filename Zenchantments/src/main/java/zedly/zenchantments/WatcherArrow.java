package zedly.zenchantments;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;

import java.util.Set;

// This is the watcher used by the EnchantArrow class. Each method checks for certain events
// and conditions and will call the relevant methods defined in the AdvancedArrow interface
//      
public class WatcherArrow implements Listener {


    // Called when an arrow hits a block
    @EventHandler
    public boolean impact(ProjectileHitEvent evt) {
        if (Storage.advancedProjectiles.containsKey(evt.getEntity())) {
            Set<AdvancedArrow> ar = Storage.advancedProjectiles.get(evt.getEntity());
            for (AdvancedArrow a : ar) {
                a.onImpact();
            }
        }
        return true;
    }

    // Called when an arrow hits an entity
    @EventHandler
    public boolean entityHit(EntityDamageByEntityEvent evt) {
        if (evt.getDamager() instanceof Arrow) {
            if (Storage.advancedProjectiles.containsKey(evt.getDamager())) {
                Set<AdvancedArrow> arrows = Storage.advancedProjectiles.get(evt.getDamager());
                for (AdvancedArrow arrow : arrows) {
                    if (evt.getEntity() instanceof LivingEntity) {
                        if (!arrow.onImpact(evt)) {
                            evt.setDamage(0);
                        }
                    }
                    Storage.advancedProjectiles.remove(evt.getDamager());
                    if (evt.getEntity() instanceof LivingEntity && evt.getDamage() >= ((LivingEntity) evt.getEntity()).getHealth()) {
                        Storage.killedEntities.put(evt.getEntity(), arrow);
                    }
                }
            }
        }
        return true;
    }

    // Called when an arrow kills an entity; the advanced arrow is removed after this event
    @EventHandler
    public boolean entityDeath(EntityDeathEvent evt) {
        if (Storage.killedEntities.containsKey(evt.getEntity())) {
            AdvancedArrow arrow = Storage.killedEntities.get(evt.getEntity());
            arrow.onKill(evt);
            Storage.killedEntities.remove(evt.getEntity());
        }
        return true;
    }

}
