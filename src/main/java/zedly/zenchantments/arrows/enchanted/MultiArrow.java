package zedly.zenchantments.arrows.enchanted;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.Storage;
import zedly.zenchantments.arrows.EnchantedArrow;

public class MultiArrow extends EnchantedArrow {
    public MultiArrow(@NotNull Arrow entity) {
        super(entity);
    }

    @Override
    public boolean onImpact(@NotNull EntityDamageByEntityEvent event) {
        LivingEntity entity = (LivingEntity) event.getEntity();
        int temp = entity.getMaximumNoDamageTicks();

        entity.setMaximumNoDamageTicks(0);
        entity.setNoDamageTicks(0);
        entity.setMaximumNoDamageTicks(temp);

        this.die();

        return true;
    }

    @Override
    public void onImpact() {
        Arrow arrow = this.getArrow().getWorld().spawnArrow(
            this.getArrow().getLocation(),
            this.getArrow().getVelocity(),
            (float) (this.getArrow().getVelocity().length() / 10),
            0
        );

        arrow.setFireTicks(this.getArrow().getFireTicks());
        arrow.getLocation().setDirection(this.getArrow().getLocation().getDirection());
        arrow.setMetadata("ze.arrow", new FixedMetadataValue(Storage.zenchantments, null));

        this.getArrow().remove();
    }
}