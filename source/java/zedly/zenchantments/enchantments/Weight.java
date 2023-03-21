package zedly.zenchantments.enchantments;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;

import static org.bukkit.potion.PotionEffectType.INCREASE_DAMAGE;
import static zedly.zenchantments.Slots.ARMOR;

@AZenchantment(runInSlots = ARMOR, conflicting = {Meador.class, Speed.class})
public final class Weight extends Zenchantment {

    @Override
    public boolean onBeingHit(final @NotNull EntityDamageByEntityEvent event, final int level, final EquipmentSlot slot) {
        final Player player = (Player) event.getEntity();

        if (!(event.getDamage() < player.getHealth())) {
            return true;
        }

        // Cancel event to prevent knockback, damage the player anyway.
        // There might be a much better way to do this.

        event.setCancelled(true);
        player.damage(event.getDamage());
        player.setVelocity(
            player.getLocation()
                .subtract(event.getDamager().getLocation())
                .toVector()
                .multiply((float) (1 / (level * this.getPower() + 1.5)))
        );

        Utilities.damageItemStackRespectUnbreaking(player, 1, slot);
        return true;
    }

    @Override
    public boolean onScan(final @NotNull Player player, final int level, final EquipmentSlot slot) {
        Utilities.addPotionEffect(player, INCREASE_DAMAGE, 610, (int) Math.round(this.getPower() * level));
        player.setWalkSpeed((float) (0.164f - level * this.getPower() * 0.014f));
        player.setMetadata("ze.speed", new FixedMetadataValue(ZenchantmentsPlugin.getInstance(), System.currentTimeMillis()));
        return true;
    }
}
