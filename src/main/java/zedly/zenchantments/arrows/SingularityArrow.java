package zedly.zenchantments.arrows;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.Utilities;
import zedly.zenchantments.ZenchantmentsPlugin;
import zedly.zenchantments.enchantments.Singularity;

public class SingularityArrow extends ZenchantedArrow {
    public SingularityArrow(@NotNull ZenchantmentsPlugin plugin, @NotNull Arrow entity, int level) {
        super(plugin, entity, level);
    }

    @Override
    public void onImpact() {
        Location location = this.getArrow().getLocation().clone();

        Singularity.SINGULARITIES.put(location, true);

        this.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(
            this.getPlugin(),
            () -> Singularity.SINGULARITIES.put(location, false),
            40
        );

        this.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(
            this.getPlugin(),
            () -> Singularity.SINGULARITIES.remove(location),
            60
        );

        for (int i = 1; i <= 61; i++) {
            this.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(
                this.getPlugin(),
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