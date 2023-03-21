package zedly.zenchantments.enchantments;

import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;

import static org.bukkit.potion.PotionEffectType.SLOW;

@AZenchantment(runInSlots = Slots.MAIN_HAND, conflicting = {})
public final class IceAspect extends Zenchantment {
    @Override
    public boolean onEntityHit(final @NotNull EntityDamageByEntityEvent event, final int level, final EquipmentSlot slot) {
        Utilities.addPotionEffect(
            (LivingEntity) event.getEntity(),
            SLOW,
            (int) Math.round(40 + level * this.getPower() * 40),
            (int) Math.round(this.getPower() * level * 2)
        );
        Utilities.displayParticle(Utilities.getCenter(event.getEntity().getLocation()), Particle.CLOUD, 10, 0.1f, 1f, 2f, 1f);
        return true;
    }
}
