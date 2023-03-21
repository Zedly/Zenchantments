package zedly.zenchantments.enchantments;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@AZenchantment(runInSlots = Slots.HANDS, conflicting = {Switch.class, Variety.class})
public final class Fire extends Zenchantment {
    // Locations where Fire has been used on a block and the drop was changed.
    // BlockBreakEvent is not cancelled but the original item drop is not desired.
    public static final HashMap<Block, AtomicInteger> ITEM_DROP_REPLACEMENTS = new HashMap<>();

    @Override
    public boolean onBlockBreak(final @NotNull BlockBreakEvent event, final int level, final EquipmentSlot slot) {
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE || !event.isDropItems()) {
            return false;
        }

        final Player player = event.getPlayer();
        final Block block = event.getBlock();
        if (!ITEM_DROP_REPLACEMENTS.containsKey(block)) {
            ITEM_DROP_REPLACEMENTS.put(block, new AtomicInteger(0));
            // Tool damage is applied one tick after breaking the block, after all the items have dropped.
            // This theoretically opens a window to redirect the tool damage to something else.
            // However we will consider this an acceptable issue, as the alternative (which used to be how we did it)
            // was to duplicate the event, fire it again, wait for all other plugins to consume it, then break the block
            // in code using NMS, observe the drops, and alter the tool damage based on that.
            Bukkit.getScheduler().scheduleSyncDelayedTask(ZenchantmentsPlugin.getInstance(), () -> {
                AtomicInteger itemsSmelted = ITEM_DROP_REPLACEMENTS.get(block);
                Utilities.damageItemStackRespectUnbreaking(player, itemsSmelted.get(), slot);
                ITEM_DROP_REPLACEMENTS.remove(block);
            }, 1);
        }
        return false;
    }
}
