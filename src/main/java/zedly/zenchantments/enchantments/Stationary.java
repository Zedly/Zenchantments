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
import zedly.zenchantments.arrows.EnchantedArrow;
import zedly.zenchantments.arrows.enchanted.StationaryArrow;

import java.util.Set;

public final class Stationary extends Zenchantment {
    public static final String KEY = "stationary";

    private static final String                             NAME        = "Stationary";
    private static final String                             DESCRIPTION = "Negates any knockback when attacking mobs, leaving them clueless as to who is attacking";
    private static final Set<Class<? extends Zenchantment>> CONFLICTING = ImmutableSet.of();
    private static final Hand                               HAND_USE    = Hand.BOTH;

    private final NamespacedKey key;

    public Stationary(
        @NotNull ZenchantmentsPlugin plugin,
        @NotNull Set<Tool> enchantable,
        int maxLevel,
        int cooldown,
        double power,
        float probability
    ) {
        super(plugin, enchantable, maxLevel, cooldown, power, probability);
        this.key = new NamespacedKey(plugin, KEY);
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
    public boolean onEntityHit(@NotNull EntityDamageByEntityEvent event, int level, boolean usedHand) {
        if (event.getEntity() instanceof LivingEntity
            && !ADAPTER.attackEntity((LivingEntity) event.getEntity(), (Player) event.getDamager(), 0)
        ) {
            return true;
        }

        LivingEntity entity = (LivingEntity) event.getEntity();

        if (event.getDamage() < entity.getHealth()) {
            event.setCancelled(true);
            entity.damage(event.getDamage());
            Utilities.damageTool(((Player) event.getDamager()), 1, usedHand);
        }

        return true;
    }

    @Override
    public boolean onEntityShootBow(@NotNull EntityShootBowEvent event, int level, boolean usedHand) {
        StationaryArrow arrow = new StationaryArrow(this.getPlugin(), (Arrow) event.getProjectile());
        EnchantedArrow.putArrow((Arrow) event.getProjectile(), arrow, (Player) event.getEntity());
        return true;
    }
}