package zedly.zenchantments.arrows.enchanted;

import org.bukkit.Bukkit;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.Storage;
import zedly.zenchantments.Utilities;
import zedly.zenchantments.arrows.EnchantedArrow;
import zedly.zenchantments.enchantments.Toxic;

import static org.bukkit.potion.PotionEffectType.CONFUSION;
import static org.bukkit.potion.PotionEffectType.HUNGER;

public class ToxicArrow extends EnchantedArrow {
    public ToxicArrow(@NotNull Arrow entity, int level, double power) {
        super(entity, level, power);
    }

    @Override
    public boolean onImpact(final @NotNull EntityDamageByEntityEvent event) {
        if (Storage.COMPATIBILITY_ADAPTER.attackEntity((LivingEntity) event.getEntity(), (Player) this.getArrow().getShooter(), 0)) {
            int value = (int) Math.round(this.getLevel() * this.getPower());

            Utilities.addPotion((LivingEntity) event.getEntity(), CONFUSION, 80 + 60 * value, 4);
            Utilities.addPotion((LivingEntity) event.getEntity(), HUNGER, 40 + 60 * value, 4);

            if (event.getEntity() instanceof Player) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(
                    Storage.zenchantments,
                    () -> {
                        ((LivingEntity) event.getEntity()).removePotionEffect(HUNGER);
                        Utilities.addPotion((LivingEntity) event.getEntity(), HUNGER, 60 + 40 * value, 0);
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