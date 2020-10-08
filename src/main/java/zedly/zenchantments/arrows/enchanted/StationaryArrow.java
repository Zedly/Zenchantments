package zedly.zenchantments.arrows.enchanted;

import org.bukkit.Bukkit;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.Storage;
import zedly.zenchantments.arrows.EnchantedArrow;

public class StationaryArrow extends EnchantedArrow {

    public StationaryArrow(Arrow entity) {
        super(entity);
    }

    public boolean onImpact(@NotNull EntityDamageByEntityEvent event) {
        if (Storage.COMPATIBILITY_ADAPTER.attackEntity((LivingEntity) event.getEntity(), (Player) this.getArrow().getShooter(), 0)) {
            LivingEntity entity = (LivingEntity) event.getEntity();
            if (event.getDamage() < entity.getHealth()) {
                event.setCancelled(true);

                // Imitate Flame arrows after cancelling the original event.
                if (this.getArrow().getFireTicks() > 0) {
                    EntityCombustByEntityEvent combustByEntityEvent = new EntityCombustByEntityEvent(this.getArrow(), entity, 5);

                    Bukkit.getPluginManager().callEvent(combustByEntityEvent);

                    if (!combustByEntityEvent.isCancelled()) {
                        Bukkit.getScheduler().scheduleSyncDelayedTask(
                            Storage.zenchantments,
                            () -> Storage.COMPATIBILITY_ADAPTER.igniteEntity(entity, (Player) this.getArrow().getShooter(), 300),
                            1
                        );
                    }
                }

                entity.damage(event.getDamage());

                if (event.getDamager().getType() == EntityType.ARROW) {
                    event.getDamager().remove();
                }
            }
        }

        this.die();
        return true;
    }
}