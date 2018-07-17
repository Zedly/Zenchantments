package zedly.zenchantments.enchantments;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import zedly.zenchantments.CustomEnchantment;
import zedly.zenchantments.Storage;
import zedly.zenchantments.enums.Hand;
import zedly.zenchantments.enums.Tool;

import static zedly.zenchantments.enums.Tool.*;

public class Grab extends CustomEnchantment {

    public Grab() {
        super(23);
        maxLevel = 1;
        loreName = "Grab";
        probability = 0;
        enchantable = new Tool[]{PICKAXE, SHOVEL, AXE};
        conflicting = new Class[]{};
        description = "Teleports mined items and XP directly to the player";
        cooldown = 0;
        power = -1.0;
        handUse = Hand.LEFT;
    }

    @Override
    public boolean onBlockBreak(final BlockBreakEvent evt, int level, boolean usedHand) {
        Storage.grabLocs.put(evt.getBlock(), evt.getPlayer().getLocation());
        final Block block = evt.getBlock();
        ADAPTER.breakBlockNMS(evt.getBlock(), evt.getPlayer());
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Storage.zenchantments, () -> {
            Storage.grabLocs.remove(block);
        }, 15);
        return true;
    }
}
