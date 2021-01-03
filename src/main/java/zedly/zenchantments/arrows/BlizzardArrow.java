package zedly.zenchantments.arrows;

import org.bukkit.Particle;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.Utilities;
import zedly.zenchantments.ZenchantmentsPlugin;

import static org.bukkit.potion.PotionEffectType.SLOW;

public final class BlizzardArrow extends ZenchantedArrow {
    public BlizzardArrow(@NotNull ZenchantmentsPlugin plugin, @NotNull Arrow entity, int level, double power) {
        super(plugin, entity, level, power);
    }

    @Override
    public void onImpact() {
        Utilities.displayParticle(
            Utilities.getCenter(this.getArrow().getLocation()),
            Particle.CLOUD,
            100 * this.getLevel(),
            0.1f,
            this.getLevel(),
            1.5f,
            this.getLevel()
        );

        double radius = 1 + this.getLevel() * this.getPower();
        for (Entity entity : this.getArrow().getNearbyEntities(radius, radius, radius)) {
            if (!(entity instanceof LivingEntity)
                || entity.equals(this.getArrow().getShooter())
                || !this.getPlugin().getCompatibilityAdapter().attackEntity((LivingEntity) entity, (Player) this.getArrow().getShooter(), 0)
            ) {
                continue;
            }

            Utilities.addPotionEffect(
                (LivingEntity) entity,
                SLOW,
                (int) Math.round(50 + this.getLevel() * this.getPower() * 50),
                (int) Math.round(this.getLevel() * this.getPower() * 2)
            );
        }

        this.die();
    }
}
