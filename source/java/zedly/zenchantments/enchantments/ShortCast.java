package zedly.zenchantments.enchantments;

import com.google.common.collect.ImmutableSet;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;

import java.util.Collection;
import java.util.Set;

public final class ShortCast extends Zenchantment {
    public static final String KEY = "short_cast";

    private static final Set<Class<? extends Zenchantment>> CONFLICTING = ImmutableSet.of(LongCast.class);

    public ShortCast(
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
        final Entity entity = event.getEntity();
        if (entity.getType() == EntityType.FISHING_HOOK) {
            entity.setVelocity(entity.getVelocity().normalize().multiply((0.8f / (level * this.getPower()))));
        }
        return true;
    }
}
