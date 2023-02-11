package zedly.zenchantments.arrows;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.Utilities;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

import static org.bukkit.potion.PotionEffectType.*;

public final class PotionArrow extends ZenchantedArrow {
    private static final PotionEffectType[] POTION_EFFECT_TYPES = {
        ABSORPTION,
        DAMAGE_RESISTANCE,
        FIRE_RESISTANCE,
        SPEED,
        JUMP,
        INVISIBILITY,
        INCREASE_DAMAGE,
        HEALTH_BOOST,
        HEAL,
        REGENERATION,
        NIGHT_VISION,
        SATURATION,
        FAST_DIGGING,
        WATER_BREATHING,
        DOLPHINS_GRACE
    };

    public PotionArrow(
        final @NotNull AbstractArrow entity,
        final int level,
        final double power
    ) {
        super(entity, level, power);
    }

    @Override
    public boolean onImpact(final @NotNull EntityDamageByEntityEvent event) {
        if (ThreadLocalRandom.current().nextInt((int) Math.round(10 / (this.getLevel() * this.getPower() + 1))) == 1) {
            Utilities.addPotionEffect(
                (LivingEntity) Objects.requireNonNull(this.getArrow().getShooter()),
                POTION_EFFECT_TYPES[ThreadLocalRandom.current().nextInt(15)],
                150 + (int) Math.round(this.getLevel() * this.getPower() * 50),
                (int) Math.round(this.getLevel() * this.getPower())
            );
        }

        this.die();
        return true;
    }
}
