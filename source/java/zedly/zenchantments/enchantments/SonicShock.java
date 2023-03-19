package zedly.zenchantments.enchantments;

import org.bukkit.entity.*;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;

@AZenchantment(runInSlots = Slots.ARMOR, conflicting = {})
public final class SonicShock extends Zenchantment {
    @Override
    public boolean onFastScan(final @NotNull Player player, final int level, final EquipmentSlot slot) {
        if (!player.isGliding() || !(player.getVelocity().length() >= 1)) {
            return true;
        }

        for (final Entity entity : player.getNearbyEntities(2 + 2 * level, 4, 2 + 2 * level)) {
            if (entity instanceof Monster || entity instanceof Slime || entity instanceof ShulkerBullet) {
                LivingEntity le = (LivingEntity) entity;
                final double damage = player.getVelocity().length() * 1.5 * level * this.getPower();
                if(le.getNoDamageTicks() > 0) {
                    continue;
                }
                WorldInteractionUtil.attackEntity((LivingEntity) entity, player, damage);
                Utilities.damageItemStackRespectUnbreaking(player, 1, EquipmentSlot.CHEST);
            }
        }

        return true;
    }
}
