package zedly.zenchantments.enchantments;

import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;

import static org.bukkit.potion.PotionEffectType.FAST_DIGGING;

@AZenchantment(runInSlots = Slots.MAIN_HAND, conflicting = {})
public final class Haste extends Zenchantment {
    @Override
    public boolean onScan(final @NotNull Player player, final int level, final EquipmentSlot slot) {
        Utilities.addPotionEffect(player, FAST_DIGGING, 610, (int) Math.round(level * this.getPower()));
        player.setMetadata("ze.haste", new FixedMetadataValue(ZenchantmentsPlugin.getInstance(), System.currentTimeMillis()));
        return false;
    }
}
