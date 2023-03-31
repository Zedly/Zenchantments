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
import zedly.zenchantments.arrows.StationaryArrow;
import zedly.zenchantments.arrows.ZenchantedArrow;

@AZenchantment(runInSlots = Slots.HANDS, conflicting = {})
public final class Stationary extends Zenchantment {
    @Override
    public boolean onEntityHit(final @NotNull EntityDamageByEntityEvent event, final int level, final EquipmentSlot slot) {
        if (event.getEntity() instanceof LivingEntity
            && !WorldInteractionUtil.attackEntity((LivingEntity) event.getEntity(), (Player) event.getDamager(), 0)
        ) {
            return true;
        }

        final LivingEntity entity = (LivingEntity) event.getEntity();

        if (event.getDamage() < entity.getHealth()) {
            event.setCancelled(true);
            entity.damage(event.getDamage());
            Utilities.damageItemStackRespectUnbreaking(((Player) event.getDamager()), 1, slot);
        }

        return true;
    }

    @Override
    public boolean onEntityShootBow(final @NotNull EntityShootBowEvent event, final int level, final EquipmentSlot slot) {
        final StationaryArrow arrow = new StationaryArrow((AbstractArrow) event.getProjectile());
        ZenchantedArrow.addZenchantedArrowToArrowEntity((AbstractArrow) event.getProjectile(), arrow, (Player) event.getEntity());
        return true;
    }

    @Override
    public boolean onProjectileLaunch(final @NotNull ProjectileLaunchEvent event, final int level, final EquipmentSlot slot) {
        if(event.getEntity().getType() != EntityType.TRIDENT) {
            return false;
        }
        final StationaryArrow arrow = new StationaryArrow(event.getEntity());
        ZenchantedArrow.addZenchantedArrowToArrowEntity(event.getEntity(), arrow, (Player) event.getEntity().getShooter());
        return true;
    }
}
