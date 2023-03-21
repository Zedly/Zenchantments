package zedly.zenchantments.enchantments;

import org.bukkit.Material;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;
import zedly.zenchantments.arrows.MissileArrow;
import zedly.zenchantments.arrows.ZenchantedArrow;

@AZenchantment(runInSlots = Slots.HANDS, conflicting = {})
public final class Missile extends Zenchantment {
    @Override
    public boolean onEntityShootBow(final @NotNull EntityShootBowEvent event, final int level, final EquipmentSlot slot) {
        final MissileArrow arrow = new MissileArrow((AbstractArrow) event.getProjectile());
        final Player player = (Player) event.getEntity();

        ZenchantedArrow.addZenchantedArrowToArrowEntity((AbstractArrow) event.getProjectile(), arrow, player);

        event.setCancelled(true);

        Utilities.damageItemStackRespectUnbreaking(player, 1, slot);
        Utilities.removeMaterialsFromPlayer(player, Material.ARROW, 1);
        return true;
    }
}
