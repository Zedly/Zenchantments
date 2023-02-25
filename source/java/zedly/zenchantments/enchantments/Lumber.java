package zedly.zenchantments.enchantments;

import com.google.common.collect.ImmutableSet;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public final class Lumber extends Zenchantment {
    public static final String KEY = "lumber";

    private static final String                             NAME        = "Lumber";
    private static final String                             DESCRIPTION = "Breaks the entire tree at once";
    private static final Set<Class<? extends Zenchantment>> CONFLICTING = ImmutableSet.of();
    private static final Hand                               HAND_USE    = Hand.LEFT;

    private static final int     MAX_BLOCKS   = 200;

    private final NamespacedKey key;

    public Lumber(
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
        return Slots.MAIN_HAND;
    }

    @Override
    public boolean onBlockBreak(final @NotNull BlockBreakEvent event, final int level, final EquipmentSlot slot) {
        if (!event.getPlayer().isSneaking()) {
            return false;
        }

        final Block startBlock = event.getBlock();

        if (!MaterialList.TRUNKS.contains(startBlock.getType())) {
            return false;
        }

        final List<Block> blocks = Utilities.bfs(
            startBlock,
            MAX_BLOCKS,
            true,
            Float.MAX_VALUE,
            Utilities.DEFAULT_SEARCH_FACES,
            MaterialList.TRUNKS,
            MaterialList.LUMBER_WHITELIST,
            true,
            false
        );

        for (final Block block : blocks) {
            if (!CompatibilityAdapter.instance().breakBlock(block, event.getPlayer())) {
                break;
            }
        }

        return !blocks.isEmpty();
    }
}
