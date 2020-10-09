package zedly.zenchantments.event.listener;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.arrows.ZenchantedArrow;

public class ArrowListener implements Listener {
    @EventHandler
    private void onProjectileHit(@NotNull ProjectileHitEvent event) {
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

    @EventHandler
    private void onEntityDamageByEntity(@NotNull EntityDamageByEntityEvent event) {
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

    @EventHandler
    private void onEntityDeath(@NotNull EntityDeathEvent event) {
        Entity entity = event.getEntity();

        if (ZenchantedArrow.KILLED_ENTITIES.containsKey(entity)) {
            ZenchantedArrow.KILLED_ENTITIES.remove(entity).onKill(event);
        }
    }
}