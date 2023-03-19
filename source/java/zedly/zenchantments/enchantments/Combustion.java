package zedly.zenchantments.enchantments;

import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;

@AZenchantment(runInSlots = Slots.ARMOR, conflicting = {})
public final class Combustion extends Zenchantment {

    @Override
    public boolean onBeingHit(final @NotNull EntityDamageByEntityEvent event, final int level, final EquipmentSlot slot) {
        final Entity entity;

        if (event.getDamager().getType() == EntityType.ARROW) {
            final AbstractArrow arrow = (AbstractArrow) event.getDamager();
            if (!(arrow.getShooter() instanceof LivingEntity)) {
                return false;
            }

            entity = (Entity) arrow.getShooter();
        } else {
            entity = event.getDamager();
        }

        return WorldInteractionUtil.igniteEntity(entity, (Player) event.getEntity(), (int) (50 * level * this.getPower()));
    }

    public boolean onCombust(final @NotNull EntityCombustByEntityEvent event, final int level, final EquipmentSlot slot) {
        if (WorldInteractionUtil.isZombie(event.getCombuster())) {
            event.setDuration(0);
        }
        return false;
    }
}
