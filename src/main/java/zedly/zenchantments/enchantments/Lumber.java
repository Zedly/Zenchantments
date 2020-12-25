package zedly.zenchantments.enchantments;

import com.google.common.collect.ImmutableSet;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;

import java.util.List;
import java.util.Set;

public final class Lumber extends Zenchantment {
    public static final String KEY = "lumber";

    private static final String                             NAME        = "Lumber";
    private static final String                             DESCRIPTION = "Breaks the entire tree at once";
    private static final Set<Class<? extends Zenchantment>> CONFLICTING = ImmutableSet.of();
    private static final Hand                               HAND_USE    = Hand.LEFT;

    private static final int     MAX_BLOCKS   = 200;
    private static final int[][] SEARCH_FACES = new int[0][0];

    private final NamespacedKey key;

    public Lumber(
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

    @Override
    public boolean onBlockBreak(@NotNull BlockBreakEvent event, int level, boolean usedHand) {
        if (!event.getPlayer().isSneaking()) {
            return false;
        }

        Block startBlock = event.getBlock();

        if (!Storage.COMPATIBILITY_ADAPTER.TrunkBlocks().contains(startBlock.getType())) {
            return false;
        }

        List<Block> blocks = Utilities.bfs(
            startBlock,
            MAX_BLOCKS,
            true,
            Float.MAX_VALUE,
            SEARCH_FACES,
            Storage.COMPATIBILITY_ADAPTER.TrunkBlocks(),
            Storage.COMPATIBILITY_ADAPTER.LumberWhitelist(),
            true,
            false
        );

        for (Block block : blocks) {
            ADAPTER.breakBlockNMS(block, event.getPlayer());
        }

        return !blocks.isEmpty();
    }
}
