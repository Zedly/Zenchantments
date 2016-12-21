/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zedly.zenchantments;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

/**
 *
 * @author Dennis
 */
public interface NMSAdapter {

    boolean breakBlockNMS(Block block, Player player);

    /**
     * Places a block on the given player's behalf. Fires a BlockPlaceEvent with
     * (nearly) appropriate parameters to probe the legitimacy (permissions etc)
     * of the action and to communicate to other plugins where the block is
     * coming from.
     *
     * @param blockPlaced the block to be changed
     * @param player the player whose identity to use
     * @param mat the material to set the block to, if allowed
     * @param blockData the block data to set for the block, if allowed
     * @return true if the block placement has been successful
     */
    boolean placeBlock(Block blockPlaced, Player player, Material mat, int blockData);

    boolean attackEntity(LivingEntity target, Player attacker, double damage);

    boolean shearEntityNMS(Entity target, Player player, boolean mainHand);

    boolean haulOrBreakBlock(Block from, Block to, BlockFace face, Player player);

    boolean igniteEntity(Entity target, Player player, int duration);

    boolean damagePlayer(Player player, double damage, DamageCause cause);

    boolean formBlock(Block block, Material mat, byte data, Player player);

    boolean showShulker(Block blockToHighlight, int entityId, Player player);

    boolean hideShulker(int entityId, Player player);

}
