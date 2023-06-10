package zedly.zenchantments.event.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.ZenchantmentPriority;
import zedly.zenchantments.ZenchantmentsPlugin;
import zedly.zenchantments.arrows.ZenchantedArrow;

import java.util.List;
import java.util.function.Consumer;

public class ArrowListener implements Listener {
    @EventHandler
    private void onProjectileHit(final @NotNull ProjectileHitEvent event) {
        final Projectile arrowEntity = event.getEntity();

        if (event.getHitBlock() != null) {
            forEachZenchatedArrow(arrowEntity, ZenchantedArrow.ARROW_METADATA_NAME, (za) -> za.onImpact(event));
        } else if (event.getHitEntity() != null) {
            Entity hitEntity = event.getHitEntity();
            if (hitEntity instanceof LivingEntity) {
                forEachZenchatedArrow(arrowEntity, ZenchantedArrow.ARROW_METADATA_NAME, (za) -> {
                    za.onImpactEntity(event);
                    ZenchantedArrow.addZenchantedArrowToEntity(hitEntity, ZenchantedArrow.KILLED_BY_ARROW_METADATA_NAME, za);
                });
                Bukkit.getScheduler().scheduleSyncDelayedTask(ZenchantmentsPlugin.getInstance(), () -> hitEntity.removeMetadata(ZenchantedArrow.KILLED_BY_ARROW_METADATA_NAME, ZenchantmentsPlugin.getInstance()), 0);
            }
            arrowEntity.removeMetadata(ZenchantedArrow.ARROW_METADATA_NAME, ZenchantmentsPlugin.getInstance());
        }
    }

    @EventHandler
    private void onEntityHurtByArrow(EntityDamageByEntityEvent event) {
        forEachZenchatedArrow(event.getEntity(), ZenchantedArrow.KILLED_BY_ARROW_METADATA_NAME, (za) -> za.onDamageEntity(event));
    }

    @EventHandler
    private void onEntityDeath(final @NotNull EntityDeathEvent event) {
        forEachZenchatedArrow(event.getEntity(), ZenchantedArrow.KILLED_BY_ARROW_METADATA_NAME, (za) -> za.onKill(event));
    }

    private static final void forEachZenchatedArrow(Entity ent, String
        metadataName, Consumer<ZenchantedArrow> consumer) {
        if (!ent.hasMetadata(metadataName)) {
            return;
        }
        List<MetadataValue> metas = ent.getMetadata(metadataName);
        for (MetadataValue meta : metas) {
            List<ZenchantedArrow> arrowMeta = (List<ZenchantedArrow>) meta.value();
            for (ZenchantedArrow zenchantedArrow : arrowMeta) {
                if(!zenchantedArrow.recursionLock && zenchantedArrow.getPriority() == ZenchantmentPriority.EARLY) {
                    zenchantedArrow.recursionLock = true;
                    consumer.accept(zenchantedArrow);
                    zenchantedArrow.recursionLock = false;
                }
            }
            for (ZenchantedArrow zenchantedArrow : arrowMeta) {
                if(!zenchantedArrow.recursionLock && zenchantedArrow.getPriority() == ZenchantmentPriority.NORMAL) {
                    zenchantedArrow.recursionLock = true;
                    consumer.accept(zenchantedArrow);
                    zenchantedArrow.recursionLock = false;
                }
            }
            for (ZenchantedArrow zenchantedArrow : arrowMeta) {
                if(!zenchantedArrow.recursionLock && zenchantedArrow.getPriority() == ZenchantmentPriority.LATE) {
                    zenchantedArrow.recursionLock = true;
                    consumer.accept(zenchantedArrow);
                    zenchantedArrow.recursionLock = false;
                }
            }
        }
    }
}
