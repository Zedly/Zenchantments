package zedly.zenchantments.enchantments;

import com.google.common.collect.ImmutableSet;
import org.bukkit.NamespacedKey;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;

import java.util.Collection;
import java.util.Set;

public final class Bind extends Zenchantment {
    public static final String KEY = "bind";

    private static final String                             NAME        = "Bind";
    private static final String                             DESCRIPTION = "Keeps items with the enchantment in your inventory after death";
    private static final Set<Class<? extends Zenchantment>> CONFLICTING = ImmutableSet.of();

    private final NamespacedKey key;

    public Bind(
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
