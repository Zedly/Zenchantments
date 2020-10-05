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

public class FrozenStep extends Zenchantment {
    public static final String KEY = "frozen_step";

    public static final Map<Location, Long> FROZEN_LOCATIONS = new HashMap<>();

    private static final String                             NAME        = "Frozen Step";
    private static final String                             DESCRIPTION = "Allows the player to walk on water and safely emerge from it when sneaking";
    private static final Set<Class<? extends Zenchantment>> CONFLICTING = ImmutableSet.of(NetherStep.class);
    private static final Hand                               HAND_USE    = Hand.NONE;

    private final NamespacedKey key;

    public FrozenStep(
        @NotNull ZenchantmentsPlugin plugin,
        @NotNull Set<Tool> enchantable,
        int maxLevel,
        int cooldown,
        double power,
        float probability
    ) {
        super(plugin, enchantable, maxLevel, cooldown, power, probability);
        this.key = new NamespacedKey(plugin, FrozenStep.KEY);
    }

    @Override
    @NotNull
    public NamespacedKey getKey() {
        return this.key;
    }

    @Override
    @NotNull
    public String getName() {
        return FrozenStep.NAME;
    }

    @Override
    @NotNull
    public String getDescription() {
        return FrozenStep.DESCRIPTION;
    }

    @Override
    @NotNull
    public Set<Class<? extends Zenchantment>> getConflicting() {
        return FrozenStep.CONFLICTING;
    }

    @Override
    @NotNull
    public Hand getHandUse() {
        return FrozenStep.HAND_USE;
    }

    public boolean onScan(@NotNull Player player, int level, boolean usedHand) {
        if (player.isSneaking()
            && player.getLocation().getBlock().getType() == WATER
            && !player.isFlying()
        ) {
            player.setVelocity(player.getVelocity().setY(.4));
        }

        Block block = player.getLocation().getBlock();
        int radius = (int) Math.round(this.getPower() * level + 2);

        Utilities.selfRemovingArea(PACKED_ICE, WATER, radius, block, player, FROZEN_LOCATIONS);
        return true;
    }
}