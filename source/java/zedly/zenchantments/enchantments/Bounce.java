package zedly.zenchantments.enchantments;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;

import static org.bukkit.Material.SLIME_BLOCK;

@AZenchantment(runInSlots = Slots.ARMOR , conflicting = {})
public final class Bounce extends Zenchantment {
    @Override
    public boolean onFastScan(final @NotNull Player player, final int level, final EquipmentSlot slot) {
        if (player.getVelocity().getY() >= 0) {
            return false;
        }

        final Block block = player.getLocation().getBlock();
        if (block.getRelative(0, -1, 0).getType() == SLIME_BLOCK
            || block.getType() == SLIME_BLOCK
            || block.getRelative(0, -2, 0).getType() == SLIME_BLOCK
            && (level * this.getPower()) > 2.0
        ) {
            if (!player.isSneaking()) {
                player.setVelocity(player.getVelocity().setY(0.56 * level * this.getPower()));
                return true;
            }

            player.setFallDistance(0);
        }

        return false;
    }
}
