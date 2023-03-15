package zedly.zenchantments.event.listener;

import org.bukkit.Bukkit;
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

        final AbstractArrow arrowEntity = (AbstractArrow) event.getEntity();

        if (!arrowEntity.hasMetadata(ZenchantedArrow.ARROW_METADATA_NAME)) {
            return;
        }

        List<MetadataValue> metas = arrowEntity.getMetadata(ZenchantedArrow.ARROW_METADATA_NAME);

        if (event.getHitBlock() != null) {
            for (MetadataValue meta : metas) {
                List<ZenchantedArrow> arrowMeta = (List<ZenchantedArrow>) meta.value();
                for (ZenchantedArrow zenchantedArrow : arrowMeta) {
                    zenchantedArrow.onImpact(event);
                }
            }
        } else if (event.getHitEntity() != null) {
            Entity hitEntity = event.getHitEntity();
            for (MetadataValue meta : metas) {
                List<ZenchantedArrow> arrowMeta = (List<ZenchantedArrow>) meta.value();
                for (ZenchantedArrow zenchantedArrow : arrowMeta) {
                    if (hitEntity instanceof LivingEntity) {
                        zenchantedArrow.onImpactEntity(event);
                    }
                    if (hitEntity instanceof LivingEntity) {
                        hitEntity.setMetadata(ZenchantedArrow.KILLED_BY_ARROW_METADATA_NAME, new FixedMetadataValue(ZenchantmentsPlugin.getInstance(), zenchantedArrow));
                        Bukkit.getScheduler().scheduleSyncDelayedTask(ZenchantmentsPlugin.getInstance(), () -> hitEntity.removeMetadata(ZenchantedArrow.KILLED_BY_ARROW_METADATA_NAME, ZenchantmentsPlugin.getInstance()), 0);
                    }

                }
            }
        }
        arrowEntity.removeMetadata(ZenchantedArrow.ARROW_METADATA_NAME, ZenchantmentsPlugin.getInstance());
    }

    @EventHandler
    private void onEntityHurtByArrow(EntityDamageByEntityEvent event) {
        final Entity entity = event.getEntity();
        if (!entity.hasMetadata(ZenchantedArrow.KILLED_BY_ARROW_METADATA_NAME)) {
            return;
        }
        List<MetadataValue> metas = entity.getMetadata(ZenchantedArrow.KILLED_BY_ARROW_METADATA_NAME);
        for (MetadataValue value : metas) {
            if (value.value() instanceof ZenchantedArrow arrow) {
                arrow.onDamageEntity(event);
            }
        }
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
    }
}
