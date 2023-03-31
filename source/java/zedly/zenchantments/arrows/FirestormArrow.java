package zedly.zenchantments.arrows;

import org.bukkit.Particle;
import org.bukkit.entity.*;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.WorldInteractionUtil;
import zedly.zenchantments.Utilities;

public final class FirestormArrow extends ZenchantedArrow {
    public FirestormArrow(
        final @NotNull Projectile entity,
        final int level,
        final double power
    ) {
        super(entity, level, power);
    }

    @Override
    public void onImpactEntity(ProjectileHitEvent event) {
        onImpact(event);
    }

    @Override
    public void onImpact(ProjectileHitEvent event) {
        Utilities.displayParticle(
            Utilities.getCenter(this.getArrow().getLocation()),
            Particle.FLAME,
            100 * this.getLevel(),
            0.1f,
            this.getLevel(),
            1.5f,
            this.getLevel()
        );

        final double radius = 1 + this.getLevel() * this.getPower();
        for (final Entity entity : this.getArrow().getNearbyEntities(radius, radius, radius)) {
            if (!(entity instanceof LivingEntity)
                || entity.equals(this.getArrow().getShooter())
                || !WorldInteractionUtil.attackEntity((LivingEntity) entity, (Player) this.getArrow().getShooter(), 0)
            ) {
                continue;
            }

            entity.setFireTicks((int) Math.round(this.getLevel() * this.getPower() * 100));
        }

        die(true);
    }
}
