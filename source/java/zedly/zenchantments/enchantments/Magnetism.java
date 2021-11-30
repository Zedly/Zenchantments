package zedly.zenchantments.enchantments;

import com.google.common.collect.ImmutableSet;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.Hand;
import zedly.zenchantments.Tool;
import zedly.zenchantments.Zenchantment;
import zedly.zenchantments.ZenchantmentsPlugin;

import java.util.Set;

import static org.bukkit.entity.EntityType.DROPPED_ITEM;

public final class Magnetism extends Zenchantment {
    public static final String KEY = "magnetism";

    private static final String NAME = "Magnetism";
    private static final String DESCRIPTION = "Slowly attracts nearby items to the players inventory";
    private static final Set<Class<? extends Zenchantment>> CONFLICTING = ImmutableSet.of();
    private static final Hand HAND_USE = Hand.NONE;

    private final NamespacedKey key;

    public Magnetism(
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
    public boolean onFastScan(final @NotNull Player player, final int level, final boolean usedHand) {
        final int radius = (int) Math.round(this.getPower() * level * 2 + 3);
        for (final Entity entity : player.getNearbyEntities(radius, radius, radius)) {
            if (entity.getType() == DROPPED_ITEM) {
                Item item = (Item) entity;
                if (item.getPickupDelay() == 0
                    && item.getTicksLived() >= 160) {
                    entity.setVelocity(player.getLocation().toVector().subtract(entity.getLocation().toVector()).multiply(0.05));
                }
            }
        }

        return true;
    }
}
