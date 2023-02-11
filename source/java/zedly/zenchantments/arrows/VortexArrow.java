package zedly.zenchantments.arrows;

import org.bukkit.event.entity.EntityDeathEvent;
import org.jetbrains.annotations.NotNull;

public final class VortexArrow extends ZenchantedArrow {
    public VortexArrow(final @NotNull AbstractArrow entity) {
        super(entity);
    }

    @Override
    public void onKill(final @NotNull EntityDeathEvent event) {
        this.die();
    }
}
