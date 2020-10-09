package zedly.zenchantments.arrows;

import org.bukkit.entity.Arrow;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.ZenchantmentsPlugin;
import zedly.zenchantments.enchantments.Tracer;

public class TracerArrow extends ZenchantedArrow {
    public TracerArrow(@NotNull ZenchantmentsPlugin plugin, @NotNull Arrow entity, int level, double power) {
        super(plugin, entity, level, power);
        Tracer.TRACERS.put(entity, (int) Math.round(level * power));
    }

    @Override
    public boolean onImpact(@NotNull EntityDamageByEntityEvent event) {
        if (event.isCancelled()) {
            Tracer.TRACERS.remove(this.getArrow());
            this.die();
        }

        return true;
    }
}