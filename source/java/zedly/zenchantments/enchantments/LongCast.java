package zedly.zenchantments.enchantments;

import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;

@AZenchantment(runInSlots = Slots.HANDS, conflicting = {})
public final class LongCast extends Zenchantment {
    @Override
    public boolean onProjectileLaunch(final @NotNull ProjectileLaunchEvent event, final int level, final EquipmentSlot slot) {
        if (event.getEntity().getType() == EntityType.FISHING_HOOK) {
            event.getEntity().setVelocity(
                event.getEntity()
                    .getVelocity()
                    .normalize()
                    .multiply(Math.min(1.9 + (this.getPower() * level - 1.2), 2.7))
            );
        }
        return true;
    }
}
