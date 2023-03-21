package zedly.zenchantments.enchantments;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;

import static org.bukkit.entity.EntityType.DROPPED_ITEM;

@AZenchantment(runInSlots = Slots.ARMOR, conflicting = {})
public final class Magnetism extends Zenchantment {
    @Override
    public boolean onFastScan(final @NotNull Player player, final int level, final EquipmentSlot slot) {
        final int radius = (int) Math.round(this.getPower() * level * 2 + 3);
        for (final Entity entity : player.getNearbyEntities(radius, radius, radius)) {
            if (entity.getType() == DROPPED_ITEM) {
                Item item = (Item) entity;
                if (item.getPickupDelay() <= 0
                    && item.getTicksLived() >= 160) {
                    entity.setVelocity(player.getLocation().toVector().subtract(entity.getLocation().toVector()).multiply(0.05));
                }
            }
        }

        return true;
    }
}
