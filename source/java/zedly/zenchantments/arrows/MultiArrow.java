package zedly.zenchantments.arrows;

import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.ZenchantmentsPlugin;

public final class MultiArrow extends ZenchantedArrow {
    public MultiArrow(final @NotNull AbstractArrow entity) {
        super(entity);
    }

    @Override
    public boolean onImpact(final @NotNull EntityDamageByEntityEvent event) {
        final LivingEntity entity = (LivingEntity) event.getEntity();
        final int temp = entity.getMaximumNoDamageTicks();

        entity.setMaximumNoDamageTicks(0);
        entity.setNoDamageTicks(0);
        entity.setMaximumNoDamageTicks(temp);

        this.die();

        return true;
    }
}
