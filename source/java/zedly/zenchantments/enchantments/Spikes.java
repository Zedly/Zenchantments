package zedly.zenchantments.enchantments;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;

@AZenchantment(runInSlots = Slots.ARMOR, conflicting = {})
public final class Spikes extends Zenchantment {
    @Override
    public boolean onFastScan(final @NotNull Player player, final int level, final EquipmentSlot slot) {
        if (!(player.getVelocity().getY() < -0.45)) {
            return true;
        }

        for (final Entity entity : player.getNearbyEntities(1, 2, 1)) {
            final double fall = Math.min(player.getFallDistance(), 20.0);
            if (entity instanceof LivingEntity) {
                WorldInteractionUtil.attackEntity((LivingEntity) entity, player, this.getPower() * level * fall * 0.25);
                Utilities.damageItemStackRespectUnbreaking(player, 1, EquipmentSlot.FEET);
            }
        }

        return true;
    }
}
