package zedly.zenchantments.arrows;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.data.type.Campfire;
import org.bukkit.block.data.type.Candle;
import org.bukkit.entity.*;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.CompatibilityAdapter;
import zedly.zenchantments.Utilities;
import zedly.zenchantments.configuration.WorldConfigurationProvider;

import java.util.Objects;

import static org.bukkit.Material.*;

public final class FuseArrow extends ZenchantedArrow {
    public FuseArrow(final @NotNull AbstractArrow entity) {
        super(entity);
    }

    @Override
    public void onImpact() {
        final Location location = this.getArrow().getLocation();
        for (int i = 1; i < 5; i++) {
            final Vector vector = this.getArrow().getVelocity().multiply(0.25 * i);
            final Location hitLocation = new Location(
                location.getWorld(),
                location.getX() + vector.getX(),
                location.getY() + vector.getY(),
                location.getZ() + vector.getZ()
            );

            if (hitLocation.getBlock().getType() == TNT) {
                final BlockBreakEvent event = new BlockBreakEvent(
                    hitLocation.getBlock(),
                    (Player) Objects.requireNonNull(this.getArrow().getShooter())
                );

                Bukkit.getPluginManager().callEvent(event);

                if (!event.isCancelled()) {
                    hitLocation.getBlock().setType(AIR);
                    hitLocation.getWorld().spawnEntity(hitLocation, EntityType.PRIMED_TNT);
                    this.die();
                }

                return;
            } else if (hitLocation.getBlock().getBlockData() instanceof final Candle candle) {
                candle.setLit(true);
                hitLocation.getBlock().setBlockData(candle);
             } else if (hitLocation.getBlock().getBlockData() instanceof final Campfire campfire) {
                campfire.setLit(true);
                hitLocation.getBlock().setBlockData(campfire);
            }
        }

        this.die();
    }

    @Override
    public boolean onImpact(final @NotNull EntityDamageByEntityEvent event) {
        if (!CompatibilityAdapter.instance().attackEntity((LivingEntity) event.getEntity(), (Player) this.getArrow().getShooter(), 0)) {
            this.die();
            return true;
        }

        if (event.getEntity().getType() == EntityType.CREEPER) {
            final Creeper creeper = (Creeper) event.getEntity();
            CompatibilityAdapter.instance().explodeCreeper(
                creeper,
                    WorldConfigurationProvider.getInstance()
                        .getConfigurationForWorld(event.getDamager().getWorld())
                    .isExplosionBlockBreakEnabled()
            );
        } else if (event.getEntity().getType() == EntityType.MUSHROOM_COW) {
            final MushroomCow mooshroom = (MushroomCow) event.getEntity();

            if (mooshroom.isAdult()) {
                final Location location = event.getEntity().getLocation();

                Utilities.displayParticle(location, Particle.EXPLOSION_LARGE, 1, 1f, 0, 0, 0);
                event.getEntity().remove();
                location.getWorld().spawnEntity(location, EntityType.COW);
                location.getWorld().dropItemNaturally(location, new ItemStack(Material.RED_MUSHROOM, 5));
            }
        }

        this.die();
        return true;
    }
}
