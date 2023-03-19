package zedly.zenchantments.enchantments;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;

import java.util.HashMap;
import java.util.Map;

@AZenchantment(runInSlots = Slots.HANDS, conflicting = {})
public final class Grab extends Zenchantment {
    public static final Map<Block, Player> GRAB_LOCATIONS = new HashMap<>();

    @Override
    public boolean onBlockBreak(final @NotNull BlockBreakEvent event, final int level, final EquipmentSlot slot) {
        final Block block = event.getBlock();

        GRAB_LOCATIONS.put(block, event.getPlayer());

        ZenchantmentsPlugin.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(
            ZenchantmentsPlugin.getInstance(),
            () -> GRAB_LOCATIONS.remove(block),
            3
        );

        return true;
    }
}
