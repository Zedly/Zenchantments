package zedly.zenchantments.enchantments;

import com.google.common.collect.ImmutableSet;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

import static org.bukkit.Material.*;
import static org.bukkit.entity.EntityType.EXPERIENCE_ORB;

public final class Fire extends Zenchantment {
    public static final String KEY = "fire";

    // Locations where Fire has been used on a block and the drop was changed.
    // BlockBreakEvent is not cancelled but the original item drop is not desired.
    public static final HashMap<Block, AtomicInteger> ITEM_DROP_REPLACEMENTS = new HashMap<>();

    private static final String NAME = "Fire";
    private static final String DESCRIPTION = "Drops the smelted version of the block broken";
    private static final Set<Class<? extends Zenchantment>> CONFLICTING = ImmutableSet.of(Switch.class, Variety.class);
    private static final Hand HAND_USE = Hand.LEFT;

    private static final int MAX_BLOCKS = 256;
    private static final int[][] SEARCH_FACES_CACTUS = new int[][]{{0, 1, 0}};
    private static final int[][] SEARCH_FACES_CHORUS = new int[][]{
        {-1, 0, 0},
        {1, 0, 0},
        {0, 1, 0},
        {0, 0, -1},
        {0, 0, 1}
    };
    private static final Map<Material, Integer> ORE_EXPERIENCE_MAP = Map.of(
        Material.IRON_ORE, 1,
        Material.GOLD_ORE, 3,
        Material.COPPER_ORE, 2,
        Material.DEEPSLATE_IRON_ORE, 1,
        Material.DEEPSLATE_GOLD_ORE, 3,
        Material.DEEPSLATE_COPPER_ORE, 2
    );

    private final NamespacedKey key;

    public Fire(
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
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE || !event.isDropItems()) {
            return false;
        }

        final Player player = event.getPlayer();
        final Block block = event.getBlock();
        if (!ITEM_DROP_REPLACEMENTS.containsKey(block)) {
            ITEM_DROP_REPLACEMENTS.put(block, new AtomicInteger(0));
            // Tool damage is applied one tick after breaking the block, after all the items have dropped.
            // This theoretically opens a window to redirect the tool damage to something else.
            // However we will consider this an acceptable issue, as the alternative (which used to be how we did it)
            // was to duplicate the event, fire it again, wait for all other plugins to consume it, then break the block
            // in code using NMS, observe the drops, and alter the tool damage based on that.
            Bukkit.getScheduler().scheduleSyncDelayedTask(ZenchantmentsPlugin.getInstance(), () -> {
                AtomicInteger itemsSmelted = ITEM_DROP_REPLACEMENTS.get(block);
                Utilities.damageItemStackRespectUnbreaking(player, itemsSmelted.get(), usedHand);
                ITEM_DROP_REPLACEMENTS.remove(block);
            }, 1);
        }
        return false;
    }
}
