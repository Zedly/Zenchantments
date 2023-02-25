package zedly.zenchantments.enchantments;

import com.google.common.collect.ImmutableSet;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;
import zedly.zenchantments.arrows.StationaryArrow;
import zedly.zenchantments.arrows.ZenchantedArrow;

import java.util.Collection;
import java.util.Set;

public final class Stationary extends Zenchantment {
    public static final String KEY = "stationary";

    private static final String                             NAME        = "Stationary";
    private static final String                             DESCRIPTION = "Negates any knockback when attacking mobs, leaving them clueless as to who is attacking";
    private static final Set<Class<? extends Zenchantment>> CONFLICTING = ImmutableSet.of();
    private static final Hand                               HAND_USE    = Hand.BOTH;

    private final NamespacedKey key;

    public Stationary(
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
        return Slots.HANDS;
    }

    @Override
    public boolean onEntityHit(final @NotNull EntityDamageByEntityEvent event, final int level, final EquipmentSlot slot) {
        if (event.getEntity() instanceof LivingEntity
            && !CompatibilityAdapter.instance().attackEntity((LivingEntity) event.getEntity(), (Player) event.getDamager(), 0)
        ) {
            return true;
        }

        final LivingEntity entity = (LivingEntity) event.getEntity();

        if (event.getDamage() < entity.getHealth()) {
            event.setCancelled(true);
            entity.damage(event.getDamage());
            Utilities.damageItemStackRespectUnbreaking(((Player) event.getDamager()), 1, slot);
        }

        return true;
    }

    @Override
    public boolean onEntityShootBow(final @NotNull EntityShootBowEvent event, final int level, final EquipmentSlot slot) {
        final StationaryArrow arrow = new StationaryArrow((AbstractArrow) event.getProjectile());
        ZenchantedArrow.putArrow((AbstractArrow) event.getProjectile(), arrow, (Player) event.getEntity());
        return true;
    }
}
