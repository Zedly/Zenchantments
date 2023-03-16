package zedly.zenchantments.enchantments;

import com.google.common.collect.ImmutableSet;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;

import java.util.Collection;
import java.util.Set;

import static org.bukkit.potion.PotionEffectType.SLOW;

public final class IceAspect extends Zenchantment {
    public static final String KEY = "ice_aspect";

    private static final Set<Class<? extends Zenchantment>> CONFLICTING = ImmutableSet.of();

    public IceAspect(
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
        return Slots.MAIN_HAND;
    }

    @Override
    public boolean onEntityHit(final @NotNull EntityDamageByEntityEvent event, final int level, final EquipmentSlot slot) {
        Utilities.addPotionEffect(
            (LivingEntity) event.getEntity(),
            SLOW,
            (int) Math.round(40 + level * this.getPower() * 40),
            (int) Math.round(this.getPower() * level * 2)
        );
        Utilities.displayParticle(Utilities.getCenter(event.getEntity().getLocation()), Particle.CLOUD, 10, 0.1f, 1f, 2f, 1f);
        return true;
    }
}
