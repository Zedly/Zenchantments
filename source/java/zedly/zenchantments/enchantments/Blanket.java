package zedly.zenchantments.enchantments;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;

import java.util.HashSet;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

@AZenchantment(runInSlots = Slots.MAIN_HAND, conflicting = {})
public class Blanket extends Zenchantment {
    private final HashSet<UUID> pendingOperations = new HashSet<>();

    @Override
    public boolean onBlockInteract(final @NotNull PlayerInteractEvent event, final int level, final EquipmentSlot slot) {
        if (event.getAction() != Action.LEFT_CLICK_BLOCK) {
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

        for (int x = -radiusXZ; x <= radiusXZ; x++) {
            for (int y = -2; y <= 2; y++) {
                for (int z = -radiusXZ; z <= radiusXZ; z++) {
                    final Block block = location.getBlock().getRelative(x, y, z);

                    if (!(block.getLocation().distanceSquared(location) < radiusXZ * radiusXZ)) {
                        continue;
                    }

                    if(block.getType() != Material.FIRE) {
                        continue;
                    }

                    final boolean blockAltered = WorldInteractionUtil.breakBlock(block, player);
                    if (!blockAltered) {
                        continue;
                    }
                }
            }
        }
    }
}
