package zedly.zenchantments.enchantments;

import com.google.common.collect.ImmutableSet;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.*;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;

import java.util.Collection;
import java.util.Set;

public final class SonicShock extends Zenchantment {
    public static final String KEY = "sonic_shock";

    private static final String                             NAME        = "Sonic Shock";
    private static final String                             DESCRIPTION = "Damages mobs when flying past at high speed";
    private static final Set<Class<? extends Zenchantment>> CONFLICTING = ImmutableSet.of();
    private static final Hand                               HAND_USE    = Hand.NONE;

    private final NamespacedKey key;

    public SonicShock(
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
                CompatibilityAdapter.instance().attackEntity((LivingEntity) entity, player, damage);
                Utilities.damageItemStackRespectUnbreaking(player, 1, EquipmentSlot.CHEST);
            }
        }

        return true;
    }
}
