package zedly.zenchantments.arrows;

import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.jetbrains.annotations.NotNull;

public final class LevelArrow extends ZenchantedArrow {
    public LevelArrow(
        final @NotNull Projectile entity,
        final int level,
        final double power
    ) {
        super(entity, level, power);
    }

    @Override
    public void onKill(final @NotNull EntityDeathEvent event) {
        event.setDroppedExp((int) (event.getDroppedExp() * (1.3 + (getLevel() * this.getPower() * .5))));
        this.die(true);
    }
}
