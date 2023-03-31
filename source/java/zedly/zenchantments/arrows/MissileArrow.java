package zedly.zenchantments.arrows;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import zedly.zenchantments.Utilities;
import zedly.zenchantments.ZenchantmentsPlugin;
import zedly.zenchantments.configuration.WorldConfiguration;
import zedly.zenchantments.configuration.WorldConfigurationProvider;

import java.util.List;

import static org.bukkit.Material.AIR;

public final class MissileArrow extends ZenchantedArrow {
    public MissileArrow(final @NotNull Projectile entity) {
        super(entity);
    }

    @Override
    public void onLaunch(final @NotNull LivingEntity player, final @Nullable List<String> lore) {
        final WorldConfiguration config = WorldConfigurationProvider.getInstance()
            .getConfigurationForWorld(player.getWorld());

        final Location target = Utilities.getCenter(player.getTargetBlock(null, 220));
        target.setY(target.getY() + .5);

        final Location playerLocation = player.getLocation();
        playerLocation.setY(playerLocation.getY() + 1.1);

        final double distance = target.distance(playerLocation);

        for (int i = 9; i <= ((int) (distance * 5) + 9); i++) {
            final int finalI = i;
            Bukkit.getScheduler().scheduleSyncDelayedTask(
                ZenchantmentsPlugin.getInstance(),
                () -> {
                    final Location location1 = target.clone();
                    location1.setX(playerLocation.getX() + (finalI * ((target.getX() - playerLocation.getX()) / (distance * 5))));
                    location1.setY(playerLocation.getY() + (finalI * ((target.getY() - playerLocation.getY()) / (distance * 5))));
                    location1.setZ(playerLocation.getZ() + (finalI * ((target.getZ() - playerLocation.getZ()) / (distance * 5))));

                    final Location location2 = target.clone();
                    location2.setX(playerLocation.getX() + ((finalI + 10) * ((target.getX() - playerLocation.getX()) / (distance * 5))));
                    location2.setY(playerLocation.getY() + ((finalI + 10) * ((target.getY() - playerLocation.getY()) / (distance * 5))));
                    location2.setZ(playerLocation.getZ() + ((finalI + 10) * ((target.getZ() - playerLocation.getZ()) / (distance * 5))));

                    Utilities.displayParticle(location1, Particle.FLAME, 10, .001f, 0, 0, 0);
                    Utilities.displayParticle(location1, Particle.FLAME, 1, .1f, 0, 0, 0);

                    if (finalI % 50 == 0) {
                        target.getWorld().playSound(location1, Sound.ENTITY_WITHER_SPAWN, 10f, .1f);
                    }

                    if (finalI >= ((int) (distance * 5) + 9) || location2.getBlock().getType() != AIR) {
                        Utilities.displayParticle(location2, Particle.EXPLOSION_HUGE, 10, 0.1f, 0, 0, 0);
                        Utilities.displayParticle(location1, Particle.FLAME, 175, 1f, 0, 0, 0);
                        location2.setY(location2.getY() + 5);
                        location2.getWorld().createExplosion(
                            location2.getX(),
                            location2.getY(),
                            location2.getZ(),
                            10,
                            config.isExplosionBlockBreakEnabled(),
                            config.isExplosionBlockBreakEnabled()
                        );
                    }
                },
                i / 7
            );
        }
    }
}
