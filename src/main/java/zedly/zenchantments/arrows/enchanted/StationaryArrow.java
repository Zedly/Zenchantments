package zedly.zenchantments.arrows.enchanted;

import org.bukkit.Bukkit;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.Storage;
import zedly.zenchantments.Zenchantments;
import zedly.zenchantments.arrows.EnchantedArrow;

public class StationaryArrow extends EnchantedArrow {

    public StationaryArrow(Arrow entity) {
        super(entity);
    }

    public boolean onImpact(@NotNull EntityDamageByEntityEvent event) {
        if (Storage.COMPATIBILITY_ADAPTER.attackEntity((LivingEntity) event.getEntity(), (Player) arrow.getShooter(),
                0)) {
            LivingEntity ent = (LivingEntity) event.getEntity();
            if (event.getDamage() < ent.getHealth()) {
                event.setCancelled(true);

                // Imitate Flame arrows after cancelling the original event
                if (arrow.getFireTicks() > 0) {
                    EntityCombustByEntityEvent ecbee = new EntityCombustByEntityEvent(arrow, ent, 5);
                    Bukkit.getPluginManager().callEvent(ecbee);
                    if (!ecbee.isCancelled()) {
                        // For some fucking reason I can't set the entity on fire in the same tick. So I'm delaying it by one tick and now it works
                        Bukkit.getScheduler().scheduleSyncDelayedTask(Storage.zenchantments, () -> {
                            Storage.COMPATIBILITY_ADAPTER.igniteEntity(ent, (Player) arrow.getShooter(), 300);
                        }, 1);
                    }
                }

                ent.damage(event.getDamage());
                if (event.getDamager().getType() == EntityType.ARROW) {
                    event.getDamager().remove();
                }
            }

        }
        die();
        return true;
    }
}
