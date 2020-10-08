package zedly.zenchantments.enchantments;

import com.google.common.collect.ImmutableSet;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.Hand;
import zedly.zenchantments.Tool;
import zedly.zenchantments.Zenchantment;
import zedly.zenchantments.ZenchantmentsPlugin;
import zedly.zenchantments.arrows.EnchantedArrow;
import zedly.zenchantments.arrows.admin.SingularityArrow;
import zedly.zenchantments.task.EffectTask;
import zedly.zenchantments.task.Frequency;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public final class Singularity extends Zenchantment {
    public static final String KEY = "singularity";

    public static final Map<Location, Boolean> SINGULARITIES = new HashMap<>();

    private static final String                             NAME        = "Singularity";
    private static final String                             DESCRIPTION = "Creates a black hole that attracts nearby entities and then discharges them";
    private static final Set<Class<? extends Zenchantment>> CONFLICTING = ImmutableSet.of();
    private static final Hand                               HAND_USE    = Hand.RIGHT;

    private final NamespacedKey key;

    public Singularity(
        @NotNull ZenchantmentsPlugin plugin,
        @NotNull Set<Tool> enchantable,
        int maxLevel,
        int cooldown,
        double power,
        float probability
    ) {
        super(plugin, enchantable, maxLevel, cooldown, power, probability);
        this.key = new NamespacedKey(plugin, KEY);
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
    public boolean onEntityShootBow(@NotNull EntityShootBowEvent event, int level, boolean usedHand) {
        SingularityArrow arrow = new SingularityArrow(this.getPlugin(), (Arrow) event.getProjectile(), level);
        EnchantedArrow.putArrow((Arrow) event.getProjectile(), arrow, (Player) event.getEntity());
        return true;
    }

    @EffectTask(Frequency.HIGH)
    public static void singularityPhysics() {
        for (Location location : SINGULARITIES.keySet()) {
            for (Entity entity : location.getWorld().getNearbyEntities(location, 10, 10, 10)) {
                if (entity instanceof Player) {
                    if (((Player) entity).getGameMode() == GameMode.CREATIVE) {
                        continue;
                    }
                }

                ThreadLocalRandom random = ThreadLocalRandom.current();

                if (SINGULARITIES.get(location)) {
                    Vector vector = location.clone().subtract(entity.getLocation()).toVector();
                    vector.setX(vector.getX() + (-0.5f + random.nextFloat()) * 10);
                    vector.setY(vector.getY() + (-0.5f + random.nextFloat()) * 10);
                    vector.setZ(vector.getZ() + (-0.5f + random.nextFloat()) * 10);

                    entity.setVelocity(vector.multiply(.35f));
                    entity.setFallDistance(0);
                } else {
                    Vector vector = entity.getLocation().subtract(location.clone()).toVector();
                    vector.setX(vector.getX() + (-0.5f + random.nextFloat()) * 2);
                    vector.setY(vector.getY() + random.nextFloat());
                    vector.setZ(vector.getZ() + (-0.5f + random.nextFloat()) * 2);

                    entity.setVelocity(vector.multiply(.35f));
                }
            }
        }
    }
}