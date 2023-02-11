package zedly.zenchantments.arrows;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.Utilities;
import zedly.zenchantments.ZenchantmentsPlugin;
import zedly.zenchantments.enchantments.Singularity;

public final class SingularityArrow extends ZenchantedArrow {
    public SingularityArrow(
        final @NotNull AbstractArrow entity,
        final int level
    ) {
        super(entity, level);
    }

    @Override
    public void onImpact() {
        final Location location = this.getArrow().getLocation().clone();

        Singularity.SINGULARITIES.put(location, true);

        Bukkit.getScheduler().scheduleSyncDelayedTask(
            ZenchantmentsPlugin.getInstance(),
            () -> Singularity.SINGULARITIES.put(location, false),
            40
        );

        Bukkit.getScheduler().scheduleSyncDelayedTask(
            ZenchantmentsPlugin.getInstance(),
            () -> Singularity.SINGULARITIES.remove(location),
            60
        );

        for (int i = 1; i <= 61; i++) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(
                ZenchantmentsPlugin.getInstance(),
                () -> {
                    Utilities.displayParticle(location, Particle.SMOKE_LARGE, 50, 0.001f, 0.75f, 0.75f, 0.75f);
                    location.getWorld().playSound(location, Sound.ENTITY_ENDER_DRAGON_GROWL, 10f, 0.1f);
                },
                i
            );
        }

        this.die();
    }
}
