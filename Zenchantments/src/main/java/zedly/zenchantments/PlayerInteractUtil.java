/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zedly.zenchantments;

import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import zedly.zenchantments.NMSAdapter;

/**
 *
 * @author Dennis
 */
public class PlayerInteractUtil {

    private static final NMSAdapter adapter;

    static {
        adapter = zedly.zenchantments.v1_11_R1.Adapter.getInstance();
    }

    public static boolean breakBlockNMS(Block block, Player player) {
        if (!ArrayUtils.contains(Storage.UNBREAKABLE_BLOCKS, block.getType())) {
            return adapter.breakBlockNMS(block, player);
        } else {
            return false;
        }
    }

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
    public static boolean placeBlock(Block blockPlaced, Player player, Material mat, int blockData) {
        return adapter.placeBlock(blockPlaced, player, mat, blockData);
    }

    public static boolean attackEntity(LivingEntity target, Player attacker, double damage) {
        return adapter.attackEntity(target, attacker, damage);
    }

    public static boolean shearEntityNMS(Entity target, Player player, boolean mainHand) {
        return adapter.shearEntityNMS(target, player, mainHand);
    }

    public static boolean haulOrBreakBlock(Block from, Block to, BlockFace face, Player player) {
        return adapter.haulOrBreakBlock(from, to, face, player);
    }

    public static boolean igniteEntity(Entity target, Player player, int duration) {
        return adapter.igniteEntity(target, player, duration);
    }

    public static boolean damagePlayer(Player player, double damage, DamageCause cause) {
        return adapter.damagePlayer(player, damage, cause);
    }

    public static boolean formBlock(Block block, Material mat, byte data, Player player) {
        return adapter.formBlock(block, mat, data, player);
    }

    public static void showShulker(Block blockToHighlight, int entityId, Player player) {
        adapter.showShulker(blockToHighlight, entityId, player);
    }

    public static void hideShulker(int entityId, Player player) {
        adapter.hideShulker(entityId, player);
    }
}
