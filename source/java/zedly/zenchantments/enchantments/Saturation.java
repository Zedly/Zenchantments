package zedly.zenchantments.enchantments;

import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;

import java.util.concurrent.ThreadLocalRandom;

@AZenchantment(runInSlots = Slots.ARMOR, conflicting = {})
public final class Saturation extends Zenchantment {
    @Override
    public boolean onHungerChange(final @NotNull FoodLevelChangeEvent event, final int level, final EquipmentSlot slot) {
        if (event.getFoodLevel() < event.getEntity().getFoodLevel()
            && ThreadLocalRandom.current().nextInt(10) > 10 - 2 * level * this.getPower()
        ) {
            event.setCancelled(true);
        }

        return true;
    }
}
