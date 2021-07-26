package zedly.zenchantments.enchantments;

import com.google.common.collect.ImmutableSet;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.bukkit.Material.PACKED_ICE;
import static org.bukkit.Material.WATER;

public final class FrozenStep extends Zenchantment {
    public static final String KEY = "frozen_step";

    public static final Map<Location, Long> FROZEN_LOCATIONS = new HashMap<>();

    private static final String                             NAME        = "Frozen Step";
    private static final String                             DESCRIPTION = "Allows the player to walk on water and safely emerge from it when sneaking";
    private static final Set<Class<? extends Zenchantment>> CONFLICTING = ImmutableSet.of(NetherStep.class);
    private static final Hand                               HAND_USE    = Hand.NONE;

    private final NamespacedKey key;

    public FrozenStep(
        final @NotNull ZenchantmentsPlugin plugin,
        final @NotNull Set<Tool> enchantable,
        final int maxLevel,
        final int cooldown,
        final double power,
        final float probability
    ) {
        super(plugin, enchantable, maxLevel, cooldown, power, probability);
        this.key = new NamespacedKey(plugin, KEY);
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

    public boolean onScan(final @NotNull Player player, final int level, final boolean usedHand) {
        if (player.isSneaking()
            && player.getLocation().getBlock().getType() == WATER
            && !player.isFlying()
        ) {
            player.setVelocity(player.getVelocity().setY(.4));
        }

        final Block block = player.getLocation().getBlock();
        final int radius = (int) Math.round(this.getPower() * level + 2);

        Utilities.selfRemovingArea(PACKED_ICE, WATER, radius, block, player, FROZEN_LOCATIONS);
        return true;
    }
}
