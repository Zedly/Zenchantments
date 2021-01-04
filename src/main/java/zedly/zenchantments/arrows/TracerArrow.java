package zedly.zenchantments.arrows;

import org.bukkit.entity.Arrow;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.ZenchantmentsPlugin;
import zedly.zenchantments.enchantments.Tracer;

public final class TracerArrow extends ZenchantedArrow {
    public TracerArrow(
        final @NotNull ZenchantmentsPlugin plugin,
        final @NotNull Arrow entity,
        final int level,
        final double power
    ) {
        super(plugin, entity, level, power);
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
}
