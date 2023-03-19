package zedly.zenchantments.enchantments;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;

import java.util.HashSet;
import java.util.UUID;

import static java.util.Objects.requireNonNull;
import static org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK;

@AZenchantment(runInSlots = Slots.HANDS, conflicting = {})
public final class Harvest extends Zenchantment {
    private final HashSet<UUID> pendingOperations = new HashSet<>();

    @Override
    public boolean onBlockInteract(final @NotNull PlayerInteractEvent event, final int level, final EquipmentSlot slot) {
        if (event.getAction() != RIGHT_CLICK_BLOCK) {
            return false;
        }

        final Block clickedBlock = requireNonNull(event.getClickedBlock());
        final Location location = clickedBlock.getLocation();


        // PlayerInteractEvent means run the method, but a following BlockPlaceEvent means a block is being placed, so don't run the method after all.
        // This is why we need to schedule and possibly cancel the method. IMO this is because Bukkit-implementing servers behave inconsistently
        pendingOperations.add(event.getPlayer().getUniqueId());
        Bukkit.getScheduler().scheduleSyncDelayedTask(ZenchantmentsPlugin.getInstance(), () -> {delayedOperationhandler(event.getPlayer(), clickedBlock, location, level, slot);}, 0);
        return true;
    }

    @Override
    public boolean onBlockPlaceOtherHand(final @NotNull BlockPlaceEvent event, final int level, final EquipmentSlot slot) {
        pendingOperations.remove(event.getPlayer().getUniqueId());
        return false;
    }

    private void delayedOperationhandler(Player player, final Block clickedBlock, final Location location, final int level, final EquipmentSlot slot) {
        if(!pendingOperations.contains(player.getUniqueId())) {
            return;
        }
        pendingOperations.remove(player.getUniqueId());
        performDelayed(player, clickedBlock, location, level, slot);
    }

    private void performDelayed(Player player, final Block clickedBlock, final Location location, final int level, final EquipmentSlot slot) {
        final int radiusXZ = (int) Math.round(this.getPower() * level + 2);

        ItemStack toolUsed = player.getInventory().getItem(slot);
        int numUsesAvailable = Utilities.getUsesRemainingOnTool(toolUsed);
        int unbreakingLevel = Utilities.getUnbreakingLevel(toolUsed);
        int damageApplied = 0;
        int harvestedBlocks = 0;

        for (int x = -radiusXZ; x <= radiusXZ; x++) {
            for (int y = -2; y <= 0; y++) {
                for (int z = -radiusXZ; z <= radiusXZ; z++) {
                    final Block block = location.getBlock().getRelative(x, y, z);

                    if (damageApplied >= numUsesAvailable || !(block.getLocation().distanceSquared(location) < radiusXZ * radiusXZ)) {
                        continue;
                    }

                    if (!MaterialList.GROWN_CROPS.contains(block.getType())
                        && !MaterialList.GROWN_CROP_BLOCKS.contains(block.getType())
                    ) {
                        continue;
                    }

                    final BlockData cropState = block.getBlockData();

                    // Is this block the crop's mature form?
                    boolean harvestReady = !(cropState instanceof Ageable);

                    // Is the mature form not a separate Material but just a particular data value?
                    if (!harvestReady) {
                        Ageable ageable = (Ageable) cropState;
                        harvestReady = ageable.getAge() == ageable.getMaximumAge();
                    }

                    if (!harvestReady) {
                        harvestReady = block.getType() == Material.SWEET_BERRY_BUSH;
                    }

                    if (!harvestReady) {
                        continue;
                    }

                    final boolean blockAltered;
                    if (block.getType() == Material.SWEET_BERRY_BUSH) {
                        blockAltered = WorldInteractionUtil.pickBerries(block, player);
                    } else {
                        blockAltered = WorldInteractionUtil.breakBlock(block, player);
                    }

                    if (!blockAltered) {
                        continue;
                    }

                    Grab.GRAB_LOCATIONS.put(block, player);

                    ZenchantmentsPlugin.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(
                        ZenchantmentsPlugin.getInstance(),
                        () -> Grab.GRAB_LOCATIONS.remove(block),
                        3
                    );

                    harvestedBlocks++;
                    if (Utilities.decideRandomlyIfDamageToolRespectUnbreaking(unbreakingLevel)) {
                        damageApplied++;
                    }
                }
            }
        }

        if(harvestedBlocks > 0) {
            Utilities.damageItemStackIgnoreUnbreaking(player, damageApplied, slot);
            // damage tool or not
        }
    }
}
