package zedly.zenchantments.enchantments;

import com.google.common.collect.ImmutableSet;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;
import zedly.zenchantments.arrows.MultiArrow;
import zedly.zenchantments.arrows.ZenchantedArrow;

import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import static java.util.Objects.requireNonNull;

public final class Spread extends Zenchantment {
    public static final String KEY = "spread";

    private static final String                             NAME        = "Spread";
    private static final String                             DESCRIPTION = "Fires an array of arrows simultaneously";
    private static final Set<Class<? extends Zenchantment>> CONFLICTING = ImmutableSet.of(Burst.class);
    private static final Hand                               HAND_USE    = Hand.RIGHT;

    private final NamespacedKey key;

    public Spread(
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
    @NotNull
    public Hand getHandUse() {
        return HAND_USE;
    }

    @Override
    public boolean onProjectileLaunch(final @NotNull ProjectileLaunchEvent event, final int level, final boolean usedHand) {
        final Arrow originalArrow = (Arrow) event.getEntity();
        final Player player = (Player) originalArrow.getShooter();
        final ItemStack hand = Utilities.getUsedItemStack(requireNonNull(player), usedHand);

        final MultiArrow multiArrow = new MultiArrow(ZenchantmentsPlugin.getInstance(), originalArrow);
        ZenchantedArrow.putArrow(originalArrow, multiArrow, player);

        ZenchantmentsPlugin.getInstance().getServer().getPluginManager().callEvent(
            new EntityShootBowEvent(
                player,
                hand,
                null,
                originalArrow,
                usedHand ? EquipmentSlot.HAND : EquipmentSlot.OFF_HAND,
                (float) originalArrow.getVelocity().length(),
                false
            )
        );

        Utilities.damageItemStack(player, (int) Math.round(level / 2.0 + 1), usedHand);

        for (int i = 0; i < (int) Math.round(this.getPower() * level * 4); i++) {
            final Vector vector = originalArrow.getVelocity();

            vector.setX(vector.getX() + Math.max(Math.min(ThreadLocalRandom.current().nextGaussian() / 8, 0.75), -0.75));
            vector.setZ(vector.getZ() + Math.max(Math.min(ThreadLocalRandom.current().nextGaussian() / 8, 0.75), -0.75));

            final Arrow arrow = player.getWorld().spawnArrow(
                player.getEyeLocation().add(player.getLocation().getDirection().multiply(1.0)),
                vector,
                1,
                0
            );
            arrow.setShooter(player);
            arrow.setVelocity(vector.normalize().multiply(originalArrow.getVelocity().length()));
            arrow.setFireTicks(originalArrow.getFireTicks());
            arrow.setKnockbackStrength(originalArrow.getKnockbackStrength());

            final EntityShootBowEvent entityShootBowEvent = new EntityShootBowEvent(
                player,
                hand,
                null,
                arrow,
                usedHand ? EquipmentSlot.HAND : EquipmentSlot.OFF_HAND,
                (float) originalArrow.getVelocity().length(),
                false
            );

            ZenchantmentsPlugin.getInstance().getServer().getPluginManager().callEvent(entityShootBowEvent);

            if (entityShootBowEvent.isCancelled()) {
                arrow.remove();
                return false;
            }

            arrow.setMetadata("ze.arrow", new FixedMetadataValue(ZenchantmentsPlugin.getInstance(), null));
            arrow.setCritical(originalArrow.isCritical());

            ZenchantedArrow.putArrow(originalArrow, new MultiArrow(ZenchantmentsPlugin.getInstance(), originalArrow), player);
        }

        return true;
    }
}
