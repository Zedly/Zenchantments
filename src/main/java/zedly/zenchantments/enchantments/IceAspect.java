package zedly.zenchantments.enchantments;

import com.google.common.collect.ImmutableSet;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;

import java.util.Set;

import static org.bukkit.potion.PotionEffectType.SLOW;

public final class IceAspect extends Zenchantment {
    public static final String KEY = "ice_aspect";

    private static final String                             NAME        = "Ice Aspect";
    private static final String                             DESCRIPTION = "Temporarily freezes the target";
    private static final Set<Class<? extends Zenchantment>> CONFLICTING = ImmutableSet.of();
    private static final Hand                               HAND_USE    = Hand.LEFT;

    private final NamespacedKey key;

    public IceAspect(
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
        Utilities.addPotion(
            (LivingEntity) event.getEntity(),
            SLOW,
            (int) Math.round(40 + level * this.getPower() * 40),
            (int) Math.round(this.getPower() * level * 2)
        );
        Utilities.display(Utilities.getCenter(event.getEntity().getLocation()), Particle.CLOUD, 10, 0.1f, 1f, 2f, 1f);
        return true;
    }
}