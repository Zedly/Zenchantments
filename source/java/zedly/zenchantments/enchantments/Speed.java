package zedly.zenchantments.enchantments;

import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;

@AZenchantment(runInSlots = Slots.ARMOR, conflicting = {Meador.class, Weight.class})
public final class Speed extends Zenchantment {
    @Override
    public boolean onScan(final @NotNull Player player, final int level, final EquipmentSlot slot) {
        //final float speed = (float) Math.min((0.05f * level * this.getPower()) + 0.2f, 1);
        final float speed = (float) Math.min(0.5f + level * this.getPower() * 0.05f, 1);

        player.setWalkSpeed(speed);
        player.setFlySpeed(speed);
        player.setMetadata("ze.speed", new FixedMetadataValue(ZenchantmentsPlugin.getInstance(), System.currentTimeMillis()));

        return true;
    }
}
