package zedly.zenchantments.enchantments;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;
import zedly.zenchantments.arrows.SiphonArrow;
import zedly.zenchantments.arrows.ZenchantedArrow;

import java.util.Objects;

@AZenchantment(runInSlots = Slots.HANDS, conflicting = {})
public final class Siphon extends Zenchantment {
    @Override
    public boolean onEntityHit(final @NotNull EntityDamageByEntityEvent event, final int level, final EquipmentSlot slot) {
        if (event.getEntity() instanceof LivingEntity
            && CompatibilityAdapter.instance().attackEntity((LivingEntity) event.getEntity(), (Player) event.getDamager(), 0)
        ) {
            final Player player = (Player) event.getDamager();
            int difference = (int) Math.round(0.17 * level * this.getPower() * event.getDamage());

            final double genericMaxHealth = Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue();

            while (difference > 0) {
                if (player.getHealth() < genericMaxHealth) {
                    player.setHealth(Math.min(player.getHealth() + 1, genericMaxHealth));
                }

                difference--;
            }
        }

        return true;
    }

    @Override
    public boolean onEntityShootBow(final @NotNull EntityShootBowEvent event, final int level, final EquipmentSlot slot) {
        final SiphonArrow arrow = new SiphonArrow((AbstractArrow) event.getProjectile(), level, this.getPower());
        ZenchantedArrow.addZenchantedArrowToArrowEntity((AbstractArrow) event.getProjectile(), arrow, (Player) event.getEntity());
        return true;
    }
}
