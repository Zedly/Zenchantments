package zedly.zenchantments.enchantments;

import com.google.common.collect.ImmutableSet;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.Hand;
import zedly.zenchantments.Tool;
import zedly.zenchantments.Zenchantment;
import zedly.zenchantments.ZenchantmentsPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class Grab extends Zenchantment {
    public static final String KEY = "grab";

    public static final Map<Block, Player> GRAB_LOCATIONS = new HashMap<>();

    private static final String                             NAME        = "Grab";
    private static final String                             DESCRIPTION = "Teleports mined items and XP directly to the player";
    private static final Set<Class<? extends Zenchantment>> CONFLICTING = ImmutableSet.of();
    private static final Hand                               HAND_USE    = Hand.LEFT;

    private final NamespacedKey key;

    public Grab(
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
    public boolean onBlockBreak(final @NotNull BlockBreakEvent event, final int level, final boolean usedHand) {
        final Block block = event.getBlock();

        GRAB_LOCATIONS.put(block, event.getPlayer());

        ZenchantmentsPlugin.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(
            ZenchantmentsPlugin.getInstance(),
            () -> GRAB_LOCATIONS.remove(block),
            3
        );

        return true;
    }
}
