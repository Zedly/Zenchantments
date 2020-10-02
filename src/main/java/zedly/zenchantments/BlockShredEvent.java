package zedly.zenchantments;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockShredEvent extends BlockBreakEvent {
    public BlockShredEvent(Block block, Player player) {
        super(block, player);
    }
}