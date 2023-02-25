package zedly.zenchantments.enchantments;

import com.google.common.collect.ImmutableSet;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;

import java.util.Collection;
import java.util.Set;

import static org.bukkit.Material.SLIME_BLOCK;

public final class Bounce extends Zenchantment {
    public static final String KEY = "bounce";

    private static final String                             NAME        = "Bounce";
    private static final String                             DESCRIPTION = "Preserves momentum when on slime blocks";
    private static final Set<Class<? extends Zenchantment>> CONFLICTING = ImmutableSet.of();
    private static final Hand                               HAND_USE    = Hand.RIGHT;

    private final NamespacedKey key;

    public Bounce(
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
        if (player.getVelocity().getY() >= 0) {
            return false;
        }

        final Block block = player.getLocation().getBlock();
        if (block.getRelative(0, -1, 0).getType() == SLIME_BLOCK
            || block.getType() == SLIME_BLOCK
            || block.getRelative(0, -2, 0).getType() == SLIME_BLOCK
            && (level * this.getPower()) > 2.0
        ) {
            if (!player.isSneaking()) {
                player.setVelocity(player.getVelocity().setY(0.56 * level * this.getPower()));
                return true;
            }

            player.setFallDistance(0);
        }

        return false;
    }
}
