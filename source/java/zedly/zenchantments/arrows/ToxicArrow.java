package zedly.zenchantments.arrows;

import org.bukkit.Bukkit;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.WorldInteractionUtil;
import zedly.zenchantments.Utilities;
import zedly.zenchantments.ZenchantmentsPlugin;
import zedly.zenchantments.enchantments.Toxic;

import static org.bukkit.potion.PotionEffectType.CONFUSION;
import static org.bukkit.potion.PotionEffectType.HUNGER;

public final class ToxicArrow extends ZenchantedArrow {
    public ToxicArrow(
        final @NotNull Projectile entity,
        final int level,
        final double power
    ) {
        super(entity, level, power);
    }

    @Override
    public void onImpactEntity(final @NotNull ProjectileHitEvent event) {
        if (WorldInteractionUtil.attackEntity((LivingEntity) event.getHitEntity(), (Player) this.getArrow().getShooter(), 0)) {
            final int value = (int) Math.round(this.getLevel() * this.getPower());

            Utilities.addPotionEffect((LivingEntity) event.getHitEntity(), CONFUSION, 80 + 60 * value, 4);
            Utilities.addPotionEffect((LivingEntity) event.getHitEntity(), HUNGER, 40 + 60 * value, 4);

            if (event.getHitEntity() instanceof Player) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(
                    ZenchantmentsPlugin.getInstance(),
                    () -> {
                        ((LivingEntity) event.getHitEntity()).removePotionEffect(HUNGER);
                        Utilities.addPotionEffect((LivingEntity) event.getHitEntity(), HUNGER, 60 + 40 * value, 0);
                    },
                    20 + 60L * value
                );

                Toxic.HUNGER_PLAYERS.put((Player) event.getHitEntity(), (1 + value) * 100);
            }
            die(true);
        }

        die(false);
    }
}
