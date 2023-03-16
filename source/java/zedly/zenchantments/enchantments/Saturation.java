package zedly.zenchantments.enchantments;

import com.google.common.collect.ImmutableSet;
import org.bukkit.NamespacedKey;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public final class Saturation extends Zenchantment {
    public static final String KEY = "saturation";

    private static final Set<Class<? extends Zenchantment>> CONFLICTING = ImmutableSet.of();

    public Saturation(
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
        return Slots.ARMOR;
    }

    @Override
    public boolean onHungerChange(final @NotNull FoodLevelChangeEvent event, final int level, final EquipmentSlot slot) {
        if (event.getFoodLevel() < event.getEntity().getFoodLevel()
            && ThreadLocalRandom.current().nextInt(10) > 10 - 2 * level * this.getPower()
        ) {
            event.setCancelled(true);
        }

        return true;
    }
}
