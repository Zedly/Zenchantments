package zedly.zenchantments.enchantments;

import com.google.common.collect.ImmutableSet;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.Hand;
import zedly.zenchantments.Tool;
import zedly.zenchantments.Zenchantment;
import zedly.zenchantments.ZenchantmentsPlugin;
import zedly.zenchantments.arrows.EnchantedArrow;
import zedly.zenchantments.arrows.enchanted.TracerArrow;
import zedly.zenchantments.task.EffectTask;
import zedly.zenchantments.task.Frequency;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class Tracer extends Zenchantment {
    public static final String KEY = "tracer";

    public static final Map<Arrow, Integer> TRACERS = new HashMap<>();

    private static final String                             NAME        = "Tracer";
    private static final String                             DESCRIPTION = "Guides the arrow to targets and then attacks";
    private static final Set<Class<? extends Zenchantment>> CONFLICTING = ImmutableSet.of();
    private static final Hand                               HAND_USE    = Hand.RIGHT;

    private final NamespacedKey key;

    public Tracer(
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
        TracerArrow arrow = new TracerArrow(this.getPlugin(), (Arrow) event.getProjectile(), level, this.getPower());
        EnchantedArrow.putArrow((Arrow) event.getProjectile(), arrow, (Player) event.getEntity());
        return true;
    }

    @EffectTask(Frequency.HIGH)
    public static void moveArrows() {
        for (Arrow arrow : TRACERS.keySet()) {
            Entity close = null;
            double distance = 100;
            int level = TRACERS.get(arrow);
            level += 2;
            for (Entity entity : arrow.getNearbyEntities(level, level, level)) {
                if (!entity.getWorld().equals(arrow.getWorld())) {
                    continue;
                }

                double d = entity.getLocation().distance(arrow.getLocation());
                Entity shooter = (Entity) arrow.getShooter();

                if (arrow.getWorld().equals(shooter.getWorld())) {
                    if (d < distance && entity instanceof LivingEntity
                        && !entity.equals(arrow.getShooter())
                        && arrow.getLocation().distance(shooter.getLocation()) > 15
                    ) {
                        distance = d;
                        close = entity;
                    }
                }
            }

            if (close != null) {
                Location location = close.getLocation();
                Location pos = arrow.getLocation();
                double its = location.distance(pos);

                if (its == 0) {
                    its = 1;
                }

                Vector vector = new Vector(0D, 0D, 0D);
                vector.setX((location.getX() - pos.getX()) / its);
                vector.setY((location.getY() - pos.getY()) / its);
                vector.setZ((location.getZ() - pos.getZ()) / its);
                vector.add(arrow.getLocation().getDirection().multiply(0.1));

                arrow.setVelocity(vector.multiply(2));
            }
        }
    }
}