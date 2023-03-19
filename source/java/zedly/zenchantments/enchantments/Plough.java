package zedly.zenchantments.enchantments;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;
import zedly.zenchantments.event.ZenBlockPlaceEvent;

import java.util.HashSet;
import java.util.UUID;

import static java.util.Objects.*;
import static org.bukkit.Material.*;
import static org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK;

@AZenchantment(runInSlots = Slots.HANDS, conflicting = {})
public final class Plough extends Zenchantment {
    private final HashSet<UUID> pendingOperations = new HashSet<>();

    @Override
    public boolean onBlockInteract(final @NotNull PlayerInteractEvent event, final int level, final EquipmentSlot slot) {
        if (event.getAction() != RIGHT_CLICK_BLOCK) {
            return false;
        }

        final Player player = event.getPlayer();
        final Block clickedBlock = requireNonNull(event.getClickedBlock());
        final Location location = clickedBlock.getLocation();

        // PlayerInteractEvent means run the method, but a following BlockPlaceEvent means a block is being placed, so don't run the method after all.
        // This is why we need to schedule and possibly cancel the method. IMO this is because Bukkit-implementing servers behave inconsistently
        pendingOperations.add(event.getPlayer().getUniqueId());
        Bukkit.getScheduler().scheduleSyncDelayedTask(ZenchantmentsPlugin.getInstance(), () -> {
            delayedOperationhandler(event.getPlayer(), clickedBlock, location, level, slot);
        }, 0);
        return true;
    }

    @Override
    public boolean onBlockPlaceOtherHand(BlockPlaceEvent evt, final int level, final EquipmentSlot slot) {
        if(!(evt instanceof ZenBlockPlaceEvent)) {
            pendingOperations.remove(evt.getPlayer().getUniqueId());
        }
        return false;
    }

    private void delayedOperationhandler(Player player, final Block clickedBlock, final Location location, final int level, final EquipmentSlot slot) {
        if (!pendingOperations.contains(player.getUniqueId())) {
            return;
        }
        pendingOperations.remove(player.getUniqueId());
        performDelayed(player, clickedBlock, location, level, slot);
    }

    private void performDelayed(Player player, final Block clickedBlock, final Location location, final int level, final EquipmentSlot slot) {
        final int radiusXZ = (int) Math.round(this.getPower() * level + 2);
        final int radiusY = 2;

        ItemStack toolUsed = player.getInventory().getItem(slot);
        int numUsesAvailable = Utilities.getUsesRemainingOnTool(toolUsed);
        int unbreakingLevel = Utilities.getUnbreakingLevel(toolUsed);
        int damageApplied = 0;

        for (int x = -radiusXZ; x <= radiusXZ; x++) {
            for (int y = -radiusY; y <= 0; y++) {
                for (int z = -radiusXZ; z <= radiusXZ; z++) {
                    final Block relativeBlock = clickedBlock.getRelative(x, y, z);

                    if (damageApplied >= numUsesAvailable || !(relativeBlock.getLocation().distanceSquared(location) < radiusXZ * radiusXZ)) {
                        continue;
                    }

                    if (((relativeBlock.getType() != DIRT
                        && relativeBlock.getType() != GRASS_BLOCK
                        && relativeBlock.getType() != MYCELIUM))
                        || !MaterialList.AIR.contains(relativeBlock.getRelative(BlockFace.UP).getType())
                    ) {
                        continue;
                    }

                    WorldInteractionUtil.placeBlock(relativeBlock, player, Material.FARMLAND, null);

                    if (Utilities.decideRandomlyIfDamageToolRespectUnbreaking(unbreakingLevel)) {
                        damageApplied++;
                    }
                }
            }
        }

        if (damageApplied != 0) {
            Utilities.damageItemStackIgnoreUnbreaking(player, damageApplied, slot);
        }
    }
}
