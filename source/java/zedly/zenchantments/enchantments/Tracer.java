package zedly.zenchantments.enchantments;

import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;
import zedly.zenchantments.arrows.BlizzardArrow;
import zedly.zenchantments.arrows.TracerArrow;
import zedly.zenchantments.arrows.ZenchantedArrow;

import java.util.HashMap;
import java.util.Map;

@AZenchantment(runInSlots = Slots.HANDS, conflicting = {})
public final class Tracer extends Zenchantment {
    public static final Map<Projectile, Integer> TRACERS = new HashMap<>();

    @Override
    public boolean onEntityShootBow(final @NotNull EntityShootBowEvent event, final int level, final EquipmentSlot slot) {
        final TracerArrow arrow = new TracerArrow((Projectile) event.getProjectile(), level, this.getPower());
        ZenchantedArrow.addZenchantedArrowToArrowEntity((Projectile) event.getProjectile(), arrow, (Player) event.getEntity());
        return true;
    }

    @Override
    public boolean onProjectileLaunch(final @NotNull ProjectileLaunchEvent event, final int level, final EquipmentSlot slot) {
        if(event.getEntity().getType() != EntityType.TRIDENT) {
            return false;
        }
        final TracerArrow arrow = new TracerArrow(event.getEntity(), level, getPower());
        ZenchantedArrow.addZenchantedArrowToArrowEntity(event.getEntity(), arrow, (Player) event.getEntity().getShooter());
        return true;
    }
}
