package zedly.zenchantments.arrows;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.*;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.Utilities;
import zedly.zenchantments.ZenchantmentsPlugin;

import java.util.Objects;

import static org.bukkit.Material.AIR;
import static org.bukkit.Material.TNT;

public final class FuseArrow extends ZenchantedArrow {
    public FuseArrow(@NotNull ZenchantmentsPlugin plugin, @NotNull Arrow entity) {
        super(plugin, entity);
    }

    @Override
    public void onImpact() {
        Location location = this.getArrow().getLocation();
        for (int i = 1; i < 5; i++) {
            Vector vector = this.getArrow().getVelocity().multiply(0.25 * i);
            Location hitLocation = new Location(
                location.getWorld(),
                location.getX() + vector.getX(),
                location.getY() + vector.getY(),
                location.getZ() + vector.getZ()
            );

            if (hitLocation.getBlock().getType() != TNT) {
                continue;
            }

            BlockBreakEvent event = new BlockBreakEvent(
                hitLocation.getBlock(),
                (Player) Objects.requireNonNull(this.getArrow().getShooter())
            );

            this.getPlugin().getServer().getPluginManager().callEvent(event);

            if (!event.isCancelled()) {
                hitLocation.getBlock().setType(AIR);
                hitLocation.getWorld().spawnEntity(hitLocation, EntityType.PRIMED_TNT);
                this.die();
            }

            return;
        }

        this.die();
    }

    @Override
    public boolean onImpact(@NotNull EntityDamageByEntityEvent event) {
        if (!this.getPlugin().getCompatibilityAdapter().attackEntity((LivingEntity) event.getEntity(), (Player) this.getArrow().getShooter(), 0)) {
            this.die();
            return true;
        }

        if (event.getEntity().getType() == EntityType.CREEPER) {
            Creeper creeper = (Creeper) event.getEntity();
            this.getPlugin().getCompatibilityAdapter().explodeCreeper(
                creeper,
                this.getPlugin()
                    .getWorldConfigurationProvider()
                    .getConfigurationForWorld(event.getDamager().getWorld())
                    .isExplosionBlockBreakEnabled()
            );
        } else if (event.getEntity().getType() == EntityType.MUSHROOM_COW) {
            MushroomCow mooshroom = (MushroomCow) event.getEntity();

            if (mooshroom.isAdult()) {
                Location location = event.getEntity().getLocation();

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
