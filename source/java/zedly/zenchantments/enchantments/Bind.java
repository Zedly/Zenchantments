package zedly.zenchantments.enchantments;

import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;

@AZenchantment(runInSlots = Slots.ALL , conflicting = {})
public final class Bind extends Zenchantment {
    @Override
    public boolean onPlayerDeath(final @NotNull PlayerDeathEvent event, final int level, final EquipmentSlot slot) {
        // Method body moved to ZenchantmentListener.onDeath because this is the only enchant that needs it anyway and we're
        // missing access to some data here that's readily available in the listener
        return false;
    }
}
