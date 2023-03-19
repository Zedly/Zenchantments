package zedly.zenchantments.enchantments;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;

@AZenchantment(runInSlots = Slots.HANDS, conflicting = {LongCast.class})
public final class ShortCast extends Zenchantment {
    @Override
    public boolean onProjectileLaunch(final @NotNull ProjectileLaunchEvent event, final int level, final EquipmentSlot slot) {
        final Entity entity = event.getEntity();
        if (entity.getType() == EntityType.FISHING_HOOK) {
            entity.setVelocity(entity.getVelocity().normalize().multiply((0.8f / (level * this.getPower()))));
        }
        return true;
    }
}
