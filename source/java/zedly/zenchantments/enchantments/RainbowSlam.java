package zedly.zenchantments.enchantments;

import com.google.common.collect.ImmutableSet;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;

public final class RainbowSlam extends Zenchantment {
    public static final String KEY = "rainbow_slam";

    public static final Set<Entity> RAINBOW_SLAM_ENTITIES = new HashSet<>();

    private static final String                             NAME        = "Rainbow Slam";
    private static final String                             DESCRIPTION = "Attacks enemy mobs with a powerful swirling slam";
    private static final Set<Class<? extends Zenchantment>> CONFLICTING = ImmutableSet.of(Force.class, Gust.class);
    private static final Hand                               HAND_USE    = Hand.RIGHT;

    private final NamespacedKey key;

    public RainbowSlam(
        final @NotNull Set<Tool> enchantable,
        final int maxLevel,
        final int cooldown,
        final double probability,
        final float power
    ) {
        super(enchantable, maxLevel, cooldown, probability, power);
        this.key = new NamespacedKey(ZenchantmentsPlugin.getInstance(), KEY);
    }

    @Override
    @NotNull
    public NamespacedKey getKey() {
        return this.key;
    }

    @Override
    @NotNull
    public String getName() {
        return NAME;
    }

    @Override
    @NotNull
    public String getDescription() {
        return DESCRIPTION;
    }

    @Override
    @NotNull
    public Set<Class<? extends Zenchantment>> getConflicting() {
        return CONFLICTING;
    }

    @Override
    @NotNull
    public Hand getHandUse() {
        return HAND_USE;
    }

    @Override
    public boolean onEntityInteract(final @NotNull PlayerInteractEntityEvent event, final int level, final boolean usedHand) {
        if (!(event.getRightClicked() instanceof LivingEntity)
            || !ZenchantmentsPlugin.getInstance().getCompatibilityAdapter().attackEntity((LivingEntity) event.getRightClicked(), event.getPlayer(), 0)
        ) {
            return false;
        }

        Utilities.damageItemStack(event.getPlayer(), 9, usedHand);

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

                ZenchantmentsPlugin.getInstance().getCompatibilityAdapter().attackEntity(entity, event.getPlayer(), level * this.getPower());

                for (int c = 0; c < 1000; c++) {
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
