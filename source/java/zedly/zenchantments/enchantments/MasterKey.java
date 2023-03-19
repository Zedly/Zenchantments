package zedly.zenchantments.enchantments;

import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.block.data.Openable;
import org.bukkit.block.data.type.TrapDoor;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;

import java.util.concurrent.ThreadLocalRandom;

import static org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK;

@AZenchantment(runInSlots = Slots.ARMOR, conflicting = {})
public class MasterKey extends Zenchantment {
    @Override
    public boolean onBlockInteract(final @NotNull PlayerInteractEvent event, final int level, final EquipmentSlot slot) {
        if (event.getAction() == RIGHT_CLICK_BLOCK && !event.getPlayer().isSneaking()) {
            Block block = event.getClickedBlock();
            if (block.getType() == Material.IRON_DOOR || block.getType() == Material.IRON_TRAPDOOR) {
                event.setCancelled(true);

                MockBlock mockBlock = new MockBlock(block, block.getType() == Material.IRON_DOOR ? Material.OAK_DOOR : Material.OAK_TRAPDOOR);
                PlayerInteractEvent mockEvent = new PlayerInteractEvent(event.getPlayer(), event.getAction(), event.getItem(), mockBlock, event.getBlockFace());
                Bukkit.getPluginManager().callEvent(mockEvent);


                if (mockEvent.useInteractedBlock() != Event.Result.DENY && mockEvent.useItemInHand() != Event.Result.DENY) {
                    Openable door = (Openable) block.getBlockData();
                    block.getWorld().playSound(block.getLocation(),
                        door instanceof TrapDoor ? (door.isOpen() ? Sound.BLOCK_IRON_TRAPDOOR_CLOSE : Sound.BLOCK_IRON_TRAPDOOR_OPEN) :
                            (door.isOpen() ? Sound.BLOCK_IRON_DOOR_CLOSE : Sound.BLOCK_IRON_DOOR_OPEN), 1, ThreadLocalRandom.current().nextFloat() * 1.5f + 0.5f);
                    door.setOpen(!door.isOpen());
                    block.setBlockData(door);
                }
            }
        }
        return false;
    }
}
