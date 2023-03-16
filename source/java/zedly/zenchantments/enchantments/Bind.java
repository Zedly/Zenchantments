package zedly.zenchantments.enchantments;

import com.google.common.collect.ImmutableSet;
import org.bukkit.NamespacedKey;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;

import java.util.Collection;
import java.util.Set;

import static zedly.zenchantments.I18n.translateString;

public final class Bind extends Zenchantment {
    public static final String KEY = "bind";

    private static final Set<Class<? extends Zenchantment>> CONFLICTING = ImmutableSet.of();

    public Bind(
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
        return Slots.ALL;
    }

    @Override
    public boolean onPlayerDeath(final @NotNull PlayerDeathEvent event, final int level, final EquipmentSlot slot) {
        // Method body moved to ZenchantmentListener.onDeath because this is the only enchant that needs it anyway and we're
        // missing access to some data here that's readily available in the listener
        return false;
    }
}
