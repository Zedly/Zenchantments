package zedly.zenchantments.arrows;

import org.bukkit.Bukkit;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.CompatibilityAdapter;
import zedly.zenchantments.ZenchantmentsPlugin;

public final class StationaryArrow extends ZenchantedArrow {
    public StationaryArrow(final @NotNull AbstractArrow entity) {
        super(entity);
    }

    @Override
    public boolean onImpact(final @NotNull EntityDamageByEntityEvent event) {
        if (CompatibilityAdapter.instance().attackEntity((LivingEntity) event.getEntity(), (Player) this.getArrow().getShooter(), 0)) {
            final LivingEntity entity = (LivingEntity) event.getEntity();
            if (event.getDamage() < entity.getHealth()) {
                event.setCancelled(true);

                // Imitate Flame arrows after cancelling the original event.
                if (this.getArrow().getFireTicks() > 0) {
                    final EntityCombustByEntityEvent combustByEntityEvent = new EntityCombustByEntityEvent(this.getArrow(), entity, 5);

                    Bukkit.getPluginManager().callEvent(combustByEntityEvent);

                    if (!combustByEntityEvent.isCancelled()) {
                        Bukkit.getScheduler().scheduleSyncDelayedTask(
                            ZenchantmentsPlugin.getInstance(),
                            () -> CompatibilityAdapter.instance().igniteEntity(entity, (Player) this.getArrow().getShooter(), 300),
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
