package zedly.zenchantments.enchantments;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import zedly.zenchantments.CustomEnchantment;
import zedly.zenchantments.Storage;
import zedly.zenchantments.Utilities;
import zedly.zenchantments.enums.Hand;
import zedly.zenchantments.enums.Tool;

import static org.bukkit.Material.*;
import static zedly.zenchantments.enums.Tool.PICKAXE;

public class Switch extends CustomEnchantment {

    public static final int ID = 60;

    @Override
    public Builder<Switch> defaults() {
        return new Builder<>(Switch::new, ID)
            .maxLevel(1)
            .loreName("Switch")
            .probability(0)
            .enchantable(new Tool[]{PICKAXE})
            .conflicting(new Class[]{Shred.class, Anthropomorphism.class, Fire.class, Extraction.class, Pierce.class,
                Reveal.class})
            .description("Replaces the clicked block with the leftmost block in your hotbar when sneaking")
            .cooldown(0)
            .power(-1.0)
            .handUse(Hand.RIGHT);
    }

    @Override
    public boolean onBlockInteract(final PlayerInteractEvent evt, int level, boolean usedHand) {
        if(evt.getAction() == Action.RIGHT_CLICK_BLOCK && evt.getPlayer().isSneaking()) {
            // Make sure clicked block is okay to break
            if(!ADAPTER.isBlockSafeToBreak(evt.getClickedBlock())) {
                return false;
            }

            Player player = evt.getPlayer();
            int c = -1;
            ItemStack switchItem = null;
            for(int i = 0; i < 9; i++) { // Find a suitable block in hotbar
                switchItem = player.getInventory().getItem(i);
                if(switchItem != null
                   && switchItem.getType() != AIR
                   && switchItem.getType().isSolid()
                   && !Storage.COMPATIBILITY_ADAPTER.UNBREAKABLE_BLOCKS.contains(switchItem.getType())
                   && !Storage.COMPATIBILITY_ADAPTER.INTERACTABLE_BLOCKS.contains(switchItem.getType())) {
                    c = i;
                    break;
                }
            }
            if(c == -1) { // No suitable block in inventory
                return false;
            }

            // Block has been selected, attempt breaking
            if(!ADAPTER.breakBlockNMS(evt.getClickedBlock(), evt.getPlayer())) {
                return false;
            }

            // Breaking succeeded, begin invasive operations
            Block clickedBlock = evt.getClickedBlock();
            Grab.grabLocs.put(clickedBlock, evt.getPlayer().getLocation());
            evt.setCancelled(true);

            Material mat = switchItem.getType();

            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Storage.zenchantments, () -> {
                Grab.grabLocs.remove(clickedBlock);
            }, 3);

            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Storage.zenchantments, () -> {
                ADAPTER.placeBlock(clickedBlock, player, mat, null); // TODO blockData
            }, 1);
            Utilities.removeItem(evt.getPlayer(), mat,  1);
            return true;
        }
        return false;
    }
}
