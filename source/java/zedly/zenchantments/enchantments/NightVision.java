package zedly.zenchantments.enchantments;

import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;

import static org.bukkit.potion.PotionEffectType.NIGHT_VISION;

@AZenchantment(runInSlots = Slots.ARMOR, conflicting = {})
public final class NightVision extends Zenchantment {
    @Override
    public boolean onScan(final @NotNull Player player, final int level, final EquipmentSlot slot) {
        Utilities.addPotionEffect(player, NIGHT_VISION, 610, 5);
        return true;
    }
}
