package zedly.zenchantments.enchantments;

import org.bukkit.entity.Entity;
import org.bukkit.entity.MushroomCow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;

@AZenchantment(runInSlots = Slots.HANDS, conflicting = {})
public final class Mow extends Zenchantment {
    @Override
    public boolean onShear(final @NotNull PlayerShearEntityEvent event, final int level, final EquipmentSlot slot) {
        return this.shear(event, level, slot);
    }

    private boolean shear(final @NotNull PlayerShearEntityEvent event, final int level, final EquipmentSlot slot) {
        final int radius = (int) Math.round(level * this.getPower() + 2);
        final Player player = event.getPlayer();

        boolean shearedEntity = false;

        for (final Entity entity : player.getNearbyEntities(radius, radius, radius)) {
            if(entity == event.getEntity()) {
                continue;
            }
            if (entity instanceof Sheep) {
                final Sheep sheep = (Sheep) entity;
                if (sheep.isAdult()) {
                    CompatibilityAdapter.instance().shearEntityNMS(sheep, player, slot == EquipmentSlot.HAND);
                    shearedEntity = true;
                }
            } else if (entity instanceof MushroomCow) {
                final MushroomCow mooshroom = (MushroomCow) entity;
                if (mooshroom.isAdult()) {
                    CompatibilityAdapter.instance().shearEntityNMS(mooshroom, player, slot == EquipmentSlot.HAND);
                    shearedEntity = true;
                }
            }
        }

        return shearedEntity;
    }
}
