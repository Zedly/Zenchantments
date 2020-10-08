package zedly.zenchantments.arrows.admin;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.Storage;
import zedly.zenchantments.arrows.EnchantedArrow;
import zedly.zenchantments.configuration.WorldConfiguration;

import static org.bukkit.Material.FIRE;
import static org.bukkit.entity.EntityType.BLAZE;
import static org.bukkit.potion.PotionEffectType.ABSORPTION;
import static org.bukkit.potion.PotionEffectType.HARM;

public class ApocalypseArrow extends EnchantedArrow {
    public ApocalypseArrow(@NotNull Arrow entity) {
        super(entity);
    }

    @Override
    public void onImpact() {
        WorldConfiguration config = WorldConfiguration.get(this.getArrow().getWorld());

        Location clone = this.getArrow().getLocation().clone();
        clone.setY(clone.getY() + 1);

        this.getArrow().getWorld().strikeLightning(clone);

        Location[] locations = {this.getArrow().getLocation(), clone};
        for (int l = 0; l < locations.length; l++) {
            Location location = locations[l];
            int finalLs = l;
            for (int i = 0; i <= 45; i++) {
                int c = i + 1;
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(
                    Storage.zenchantments,
                    () -> {
                        Entity entity = location.getWorld().spawnFallingBlock(location, Bukkit.createBlockData(FIRE));

                        Vector vector = location.toVector();
                        vector.setY(Math.abs(Math.sin(c)));

                        if (finalLs % 2 == 0) {
                            vector.setZ((Math.sin(c) / 2));
                            vector.setX((Math.cos(c) / 2));
                        } else {
                            vector.setX((Math.sin(c) / 2));
                            vector.setZ((Math.cos(c) / 2));
                        }

                        entity.setVelocity(vector.multiply(1.5));

                        TNTPrimed prime = (TNTPrimed) this.getArrow().getWorld().spawnEntity(location, EntityType.PRIMED_TNT);
                        prime.setFuseTicks(200);
                        prime.setYield(config.explosionBlockBreak() ? 4 : 0);

                        Blaze blaze = (Blaze) this.getArrow().getWorld().spawnEntity(location, BLAZE);
                        blaze.addPotionEffect(new PotionEffect(ABSORPTION, 150, 100000));
                        blaze.addPotionEffect(new PotionEffect(HARM, 10000, 1));

                        if (config.explosionBlockBreak()) {
                            Entity crystal = this.getArrow().getWorld().spawnEntity(location, EntityType.ENDER_CRYSTAL);
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