package zedly.zenchantments.enchantments;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;

import static org.bukkit.Material.SLIME_BLOCK;

@AZenchantment(runInSlots = Slots.ARMOR, conflicting = {})
public final class Bounce extends Zenchantment {
    @Override
    public boolean onFastScan(final @NotNull Player player, final int level, final EquipmentSlot slot) {
        if (player.getVelocity().getY() >= -0.1) {
            return false;
        }

        if (!player.isSneaking()) {
            final Block block = player.getLocation().getBlock();
            double verticalVelocity = player.getVelocity().getY();
            if ((verticalVelocity < (player.getLocation().getBlockY() - player.getLocation().getY())
                && block.getRelative(0, -1, 0).getType() == SLIME_BLOCK)
                || block.getType() == SLIME_BLOCK) {
                player.setVelocity(player.getVelocity().setY(-verticalVelocity * this.getPower() * 0.95f));
                player.setFallDistance(0);
                return true;
            }
        }
        return false;
    }
}
