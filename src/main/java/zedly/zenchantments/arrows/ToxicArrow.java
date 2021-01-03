package zedly.zenchantments.arrows;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.Utilities;
import zedly.zenchantments.ZenchantmentsPlugin;
import zedly.zenchantments.enchantments.Toxic;

import static org.bukkit.potion.PotionEffectType.CONFUSION;
import static org.bukkit.potion.PotionEffectType.HUNGER;

public final class ToxicArrow extends ZenchantedArrow {
    public ToxicArrow(@NotNull ZenchantmentsPlugin plugin, @NotNull Arrow entity, int level, double power) {
        super(plugin, entity, level, power);
    }

    @Override
    public boolean onImpact(final @NotNull EntityDamageByEntityEvent event) {
        if (this.getPlugin().getCompatibilityAdapter().attackEntity((LivingEntity) event.getEntity(), (Player) this.getArrow().getShooter(), 0)) {
            int value = (int) Math.round(this.getLevel() * this.getPower());

            Utilities.addPotionEffect((LivingEntity) event.getEntity(), CONFUSION, 80 + 60 * value, 4);
            Utilities.addPotionEffect((LivingEntity) event.getEntity(), HUNGER, 40 + 60 * value, 4);

            if (event.getEntity() instanceof Player) {
                this.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(
                    this.getPlugin(),
                    () -> {
                        ((LivingEntity) event.getEntity()).removePotionEffect(HUNGER);
                        Utilities.addPotionEffect((LivingEntity) event.getEntity(), HUNGER, 60 + 40 * value, 0);
                    },
                    20 + 60 * value
                );

                Toxic.HUNGER_PLAYERS.put((Player) event.getEntity(), (1 + value) * 100);
            }
        }

        this.die();
        return true;
    }
}
