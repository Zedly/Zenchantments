package zedly.zenchantments.enchantments;

import com.google.common.collect.ImmutableSet;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;
import zedly.zenchantments.arrows.ReaperArrow;
import zedly.zenchantments.arrows.ZenchantedArrow;

import java.util.Set;

import static org.bukkit.potion.PotionEffectType.BLINDNESS;

public final class Reaper extends Zenchantment {
    public static final String KEY = "reaper";

    private static final String                             NAME        = "Reaper";
    private static final String                             DESCRIPTION = "Gives the target temporary wither effect and blindness";
    private static final Set<Class<? extends Zenchantment>> CONFLICTING = ImmutableSet.of();
    private static final Hand                               HAND_USE    = Hand.BOTH;

    private final NamespacedKey key;

    public Reaper(
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
        if (!(event.getEntity() instanceof LivingEntity)
            || !CompatibilityAdapter.instance().attackEntity((LivingEntity) event.getEntity(), (Player) event.getDamager(), 0)
        ) {
            return true;
        }

        final int power = (int) Math.round(level * this.getPower());
        final int length = (int) Math.round(10 + level * 20 * this.getPower());
        Utilities.addPotionEffect((LivingEntity) event.getEntity(), PotionEffectType.WITHER, length, power);
        Utilities.addPotionEffect((LivingEntity) event.getEntity(), BLINDNESS, length, power);

        return true;
    }

    @Override
    public boolean onEntityShootBow(final @NotNull EntityShootBowEvent event, final int level, final boolean usedHand) {
        final ReaperArrow arrow = new ReaperArrow((AbstractArrow) event.getProjectile(), level, this.getPower());
        ZenchantedArrow.putArrow((AbstractArrow) event.getProjectile(), arrow, (Player) event.getEntity());
        return true;
    }
}
