package zedly.zenchantments.arrows;

import org.bukkit.entity.Arrow;
import org.bukkit.event.entity.EntityDeathEvent;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.ZenchantmentsPlugin;

public final class LevelArrow extends ZenchantedArrow {
    public LevelArrow(
        final @NotNull ZenchantmentsPlugin plugin,
        final @NotNull Arrow entity,
        final int level,
        final double power
    ) {
        super(plugin, entity, level, power);
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
