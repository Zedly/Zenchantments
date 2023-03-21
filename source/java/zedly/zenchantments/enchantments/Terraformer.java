package zedly.zenchantments.enchantments;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;

import static java.util.Objects.requireNonNull;
import static org.bukkit.Material.AIR;
import static org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK;

@AZenchantment(runInSlots = Slots.MAIN_HAND, conflicting = {})
public final class Terraformer extends Zenchantment {
    private static final int[][] SEARCH_FACES = { { -1, 0, 0 }, { 1, 0, 0 }, { 0, 0, -1 }, { 0, 0, 1 } };

    @Override
    public boolean onBlockInteract(final @NotNull PlayerInteractEvent event, final int level, final EquipmentSlot slot) {
        if (!event.getPlayer().isSneaking() || event.getAction() != RIGHT_CLICK_BLOCK) {
            return false;
        }

        final Block start = requireNonNull(event.getClickedBlock()).getRelative(event.getBlockFace());
        final Inventory inventory = event.getPlayer().getInventory();
        Material material = AIR;

        ItemStack item = null;
        for (int i = 0; i < 9; i++) {
            item = inventory.getItem(i);

            if (item == null) {
                continue;
            }

            if (item.getType().isBlock() && MaterialList.TERRAFORMER_MATERIALS.contains(item.getType())) {
                material = item.getType();
                break;
            }
        }

        if(item == null) {
            return false;
        }

        int fillSize = item.getAmount();

        final Iterable<Block> blocks = Utilities.bfs(
            start,
            fillSize,
            false,
            5f,
            SEARCH_FACES,
            MaterialList.AIR,
            MaterialList.EMPTY,
            false,
            true
        );

        int blocksPlaced = 0;
        for (final Block block : blocks) {
            if (block.getType() != AIR) {
                continue;
            }

            if (WorldInteractionUtil.placeBlock(block, event.getPlayer(), material, null)) {
                blocksPlaced++;
            }
        }
        Utilities.removeMaterialsFromPlayer(event.getPlayer(), material, blocksPlaced);
        Utilities.damageItemStackRespectUnbreaking(event.getPlayer(), blocksPlaced, slot);
        return true;
    }
}
