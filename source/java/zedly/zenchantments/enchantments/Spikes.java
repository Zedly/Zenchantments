package zedly.zenchantments.enchantments;

import com.google.common.collect.ImmutableSet;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;

import java.util.Collection;
import java.util.Set;

public final class Spikes extends Zenchantment {
    public static final String KEY = "spikes";

    private static final Set<Class<? extends Zenchantment>> CONFLICTING = ImmutableSet.of();

    public Spikes(
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
    public boolean onFastScan(final @NotNull Player player, final int level, final EquipmentSlot slot) {
        if (!(player.getVelocity().getY() < -0.45)) {
            return true;
        }

        for (final Entity entity : player.getNearbyEntities(1, 2, 1)) {
            final double fall = Math.min(player.getFallDistance(), 20.0);
            if (entity instanceof LivingEntity) {
                CompatibilityAdapter.instance().attackEntity((LivingEntity) entity, player, this.getPower() * level * fall * 0.25);
                Utilities.damageItemStackRespectUnbreaking(player, 1, EquipmentSlot.FEET);
            }
        }

        return true;
    }
}
