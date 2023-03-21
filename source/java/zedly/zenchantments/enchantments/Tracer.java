package zedly.zenchantments.enchantments;

import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;
import zedly.zenchantments.arrows.TracerArrow;
import zedly.zenchantments.arrows.ZenchantedArrow;

import java.util.HashMap;
import java.util.Map;

@AZenchantment(runInSlots = Slots.HANDS, conflicting = {})
public final class Tracer extends Zenchantment {
    public static final Map<AbstractArrow, Integer> TRACERS = new HashMap<>();

    @Override
    public boolean onEntityShootBow(final @NotNull EntityShootBowEvent event, final int level, final EquipmentSlot slot) {
        final TracerArrow arrow = new TracerArrow((AbstractArrow) event.getProjectile(), level, this.getPower());
        ZenchantedArrow.addZenchantedArrowToArrowEntity((AbstractArrow) event.getProjectile(), arrow, (Player) event.getEntity());
        return true;
    }
}
