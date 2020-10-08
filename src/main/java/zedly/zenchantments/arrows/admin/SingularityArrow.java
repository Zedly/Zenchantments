package zedly.zenchantments.arrows.admin;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import zedly.zenchantments.Storage;
import zedly.zenchantments.Utilities;
import zedly.zenchantments.arrows.EnchantedArrow;
import zedly.zenchantments.enchantments.Singularity;

public class SingularityArrow extends EnchantedArrow {

    public SingularityArrow(Arrow entity, int level) {
        super(entity, level);
    }

    public void onImpact() {
        Location location = this.getArrow().getLocation().clone();

        Singularity.SINGULARITIES.put(location, true);

        Bukkit.getScheduler().scheduleSyncDelayedTask(
            Storage.zenchantments,
            () -> Singularity.SINGULARITIES.put(location, false),
            40
        );

        Bukkit.getScheduler().scheduleSyncDelayedTask(
            Storage.zenchantments,
            () -> Singularity.SINGULARITIES.remove(location),
            60
        );

        for (int i = 1; i <= 61; i++) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(
                Storage.zenchantments,
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