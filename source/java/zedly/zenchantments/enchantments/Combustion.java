package zedly.zenchantments.enchantments;

import com.google.common.collect.ImmutableSet;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;

import java.util.Collection;
import java.util.Set;

public final class Combustion extends Zenchantment {
    public static final String KEY = "combustion";

    private static final Set<Class<? extends Zenchantment>> CONFLICTING = ImmutableSet.of();

    public Combustion(
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
    public boolean onBeingHit(final @NotNull EntityDamageByEntityEvent event, final int level, final EquipmentSlot slot) {
        final Entity entity;

        if (event.getDamager().getType() == EntityType.ARROW) {
            final AbstractArrow arrow = (AbstractArrow) event.getDamager();
            if (!(arrow.getShooter() instanceof LivingEntity)) {
                return false;
            }

            entity = (Entity) arrow.getShooter();
        } else {
            entity = event.getDamager();
        }

        return CompatibilityAdapter.instance()
            .igniteEntity(entity, (Player) event.getEntity(), (int) (50 * level * this.getPower()));
    }

    public boolean onCombust(final @NotNull EntityCombustByEntityEvent event, final int level, final EquipmentSlot slot) {
        if (CompatibilityAdapter.instance().isZombie(event.getCombuster())) {
            event.setDuration(0);
        }
        return false;
    }
}
