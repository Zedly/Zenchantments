package zedly.zenchantments.arrows;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Campfire;
import org.bukkit.block.data.type.Candle;
import org.bukkit.entity.*;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
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
