package zedly.zenchantments.enchantments;

import com.google.common.collect.ImmutableSet;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;

import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import static java.util.Objects.requireNonNull;
import static org.bukkit.Material.AIR;
import static org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK;

public final class Terraformer extends Zenchantment {
    public static final String KEY = "terraformer";

    private static final String                             NAME        = "Terraformer";
    private static final String                             DESCRIPTION = "Places the leftmost blocks in the players inventory within a 7 block radius";
    private static final Set<Class<? extends Zenchantment>> CONFLICTING = ImmutableSet.of();
    private static final Hand                               HAND_USE    = Hand.RIGHT;

    private static final int[][] SEARCH_FACES = { { -1, 0, 0 }, { 1, 0, 0 }, { 0, 0, -1 }, { 0, 0, 1 } };
    private static final int     MAX_BLOCKS   = 64;

    private final NamespacedKey key;

    public Terraformer(
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
    public boolean onBlockInteract(final @NotNull PlayerInteractEvent event, final int level, final boolean usedHand) {
        if (!event.getPlayer().isSneaking() || event.getAction() != RIGHT_CLICK_BLOCK) {
            return false;
        }

        final Block start = requireNonNull(event.getClickedBlock()).getRelative(event.getBlockFace());
        final Inventory inventory = event.getPlayer().getInventory();
        Material material = AIR;

        for (int i = 0; i < 9; i++) {
            final ItemStack item = inventory.getItem(i);

            if (item == null) {
                continue;
            }

            if (item.getType().isBlock() && MaterialList.TERRAFORMER_MATERIALS.contains(item.getType())) {
                material = item.getType();
                break;
            }
        }

        final Iterable<Block> blocks = Utilities.bfs(
            start,
            MAX_BLOCKS,
            false,
            5f,
            SEARCH_FACES,
            MaterialList.AIR,
            MaterialList.EMPTY,
            false,
            true
        );

        for (final Block block : blocks) {
            if (block.getType() != AIR) {
                continue;
            }

            if (!Utilities.playerHasMaterial(event.getPlayer(), material, 1)) {
                continue;
            }

            if (ZenchantmentsPlugin.getInstance().getCompatibilityAdapter().placeBlock(block, event.getPlayer(), material, null)) {
                Utilities.removeMaterialsFromPlayer(event.getPlayer(), material, 1);
                if (ThreadLocalRandom.current().nextInt(10) == 5) {
                    Utilities.damageItemStack(event.getPlayer(), 1, usedHand);
                }
            }
        }

        return true;
    }
}