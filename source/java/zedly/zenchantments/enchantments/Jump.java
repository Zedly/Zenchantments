package zedly.zenchantments.enchantments;

import com.google.common.collect.ImmutableSet;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;

import java.util.Collection;
import java.util.Set;

import static org.bukkit.potion.PotionEffectType.JUMP;

public final class Jump extends Zenchantment {
    public static final String KEY = "jump";

    private static final Set<Class<? extends Zenchantment>> CONFLICTING = ImmutableSet.of();

    public Jump(
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
    public boolean onScan(final @NotNull Player player, final int level, final EquipmentSlot slot) {
        Utilities.addPotionEffect(player, JUMP, 610, (int) Math.round(level * this.getPower()));
        return true;
    }
}
