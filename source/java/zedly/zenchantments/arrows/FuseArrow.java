package zedly.zenchantments.arrows;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.*;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.WorldInteractionUtil;
import zedly.zenchantments.Utilities;

public final class FuseArrow extends ZenchantedArrow {
    public FuseArrow(final @NotNull Projectile entity) {
        super(entity);
    }

    @Override
    public void onImpactEntity(final @NotNull ProjectileHitEvent event) {
        if (!WorldInteractionUtil.attackEntity((LivingEntity) event.getHitEntity(), (Player) this.getArrow().getShooter(), 0)) {
             die(true);
        }

        if (event.getHitEntity().getType() == EntityType.CREEPER) {
            final Creeper creeper = (Creeper) event.getHitEntity();
            creeper.explode();
            event.setCancelled(true);
            die(true);
        } else if (event.getHitEntity().getType() == EntityType.MUSHROOM_COW) {
            final MushroomCow mooshroom = (MushroomCow) event.getHitEntity();

            if (mooshroom.isAdult()) {
                final Location location = event.getHitEntity().getLocation();

                Utilities.displayParticle(location, Particle.EXPLOSION_LARGE, 1, 1f, 0, 0, 0);
                event.getHitEntity().remove();
                location.getWorld().spawnEntity(location, EntityType.COW);
                location.getWorld().dropItemNaturally(location, new ItemStack(Material.RED_MUSHROOM, 5));
                event.setCancelled(true);
                die(true);
            }
        }

        die(false);
    }
}
