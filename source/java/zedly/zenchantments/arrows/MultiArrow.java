package zedly.zenchantments.arrows;

import org.bukkit.Bukkit;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.ZenchantmentsPlugin;

public final class MultiArrow extends ZenchantedArrow {
    public MultiArrow(final @NotNull AbstractArrow entity) {
        super(entity);
    }

    @Override
    public void onImpactEntity(final @NotNull ProjectileHitEvent event) {
        final LivingEntity entity = (LivingEntity) event.getHitEntity();
        final int temp = entity.getMaximumNoDamageTicks();

        Bukkit.getScheduler().scheduleSyncDelayedTask(ZenchantmentsPlugin.getInstance(), () -> {
            entity.setMaximumNoDamageTicks(0);
            entity.setNoDamageTicks(0);
            entity.setMaximumNoDamageTicks(temp); // Apparently resetting the damage ticks doesn't always work unless I do this
        }, 0);

        die(false);
    }
}
