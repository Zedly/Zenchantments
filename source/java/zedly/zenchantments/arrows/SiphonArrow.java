package zedly.zenchantments.arrows;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.CompatibilityAdapter;

public final class SiphonArrow extends ZenchantedArrow {
    public SiphonArrow(
        final @NotNull AbstractArrow entity,
        final int level,
        final double power
    ) {
        super(entity, level, power);
    }

    @Override
    public void onDamageEntity(final @NotNull EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof LivingEntity
            && CompatibilityAdapter.instance().attackEntity((LivingEntity) event.getEntity(), (Player) this.getArrow().getShooter(), 0)
        ) {
            Entity shooter = event.getDamager();
            if (shooter == null || !(shooter instanceof Player)) {
                die(true);
                return;
            }
            final Player player = (Player) shooter;
            int difference = (int) Math.round(0.17 * this.getLevel() * this.getPower() * event.getFinalDamage());
            player.setHealth(Math.min(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue(), player.getHealth() + difference));
        }

        this.die(true);
    }
}
