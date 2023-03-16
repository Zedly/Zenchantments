package zedly.zenchantments.enchantments;

import com.google.common.collect.ImmutableSet;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;

import java.util.Collection;
import java.util.Set;

public final class LongCast extends Zenchantment {
    public static final String KEY = "long_cast";

    private static final Set<Class<? extends Zenchantment>> CONFLICTING = ImmutableSet.of(ShortCast.class);

    public LongCast(
        final @NotNull Set<Tool> enchantable,
        final int maxLevel,
        final int cooldown,
        final double probability,
        final float power
    ) {
        super(enchantable, maxLevel, cooldown, probability, power, CONFLICTING, KEY);
    }

    @Override
    public Collection<EquipmentSlot> getApplyToSlots() {
        return Slots.HANDS;
    }

    @Override
    public boolean onProjectileLaunch(final @NotNull ProjectileLaunchEvent event, final int level, final EquipmentSlot slot) {
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
