package zedly.zenchantments.event;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.jetbrains.annotations.NotNull;

public class BlockShredEvent extends BlockBreakEvent {
    public BlockShredEvent(@NotNull Block block, @NotNull Player player) {
        super(block, player);
    }
}