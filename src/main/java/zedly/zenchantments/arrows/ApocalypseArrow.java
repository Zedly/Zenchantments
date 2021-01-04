package zedly.zenchantments.arrows;

import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.ZenchantmentsPlugin;
import zedly.zenchantments.configuration.WorldConfiguration;

import static org.bukkit.Material.FIRE;
import static org.bukkit.entity.EntityType.BLAZE;
import static org.bukkit.potion.PotionEffectType.ABSORPTION;
import static org.bukkit.potion.PotionEffectType.HARM;

public final class ApocalypseArrow extends ZenchantedArrow {
    public ApocalypseArrow(final @NotNull ZenchantmentsPlugin plugin, final @NotNull Arrow entity) {
        super(plugin, entity);
    }

    @Override
    public void onImpact() {
        final WorldConfiguration config = this.getPlugin()
            .getWorldConfigurationProvider()
            .getConfigurationForWorld(this.getArrow().getWorld());

        final Location clone = this.getArrow().getLocation().clone();
        clone.setY(clone.getY() + 1);

        this.getArrow().getWorld().strikeLightning(clone);

        final Location[] locations = { this.getArrow().getLocation(), clone };
        for (int l = 0; l < locations.length; l++) {
            final Location location = locations[l];
            final int finalLs = l;
            for (int i = 0; i <= 45; i++) {
                int c = i + 1;
                this.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(
                    this.getPlugin(),
                    () -> {
                        final Entity entity = location.getWorld().spawnFallingBlock(location, this.getPlugin().getServer().createBlockData(FIRE));

                        final Vector vector = location.toVector();
                        vector.setY(Math.abs(Math.sin(c)));

                        if (finalLs % 2 == 0) {
                            vector.setZ((Math.sin(c) / 2));
                            vector.setX((Math.cos(c) / 2));
                        } else {
                            vector.setX((Math.sin(c) / 2));
                            vector.setZ((Math.cos(c) / 2));
                        }

                        entity.setVelocity(vector.multiply(1.5));

                        final TNTPrimed prime = (TNTPrimed) this.getArrow().getWorld().spawnEntity(location, EntityType.PRIMED_TNT);
                        prime.setFuseTicks(200);
                        prime.setYield(config.isExplosionBlockBreakEnabled() ? 4 : 0);

                        final Blaze blaze = (Blaze) this.getArrow().getWorld().spawnEntity(location, BLAZE);
                        blaze.addPotionEffect(new PotionEffect(ABSORPTION, 150, 100000));
                        blaze.addPotionEffect(new PotionEffect(HARM, 10000, 1));

                        if (config.isExplosionBlockBreakEnabled()) {
                            final Entity crystal = this.getArrow().getWorld().spawnEntity(location, EntityType.ENDER_CRYSTAL);
                            entity.addPassenger(prime);
                            crystal.addPassenger(blaze);
                            prime.addPassenger(crystal);
                        } else {
                            entity.addPassenger(prime);
                            prime.addPassenger(blaze);
                        }
                    },
                    c
                );
            }
        }

        this.die();
    }
}
