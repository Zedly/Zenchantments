package zedly.zenchantments.arrows;

import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.WorldInteractionUtil;
import zedly.zenchantments.Utilities;

public final class ReaperArrow extends ZenchantedArrow {
    public ReaperArrow(
        final @NotNull Projectile entity,
        final int level,
        final double power
    ) {
        super(entity, level, power);
    }

    @Override
    public void onImpactEntity(final @NotNull ProjectileHitEvent event) {
        final LivingEntity entity = (LivingEntity) event.getHitEntity();
        if (WorldInteractionUtil.attackEntity(entity, (Player) this.getArrow().getShooter(), 0)) {
            final int power = (int) Math.round(this.getLevel() * this.getPower());
            final int duration = (int) Math.round(20 + this.getLevel() * 10 * this.getPower());
            Utilities.addPotionEffect(entity, PotionEffectType.WITHER, duration, power);
            Utilities.addPotionEffect(entity, PotionEffectType.BLINDNESS, duration, power);
            die(true);
        }
        die(false);
    }
}
