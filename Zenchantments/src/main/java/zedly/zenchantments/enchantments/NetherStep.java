package zedly.zenchantments.enchantments;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import zedly.zenchantments.CustomEnchantment;
import zedly.zenchantments.Storage;
import zedly.zenchantments.enums.Tool;

import static org.bukkit.Material.*;
import static zedly.zenchantments.enums.Tool.BOOTS;

public class NetherStep extends CustomEnchantment {

    public NetherStep() {
        maxLevel = 3;
        loreName = "Nether Step";
        probability = 0;
        enchantable = new Tool[]{BOOTS};
        conflicting = new Class[]{FrozenStep.class};
        description = "Allows the player to slowly but safely walk on lava";
        cooldown = 0;
        power = 1.0;
        handUse = 0;
    }

    public int getEnchantmentId() {
        return 39;
    }

    @Override
    public boolean onScan(Player player, int level, boolean usedHand) {
        if(player.isSneaking() && player.getLocation().getBlock().getType() == STATIONARY_LAVA &&
           !player.isFlying()) {
            player.setVelocity(player.getVelocity().setY(.4));
        }
        Block block = (Block) player.getLocation().add(0, 0.2, 0).getBlock();
        int radius = (int) Math.round(power * level + 2);
        for(int x = -(radius); x <= radius; x++) {
            for(int z = -(radius); z <= radius; z++) {
                Block possiblePlatformBlock = block.getRelative(x, -1, z);
                Location possiblePlatformLoc = possiblePlatformBlock.getLocation();
                if(possiblePlatformLoc.distanceSquared(block.getLocation()) < radius * radius - 2) {
                    if(Storage.fireLocs.containsKey(possiblePlatformLoc)) {
                        Storage.fireLocs.put(possiblePlatformLoc, System.nanoTime());
                    } else if(possiblePlatformBlock.getType() == STATIONARY_LAVA
                              && possiblePlatformBlock.getData() == 0
                              && possiblePlatformBlock.getRelative(0, 1, 0).getType() == AIR) {
                        if(ADAPTER.formBlock(possiblePlatformBlock, SOUL_SAND, (byte) 0, player)) {
                            Storage.fireLocs.put(possiblePlatformLoc, System.nanoTime());
                        }
                    }
                }
            }
        }
        return true;
    }
}
