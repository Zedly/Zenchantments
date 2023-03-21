package zedly.zenchantments.enchantments;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;
import zedly.zenchantments.arrows.SingularityArrow;
import zedly.zenchantments.arrows.ZenchantedArrow;
import zedly.zenchantments.task.EffectTask;
import zedly.zenchantments.task.Frequency;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import static java.util.Objects.requireNonNull;

@AZenchantment(runInSlots = Slots.HANDS, conflicting = {})
public final class Singularity extends Zenchantment {
    public static final Map<Location, Boolean> SINGULARITIES = new HashMap<>();

    @Override
    public boolean onEntityShootBow(final @NotNull EntityShootBowEvent event, final int level, final EquipmentSlot slot) {
        final SingularityArrow arrow = new SingularityArrow((AbstractArrow) event.getProjectile(), level);
        ZenchantedArrow.addZenchantedArrowToArrowEntity((AbstractArrow) event.getProjectile(), arrow, (Player) event.getEntity());
        return true;
    }

    @EffectTask(Frequency.HIGH)
    public static void singularityPhysics() {
        for (final Location location : SINGULARITIES.keySet()) {
            for (final Entity entity : requireNonNull(location.getWorld()).getNearbyEntities(location, 10, 10, 10)) {
                if (entity instanceof Player) {
                    if (((Player) entity).getGameMode() == GameMode.CREATIVE) {
                        continue;
                    }
                }

                final ThreadLocalRandom random = ThreadLocalRandom.current();

                if (SINGULARITIES.get(location)) {
                    final Vector vector = location.clone().subtract(entity.getLocation()).toVector();
                    vector.setX(vector.getX() + (-0.5f + random.nextFloat()) * 10);
                    vector.setY(vector.getY() + (-0.5f + random.nextFloat()) * 10);
                    vector.setZ(vector.getZ() + (-0.5f + random.nextFloat()) * 10);

                    entity.setVelocity(vector.multiply(.35f));
                    entity.setFallDistance(0);
                } else {
                    final Vector vector = entity.getLocation().subtract(location.clone()).toVector();
                    vector.setX(vector.getX() + (-0.5f + random.nextFloat()) * 2);
                    vector.setY(vector.getY() + random.nextFloat());
                    vector.setZ(vector.getZ() + (-0.5f + random.nextFloat()) * 2);

                    entity.setVelocity(vector.multiply(.35f));
                }
            }
        }
    }
}
