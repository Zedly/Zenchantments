package zedly.zenchantments.enchantments;

import com.google.common.collect.ImmutableSet;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.Hand;
import zedly.zenchantments.Tool;
import zedly.zenchantments.Zenchantment;
import zedly.zenchantments.ZenchantmentsPlugin;

import java.util.Set;

public final class LongCast extends Zenchantment {
    public static final String KEY = "long_cast";

    private static final String                             NAME        = "Long Cast";
    private static final String                             DESCRIPTION = "Launches fishing hooks farther out when casting";
    private static final Set<Class<? extends Zenchantment>> CONFLICTING = ImmutableSet.of(ShortCast.class);
    private static final Hand                               HAND_USE    = Hand.RIGHT;

    private final NamespacedKey key;

    public LongCast(
        final @NotNull ZenchantmentsPlugin plugin,
        final @NotNull Set<Tool> enchantable,
        final int maxLevel,
        final int cooldown,
        final double power,
        final float probability
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
    public boolean onProjectileLaunch(@NotNull ProjectileLaunchEvent event, int level, boolean usedHand) {
        if (event.getEntity().getType() == EntityType.FISHING_HOOK) {
            event.getEntity().setVelocity(
                event.getEntity()
                    .getVelocity()
                    .normalize()
                    .multiply(Math.min(1.9 + (this.getPower() * level - 1.2), 2.7))
            );
        }
        return true;
    }
}
