package zedly.zenchantments.event;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ZenBlockPlaceEvent extends BlockPlaceEvent {

    public ZenBlockPlaceEvent(@NotNull Block placedBlock, @NotNull BlockState replacedBlockState, @NotNull Block placedAgainst, @NotNull ItemStack itemInHand, @NotNull Player thePlayer, boolean canBuild, @NotNull EquipmentSlot hand) {
        super(placedBlock, replacedBlockState, placedAgainst, itemInHand, thePlayer, canBuild, hand);
    }
}
