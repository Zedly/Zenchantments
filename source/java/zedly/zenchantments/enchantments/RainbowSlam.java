package zedly.zenchantments.enchantments;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;

@AZenchantment(runInSlots = Slots.MAIN_HAND, conflicting = {Force.class})
public final class RainbowSlam extends Zenchantment {
    public static final Set<Entity> RAINBOW_SLAM_ENTITIES = new HashSet<>();

    @Override
    public boolean onEntityInteract(final @NotNull PlayerInteractEntityEvent event, final int level, final EquipmentSlot slot) {
        if (slot != EquipmentSlot.HAND || !(event.getRightClicked() instanceof LivingEntity)
            || !WorldInteractionUtil.attackEntity((LivingEntity) event.getRightClicked(), event.getPlayer(), 0)
        ) {
            return false;
        }

        Utilities.damageItemStackRespectUnbreaking(event.getPlayer(), 9, slot);

        final LivingEntity entity = (LivingEntity) event.getRightClicked();
        final Location location = entity.getLocation();

        entity.teleport(location);

        for (int i = 0; i < 30; i++) {
            int finalI = i;

            ZenchantmentsPlugin.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(ZenchantmentsPlugin.getInstance(), () -> {
                for (int j = 0; j < 40; j++) {
                    if (entity.isDead()) {
                        return;
                    }
                    final Location cloned = location.clone();
                    final float t = 30 * finalI + j;
                    cloned.setY(cloned.getY() + (t / 100));
                    cloned.setX(cloned.getX() + Math.sin(Math.toRadians(t)) * t / 330);
                    cloned.setZ(cloned.getZ() + Math.cos(Math.toRadians(t)) * t / 330);

                    ThreadLocalRandom random = ThreadLocalRandom.current();

                    entity.getWorld().spawnParticle(
                        Particle.REDSTONE,
                        cloned,
                        1,
                        new Particle.DustOptions(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)), 1.0f)
                    );

                    cloned.setY(cloned.getY() + 1.3);

                    entity.setVelocity(cloned.toVector().subtract(entity.getLocation().toVector()));
                }
            }, i);
        }

        RAINBOW_SLAM_ENTITIES.add(entity);

        final AtomicBoolean applied = new AtomicBoolean(false);

        for (int i = 0; i < 3; i++) {
            ZenchantmentsPlugin.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(ZenchantmentsPlugin.getInstance(), () -> {
                entity.setVelocity(location.toVector().subtract(entity.getLocation().toVector()).multiply(0.3));
                entity.setFallDistance(0);

                if (!entity.isOnGround() || applied.get()) {
                    return;
                }

                applied.set(true);

                RAINBOW_SLAM_ENTITIES.remove(entity);

                WorldInteractionUtil.attackEntity(entity, event.getPlayer(), level * this.getPower());

                int numParticleGroups = (int) Math.pow(10, getPower());
                for (int c = 0; c < numParticleGroups; c++) {
                    entity.getWorld().spawnParticle(
                        Particle.BLOCK_DUST,
                        Utilities.getCenter(location),
                        10,
                        event.getPlayer().getLocation().getBlock().getBlockData()
                    );
                }
            }, 35 + i * 5);
        }

        return true;
    }
}
