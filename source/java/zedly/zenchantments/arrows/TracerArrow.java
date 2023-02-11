package zedly.zenchantments.arrows;

import org.bukkit.Location;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.enchantments.Tracer;

import static java.util.Objects.requireNonNull;

public final class TracerArrow extends ZenchantedArrow {
    public TracerArrow(
        final @NotNull AbstractArrow entity,
        final int level,
        final double power
    ) {
        super(entity, level, power);
        Tracer.TRACERS.put(entity, (int) Math.round(level * power));
    }

    @Override
    public boolean onImpact(final @NotNull EntityDamageByEntityEvent event) {
        if (event.isCancelled()) {
            Tracer.TRACERS.remove(this.getArrow());
            this.die();
        }

        return true;
    }

    public void onTick() {
        Entity closestEntity = null;
        double closestDistance = 100;
        int searchRadius = getLevel() + 2;
        for (final Entity entity : getArrow().getNearbyEntities(searchRadius, searchRadius, searchRadius)) {
            if (!entity.getWorld().equals(getArrow().getWorld())) {
                continue;
            }

            final double d = entity.getLocation().distance(getArrow().getLocation());
            final Entity shooter = (Entity) getArrow().getShooter();

            if (getArrow().getWorld().equals(requireNonNull(shooter).getWorld())) {
                if (d < closestDistance && entity instanceof LivingEntity
                    && !entity.equals(getArrow().getShooter())
                    && getArrow().getLocation().distance(shooter.getLocation()) > 15
                ) {
                    closestDistance = d;
                    closestEntity = entity;
                }
            }
        }

        if (closestEntity != null) {
            final Location targetLoc = closestEntity.getLocation();
            final Location arrowLoc = getArrow().getLocation();
            final Vector vector = targetLoc.toVector().subtract(arrowLoc.toVector()).normalize().multiply(0.1);
            getArrow().setVelocity(vector.multiply(2));
        }
    }
}
