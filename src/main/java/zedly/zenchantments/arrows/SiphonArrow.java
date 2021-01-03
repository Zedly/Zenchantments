package zedly.zenchantments.arrows;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.ZenchantmentsPlugin;

import java.util.Objects;

public final class SiphonArrow extends ZenchantedArrow {
    public SiphonArrow(@NotNull ZenchantmentsPlugin plugin, @NotNull Arrow entity, int level, double power) {
        super(plugin, entity, level, power);
    }

    @Override
    public boolean onImpact(@NotNull EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof LivingEntity
            && this.getPlugin().getCompatibilityAdapter().attackEntity((LivingEntity) event.getEntity(), (Player) this.getArrow().getShooter(), 0)
        ) {
            Player player = (Player) Objects.requireNonNull(((Projectile) event.getDamager()).getShooter());
            int difference = (int) Math.round(0.17 * this.getLevel() * this.getPower() * event.getDamage());
            while (difference > 0) {
                if (player.getHealth() <= player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()) {
                    player.setHealth(player.getHealth() + 1);
                }

                difference--;
            }
        }

        this.die();
        return true;
    }
}
