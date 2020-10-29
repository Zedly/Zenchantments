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
import zedly.zenchantments.compatibility.EnumStorage;

import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import static org.bukkit.Material.AIR;
import static org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK;

public final class Terraformer extends Zenchantment {
    public static final String KEY = "terraformer";

    private static final String                             NAME        = "Terraformer";
    private static final String                             DESCRIPTION = "Places the leftmost blocks in the players inventory within a 7 block radius";
    private static final Set<Class<? extends Zenchantment>> CONFLICTING = ImmutableSet.of();
    private static final Hand                               HAND_USE    = Hand.RIGHT;

    private static final int[][] SEARCH_FACES = {{-1, 0, 0}, {1, 0, 0}, {0, -1, 0}, {0, 0, -1}, {0, 0, 1}};
    private static final int     MAX_BLOCKS   = 64;

    private final NamespacedKey key;

    public Terraformer(
        @NotNull ZenchantmentsPlugin plugin,
        @NotNull Set<Tool> enchantable,
        int maxLevel,
        int cooldown,
        double power,
        float probability
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
    public boolean onBlockInteract(@NotNull PlayerInteractEvent event, int level, boolean usedHand) {
        if (!event.getPlayer().isSneaking() || !event.getAction().equals(RIGHT_CLICK_BLOCK)) {
            return false;
        }

        Block start = event.getClickedBlock().getRelative(event.getBlockFace());
        Inventory inventory = event.getPlayer().getInventory();
        Material material = AIR;

        for (int i = 0; i < 9; i++) {
            ItemStack item = inventory.getItem(i);

            if (item == null) {
                continue;
            }

            if (item.getType().isBlock() && Storage.COMPATIBILITY_ADAPTER.TerraformerMaterials().contains(item.getType())) {
                material = item.getType();
                break;
            }
        }

        Iterable<Block> blocks = Utilities.bfs(
            start,
            MAX_BLOCKS,
            false,
            5f,
            SEARCH_FACES,
            Storage.COMPATIBILITY_ADAPTER.Airs(),
            new EnumStorage<>(new Material[0]),
            false,
            true
        );

        for (Block block : blocks) {
            if (block.getType() != AIR) {
                continue;
            }

            if (!Utilities.playerHasMaterial(event.getPlayer(), material, 1)) {
                continue;
            }

            if (Storage.COMPATIBILITY_ADAPTER.placeBlock(block, event.getPlayer(), material, null)) {
                Utilities.removeMaterialsFromPlayer(event.getPlayer(), material, 1);
                if (ThreadLocalRandom.current().nextInt(10) == 5) {
                    Utilities.damageItemStack(event.getPlayer(), 1, usedHand);
                }
            }
        }

        return true;
    }
}