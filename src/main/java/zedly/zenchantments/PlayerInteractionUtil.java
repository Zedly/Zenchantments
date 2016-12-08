/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zedly.zenchantments;

import net.minecraft.server.v1_11_R1.BlockPosition;
import net.minecraft.server.v1_11_R1.EntityPlayer;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

/**
 *
 * @author Dennis
 */
public class PlayerInteractionUtil {
    
    public static boolean breakBlockAs(Block block, Player player) {
        EntityPlayer ep = ((CraftPlayer) player).getHandle();
        WatcherEnchant.ignoreBlockBreak(true);
        boolean success = ep.playerInteractManager.breakBlock(new BlockPosition(block.getX(), block.getY(), block.getZ()));
        WatcherEnchant.ignoreBlockBreak(false);
        return success;
    }
    
    
    
    
    
    
}
