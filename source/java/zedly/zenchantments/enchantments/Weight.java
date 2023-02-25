package zedly.zenchantments.enchantments;

import com.google.common.collect.ImmutableSet;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;

import java.util.Collection;
import java.util.Set;

import static org.bukkit.potion.PotionEffectType.INCREASE_DAMAGE;

public final class Weight extends Zenchantment {
    public static final String KEY = "weight";

    private static final String                             NAME        = "Weight";
    private static final String                             DESCRIPTION = "Slows the player down but makes them stronger and more resistant to knockback";
    private static final Set<Class<? extends Zenchantment>> CONFLICTING = ImmutableSet.of(Meador.class, Speed.class);
    private static final Hand                               HAND_USE    = Hand.NONE;

    private final NamespacedKey key;

    public Weight(
        final @NotNull Set<Tool> enchantable,
        final int maxLevel,
        final int cooldown,
        final double probability,
        final float power
    ) {
        super(enchantable, maxLevel, cooldown, probability, power);
        this.key = new NamespacedKey(ZenchantmentsPlugin.getInstance(), KEY);
    }
    
    @Override
    @NotNull
    public NamespacedKey getKey() {
        return this.key;
    }

    @Override
    @NotNull
    public String getName() {
        return NAME;
    }

    @Override
    @NotNull
    public String getDescription() {
        return DESCRIPTION;
    }

    @Override
    @NotNull
    public Set<Class<? extends Zenchantment>> getConflicting() {
        return CONFLICTING;
    }

    @Override
    public Collection<EquipmentSlot> getApplyToSlots() {
        return Slots.ARMOR;
    }

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
