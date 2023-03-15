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
import org.bukkit.event.entity.ProjectileHitEvent;
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
    public void onImpactEntity(final @NotNull ProjectileHitEvent event) {
        if (!CompatibilityAdapter.instance().attackEntity((LivingEntity) event.getHitEntity(), (Player) this.getArrow().getShooter(), 0)) {
             die(true);
        }

        if (event.getHitEntity().getType() == EntityType.CREEPER) {
            final Creeper creeper = (Creeper) event.getHitEntity();
            creeper.explode();
            event.setCancelled(true);
        } else if (event.getHitEntity().getType() == EntityType.MUSHROOM_COW) {
            final MushroomCow mooshroom = (MushroomCow) event.getHitEntity();

            if (mooshroom.isAdult()) {
                final Location location = event.getHitEntity().getLocation();

                Utilities.displayParticle(location, Particle.EXPLOSION_LARGE, 1, 1f, 0, 0, 0);
                event.getHitEntity().remove();
                location.getWorld().spawnEntity(location, EntityType.COW);
                location.getWorld().dropItemNaturally(location, new ItemStack(Material.RED_MUSHROOM, 5));
                event.setCancelled(true);
            }
        }

        die(true);
    }
}
