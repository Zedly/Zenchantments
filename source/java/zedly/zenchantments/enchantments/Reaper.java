package zedly.zenchantments.enchantments;

import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;
import zedly.zenchantments.arrows.BlizzardArrow;
import zedly.zenchantments.arrows.ReaperArrow;
import zedly.zenchantments.arrows.ZenchantedArrow;

import static org.bukkit.potion.PotionEffectType.BLINDNESS;

@AZenchantment(runInSlots = Slots.HANDS, conflicting = {})
public final class Reaper extends Zenchantment {
    @Override
    public boolean onEntityHit(final @NotNull EntityDamageByEntityEvent event, final int level, final EquipmentSlot slot) {
        if (!(event.getEntity() instanceof LivingEntity)
            || !WorldInteractionUtil.attackEntity((LivingEntity) event.getEntity(), (Player) event.getDamager(), 0)
        ) {
            return true;
        }

        final int power = (int) Math.round(level * this.getPower());
        final int length = (int) Math.round(10 + level * 20 * this.getPower());
        Utilities.addPotionEffect((LivingEntity) event.getEntity(), PotionEffectType.WITHER, length, power);
        Utilities.addPotionEffect((LivingEntity) event.getEntity(), BLINDNESS, length, power);

        return true;
    }

    @Override
    public boolean onEntityShootBow(final @NotNull EntityShootBowEvent event, final int level, final EquipmentSlot slot) {
        final ReaperArrow arrow = new ReaperArrow((AbstractArrow) event.getProjectile(), level, this.getPower());
        ZenchantedArrow.addZenchantedArrowToArrowEntity((AbstractArrow) event.getProjectile(), arrow, (Player) event.getEntity());
        return true;
    }

    @Override
    public boolean onProjectileLaunch(final @NotNull ProjectileLaunchEvent event, final int level, final EquipmentSlot slot) {
        if(event.getEntity().getType() != EntityType.TRIDENT) {
            return false;
        }
        final ReaperArrow arrow = new ReaperArrow(event.getEntity(), level, getPower());
        ZenchantedArrow.addZenchantedArrowToArrowEntity(event.getEntity(), arrow, (Player) event.getEntity().getShooter());
        return true;
    }
}
