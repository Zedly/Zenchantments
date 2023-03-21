package zedly.zenchantments.enchantments;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;

import org.bukkit.Bukkit;
import static org.bukkit.Material.AIR;

@AZenchantment(runInSlots = Slots.MAIN_HAND, conflicting = {Shred.class, Anthropomorphism.class, Fire.class, Pierce.class, Reveal.class})
public final class Switch extends Zenchantment {
    @Override
    public boolean onBlockInteract(final @NotNull PlayerInteractEvent event, final int level, final EquipmentSlot slot) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK || !event.getPlayer().isSneaking()) {
            return false;
        }
        
        Block clickedBlock = event.getClickedBlock();
        if(clickedBlock == null) {
            return false;
        }

        // Make sure clicked block is okay to break.
        if (!WorldInteractionUtil.isBlockSafeToBreak(clickedBlock)) {
            return false;
        }

        final Player player = event.getPlayer();
        ItemStack switchItem = null;
        int c = -1;

        // Find a suitable block in hotbar.
        for (int i = 0; i < 9; i++) {
            switchItem = player.getInventory().getItem(i);
            if (switchItem != null
                && switchItem.getType() != AIR
                && switchItem.getType().isSolid()
                && !MaterialList.UNBREAKABLE_BLOCKS.contains(switchItem.getType())
                && !MaterialList.INTERACTABLE_BLOCKS.contains(switchItem.getType())
                && !MaterialList.SHULKER_BOXES.contains(switchItem.getType())
            ) {
                c = i;
                break;
            }
        }

        if (c == -1) {
            // No suitable block in inventory.
            return false;
        }

        // Block has been selected, attempt breaking.
        if (!WorldInteractionUtil.breakBlock(clickedBlock, event.getPlayer())) {
            return false;
        }

        Grab.GRAB_LOCATIONS.put(clickedBlock, event.getPlayer());

        event.setCancelled(true);

        final Material material = switchItem.getType();

        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(
            ZenchantmentsPlugin.getInstance(),
            () -> Grab.GRAB_LOCATIONS.remove(clickedBlock),
            3
        );

        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(ZenchantmentsPlugin.getInstance(), () -> {
            WorldInteractionUtil.placeBlock(clickedBlock, player, material, null);
        }, 1);  // TODO: Check item availability again in next tick, then place, then consume.

        Utilities.removeMaterialsFromPlayer(event.getPlayer(), material, 1);
        return true;
    }
}
