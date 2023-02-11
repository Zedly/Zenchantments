package zedly.zenchantments.arrows;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.CompatibilityAdapter;
import zedly.zenchantments.Utilities;

public final class ReaperArrow extends ZenchantedArrow {
    public ReaperArrow(
        final @NotNull AbstractArrow entity,
        final int level,
        final double power
    ) {
        super(entity, level, power);
    }

    @Override
    public boolean onImpact(final @NotNull EntityDamageByEntityEvent event) {
        final LivingEntity entity = (LivingEntity) event.getEntity();
        if (CompatibilityAdapter.instance().attackEntity(entity, (Player) this.getArrow().getShooter(), 0)) {
            final int power = (int) Math.round(this.getLevel() * this.getPower());
            final int duration = (int) Math.round(20 + this.getLevel() * 10 * this.getPower());
            Utilities.addPotionEffect(entity, PotionEffectType.WITHER, duration, power);
            Utilities.addPotionEffect(entity, PotionEffectType.BLINDNESS, duration, power);
        }

        this.die();
        return true;
    }
}
