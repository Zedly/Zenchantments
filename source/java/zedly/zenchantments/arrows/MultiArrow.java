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

    @Override
    public void onImpact() {
        final AbstractArrow arrow = this.getArrow().getWorld().spawnArrow(
            this.getArrow().getLocation(),
            this.getArrow().getVelocity(),
            (float) (this.getArrow().getVelocity().length() / 10),
            0
        );

        arrow.setFireTicks(this.getArrow().getFireTicks());
        arrow.getLocation().setDirection(this.getArrow().getLocation().getDirection());
        arrow.setMetadata("ze.arrow", new FixedMetadataValue(ZenchantmentsPlugin.getInstance(), null));
        arrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
        this.getArrow().remove();
    }
}
