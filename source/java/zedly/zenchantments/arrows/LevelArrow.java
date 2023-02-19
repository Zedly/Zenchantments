package zedly.zenchantments.arrows;

import org.bukkit.entity.AbstractArrow;
import org.bukkit.event.entity.EntityDeathEvent;
import org.jetbrains.annotations.NotNull;

public final class LevelArrow extends ZenchantedArrow {
    public LevelArrow(
        final @NotNull AbstractArrow entity,
        final int level,
        final double power
    ) {
        super(entity, level, power);
    }

    @Override
    public void onKill(final @NotNull EntityDeathEvent event) {
        this.die(true);
    }

    @Override
    public void onImpact() {
        this.die(false);
    }
}
