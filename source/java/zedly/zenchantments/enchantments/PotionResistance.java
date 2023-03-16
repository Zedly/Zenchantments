package zedly.zenchantments.enchantments;

import com.google.common.collect.ImmutableSet;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;
import zedly.zenchantments.configuration.WorldConfigurationProvider;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public final class PotionResistance extends Zenchantment {
    public static final String KEY = "potion_resistance";

    private static final Set<Class<? extends Zenchantment>> CONFLICTING = ImmutableSet.of();

    public PotionResistance(
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
    public boolean onPotionSplash(final @NotNull PotionSplashEvent event, final int level, final EquipmentSlot slot) {
        for (final LivingEntity entity : event.getAffectedEntities()) {
            if (!(entity instanceof Player)) {
                continue;
            }

            int effect = 0;

            for (final ItemStack stack : ((Player) entity).getInventory().getArmorContents()) {
                final Map<Zenchantment, Integer> map = Zenchantment.getZenchantmentsOnItemStack(
                    stack,
                    WorldConfigurationProvider.getInstance().getConfigurationForWorld(entity.getWorld())
                );

                for (final Zenchantment zenchantment : map.keySet()) {
                    if (zenchantment.equals(this)) {
                        effect += map.get(zenchantment);
                    }
                }
            }

            event.setIntensity(entity, event.getIntensity(entity) / ((effect * this.getPower() + 1.3) / 2));
        }

        return true;
    }
}
