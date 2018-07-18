package zedly.zenchantments.enchantments;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.player.PlayerInteractEvent;
import zedly.zenchantments.CustomEnchantment;
import zedly.zenchantments.Storage;
import zedly.zenchantments.Utilities;
import zedly.zenchantments.enums.Hand;
import zedly.zenchantments.enums.Tool;

import static org.bukkit.Material.*;
import static org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK;
import static zedly.zenchantments.enums.Tool.HOE;

public class Plough extends CustomEnchantment {

    public Plough() {
        super(43);
        maxLevel = 3;
        loreName = "Plough";
        probability = 0;
        enchantable = new Tool[]{HOE};
        conflicting = new Class[]{};
        description = "Tills all soil within a radius";
        cooldown = 0;
        power = 1.0;
        handUse = Hand.RIGHT;
    }

    @Override
    public boolean onBlockInteract(PlayerInteractEvent evt, int level, boolean usedHand) {
        if(evt.getAction() == RIGHT_CLICK_BLOCK) {
            Location loc = evt.getClickedBlock().getLocation();
            int radiusXZ = (int) Math.round(power * level + 2);
            int radiusY = 1;
            for(int x = -(radiusXZ); x <= radiusXZ; x++) {
                for(int y = -(radiusY) - 1; y <= radiusY - 1; y++) {
                    for(int z = -(radiusXZ); z <= radiusXZ; z++) {
                        Block block = (Block) loc.getBlock();
                        if(block.getRelative(x, y, z).getLocation().distanceSquared(loc) < radiusXZ * radiusXZ) {
                            if(((block.getRelative(x, y, z).getType() == DIRT
                                 || block.getRelative(x, y, z).getType() == GRASS
                                 || block.getRelative(x, y, z).getType() == MYCEL))
                               && block.getRelative(x, y + 1, z).getType() == AIR) {
                                ADAPTER.placeBlock(block.getRelative(x, y, z), evt.getPlayer(), SOIL, 0);
                                if(Storage.rnd.nextBoolean()) {
                                    Utilities.damageTool(evt.getPlayer(), 1, usedHand);
                                }
                            }
                        }
                    }
                }
            }
            return true;
        }
        return false;
    }
}
