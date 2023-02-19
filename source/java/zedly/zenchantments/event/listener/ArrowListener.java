package zedly.zenchantments.event.listener;

import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.ZenchantmentsPlugin;
import zedly.zenchantments.arrows.ZenchantedArrow;

import java.util.List;

public class ArrowListener implements Listener {
    @EventHandler
    private void onProjectileHit(final @NotNull ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof AbstractArrow)) {
            return;
        }

        if(event.getHitBlock() == null) {
            return;
        }

        final AbstractArrow damager = (AbstractArrow) event.getEntity();

        if (!damager.hasMetadata(ZenchantedArrow.ARROW_METADATA_NAME)) {
            return;
        }

        List<MetadataValue> metas = damager.getMetadata(ZenchantedArrow.ARROW_METADATA_NAME);
        for (MetadataValue meta : metas) {
            List<ZenchantedArrow> arrowMeta = (List<ZenchantedArrow>) meta.value();
            for (ZenchantedArrow arrow : arrowMeta) {
                arrow.onImpact();
            }
        }
        damager.removeMetadata(ZenchantedArrow.ARROW_METADATA_NAME, ZenchantmentsPlugin.getInstance());
    }

    @EventHandler
    private void onEntityDamageByEntity(final @NotNull EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof AbstractArrow)) {
            return;
        }

        final AbstractArrow damager = (AbstractArrow) event.getDamager();

        if (!damager.hasMetadata(ZenchantedArrow.ARROW_METADATA_NAME)) {
            return;
        }

        List<MetadataValue> metas = damager.getMetadata(ZenchantedArrow.ARROW_METADATA_NAME);
        for (MetadataValue meta : metas) {
            List<ZenchantedArrow> arrowMeta = (List<ZenchantedArrow>) meta.value();
            for (ZenchantedArrow arrow : arrowMeta) {
                arrow.onImpact();
                if (event.getEntity() instanceof LivingEntity) {
                    if (!arrow.onImpact(event)) {
                        event.setDamage(0);
                    }
                }

                if (event.getEntity() instanceof LivingEntity
                    && event.getDamage() >= ((LivingEntity) event.getEntity()).getHealth()
                ) {
                    event.getEntity().setMetadata(ZenchantedArrow.KILLED_BY_ARROW_METADATA_NAME, new FixedMetadataValue(ZenchantmentsPlugin.getInstance(), arrow));
                }
            }
        }
        damager.removeMetadata(ZenchantedArrow.ARROW_METADATA_NAME, ZenchantmentsPlugin.getInstance());
    }

    @EventHandler
    private void onEntityDeath(final @NotNull EntityDeathEvent event) {
        final Entity entity = event.getEntity();

        if (!entity.hasMetadata(ZenchantedArrow.KILLED_BY_ARROW_METADATA_NAME)) {
            return;
        }

        List<MetadataValue> metas = entity.getMetadata(ZenchantedArrow.KILLED_BY_ARROW_METADATA_NAME);
        for (MetadataValue value : metas) {
            if (value.value() instanceof ZenchantedArrow arrow) {
                arrow.onKill(event);
            }
        }
        entity.removeMetadata(ZenchantedArrow.KILLED_BY_ARROW_METADATA_NAME, ZenchantmentsPlugin.getInstance());
    }
}
