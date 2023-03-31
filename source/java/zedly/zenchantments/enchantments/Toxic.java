package zedly.zenchantments.enchantments;

import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;
import zedly.zenchantments.arrows.BlizzardArrow;
import zedly.zenchantments.arrows.ToxicArrow;
import zedly.zenchantments.arrows.ZenchantedArrow;
import zedly.zenchantments.task.EffectTask;
import zedly.zenchantments.task.Frequency;

import java.util.*;

import static org.bukkit.potion.PotionEffectType.CONFUSION;
import static org.bukkit.potion.PotionEffectType.HUNGER;

@AZenchantment(runInSlots = Slots.HANDS, conflicting = {})
public final class Toxic extends Zenchantment {
    public static final Map<Player, Integer> HUNGER_PLAYERS = new HashMap<>();

    @Override
    public boolean onEntityHit(final @NotNull EntityDamageByEntityEvent event, final int level, final EquipmentSlot slot) {
        if (!(event.getEntity() instanceof LivingEntity) ||
            !WorldInteractionUtil.attackEntity((LivingEntity) event.getEntity(), (Player) event.getDamager(), 0)
        ) {
            return true;
        }

        final int value = (int) Math.round(level * this.getPower());

        Utilities.addPotionEffect((LivingEntity) event.getEntity(), CONFUSION, 80 + 60 * value, 4);
        Utilities.addPotionEffect((LivingEntity) event.getEntity(), HUNGER, 40 + 60 * value, 4);

        if (!(event.getEntity() instanceof Player)) {
            return true;
        }

        final Player player = (Player) event.getEntity();

        ZenchantmentsPlugin.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(ZenchantmentsPlugin.getInstance(), () -> {
            player.removePotionEffect(HUNGER);
            Utilities.addPotionEffect(player, HUNGER, 60 + 40 * value, 0);
        }, 20 + 60L * value);

        HUNGER_PLAYERS.put((Player) event.getEntity(), (1 + value) * 100);

        return true;
    }

    @Override
    public boolean onEntityShootBow(final @NotNull EntityShootBowEvent event, final int level, final EquipmentSlot slot) {
        final ToxicArrow arrow = new ToxicArrow((AbstractArrow) event.getProjectile(), level, this.getPower());
        ZenchantedArrow.addZenchantedArrowToArrowEntity((AbstractArrow) event.getProjectile(), arrow, (Player) event.getEntity());
        return true;
    }

    @Override
    public boolean onProjectileLaunch(final @NotNull ProjectileLaunchEvent event, final int level, final EquipmentSlot slot) {
        if(event.getEntity().getType() != EntityType.TRIDENT) {
            return false;
        }
        final ToxicArrow arrow = new ToxicArrow(event.getEntity(), level, getPower());
        ZenchantedArrow.addZenchantedArrowToArrowEntity(event.getEntity(), arrow, (Player) event.getEntity().getShooter());
        return true;
    }

    @EffectTask(Frequency.HIGH)
    public static void hunger() {
        final Iterator<Player> iterator = HUNGER_PLAYERS.keySet().iterator();
        while (iterator.hasNext()) {
            final Player player = iterator.next();
            if (HUNGER_PLAYERS.get(player) < 1) {
                iterator.remove();
            } else {
                HUNGER_PLAYERS.put(player, HUNGER_PLAYERS.get(player) - 1);
            }
        }
    }
}
