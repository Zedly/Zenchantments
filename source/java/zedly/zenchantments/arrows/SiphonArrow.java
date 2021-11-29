package zedly.zenchantments.arrows;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.ZenchantmentsPlugin;

import java.util.Objects;

public final class SiphonArrow extends ZenchantedArrow {
    public SiphonArrow(
        final @NotNull ZenchantmentsPlugin plugin,
        final @NotNull Arrow entity,
        final int level,
        final double power
    ) {
        super(plugin, entity, level, power);
    }

    @Override
    public boolean onImpact(final @NotNull EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof LivingEntity
            && this.getPlugin().getCompatibilityAdapter().attackEntity((LivingEntity) event.getEntity(), (Player) this.getArrow().getShooter(), 0)
        ) {
            ProjectileSource shooter = ((Projectile) event.getDamager()).getShooter();
            if (shooter == null || !(shooter instanceof Player)) {
                die();
                return false;
            }
            final Player player = (Player) shooter;
            int difference = (int) Math.round(0.17 * this.getLevel() * this.getPower() * event.getFinalDamage());
            player.setHealth(Math.min(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue(), player.getHealth() + difference));
        }

        this.die();
        return true;
    }
}
