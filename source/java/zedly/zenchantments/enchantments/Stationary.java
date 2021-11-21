package zedly.zenchantments.enchantments;

import com.google.common.collect.ImmutableSet;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;
import zedly.zenchantments.arrows.StationaryArrow;
import zedly.zenchantments.arrows.ZenchantedArrow;

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
    @NotNull
    public Hand getHandUse() {
        return HAND_USE;
    }

    @Override
    public boolean onEntityHit(final @NotNull EntityDamageByEntityEvent event, final int level, final boolean usedHand) {
        if (event.getEntity() instanceof LivingEntity
            && !ZenchantmentsPlugin.getInstance().getCompatibilityAdapter().attackEntity((LivingEntity) event.getEntity(), (Player) event.getDamager(), 0)
        ) {
            return true;
        }

        final LivingEntity entity = (LivingEntity) event.getEntity();

        if (event.getDamage() < entity.getHealth()) {
            event.setCancelled(true);
            entity.damage(event.getDamage());
            Utilities.damageItemStack(((Player) event.getDamager()), 1, usedHand);
        }

        return true;
    }

    @Override
    public boolean onEntityShootBow(final @NotNull EntityShootBowEvent event, final int level, final boolean usedHand) {
        final StationaryArrow arrow = new StationaryArrow(ZenchantmentsPlugin.getInstance(), (Arrow) event.getProjectile());
        ZenchantedArrow.putArrow((Arrow) event.getProjectile(), arrow, (Player) event.getEntity());
        return true;
    }
}
