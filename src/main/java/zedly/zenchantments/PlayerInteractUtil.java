/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zedly.zenchantments;

import net.minecraft.server.v1_11_R1.BlockPosition;
import net.minecraft.server.v1_11_R1.EntityHuman;
import net.minecraft.server.v1_11_R1.EntityMushroomCow;
import net.minecraft.server.v1_11_R1.EntityPlayer;
import net.minecraft.server.v1_11_R1.EntitySheep;
import net.minecraft.server.v1_11_R1.EnumHand;
import org.bukkit.craftbukkit.v1_11_R1.block.CraftBlockState;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftPlayer;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.MushroomCow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.EntityBlockFormEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityCombustByEntityEvent;

import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Dennis
 */
public class PlayerInteractUtil {

    public static boolean breakBlockNMS(Block block, Player player) {
        EntityPlayer ep = ((CraftPlayer) player).getHandle();
        boolean success = ep.playerInteractManager.breakBlock(new BlockPosition(block.getX(), block.getY(), block.getZ()));
        return success;
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
        Block blockAgainst = blockPlaced.getRelative((blockPlaced.getY() == 0) ? BlockFace.UP : BlockFace.DOWN);
        ItemStack itemHeld = new ItemStack(mat, 1, (short) blockData);
        BlockPlaceEvent placeEvent = new BlockPlaceEvent(blockPlaced, blockAgainst.getState(), blockAgainst, itemHeld, player, true);
        Bukkit.getPluginManager().callEvent(placeEvent);
        if (!placeEvent.isCancelled()) {
            blockPlaced.setType(mat);
            blockPlaced.setData((byte) blockData);
            return true;
        }
        return false;
    }

    public static boolean attackEntityNMS(LivingEntity target, Player attacker, double damage) {
        EntityDamageByEntityEvent damageEvent = new EntityDamageByEntityEvent(attacker, target, DamageCause.ENTITY_ATTACK, damage);
        Bukkit.getPluginManager().callEvent(damageEvent);
        if (!damageEvent.isCancelled()) {
            target.damage(damage);
            return true;
        }
        return false;
    }

    public static boolean shearEntityNMS(Entity target, Player player, boolean mainHand) {
        if (target instanceof Sheep) {
            EntitySheep entitySheep = (EntitySheep) target;
            return entitySheep.a((EntityHuman) player, mainHand ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND);
        } else if (target instanceof MushroomCow) {
            EntityMushroomCow entityMushroomCow = (EntityMushroomCow) target;
            return entityMushroomCow.a((EntityHuman) player, mainHand ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND);
        }
        return false;
    }

    public static boolean haulOrBreakBlock(Block from, Block to, BlockFace face, Player player) {
        BlockState state = from.getState();
        if (state.getClass() != CraftBlockState.class) {
            return false;
        }
        BlockBreakEvent breakEvent = new BlockBreakEvent(from, player);
        Bukkit.getPluginManager().callEvent(breakEvent);
        if (breakEvent.isCancelled()) {
            return false;
        }
        ItemStack stack = new ItemStack(state.getType(), 1, state.getData().getData());
        from.setType(Material.AIR);
        from.setData((byte) 0);
        BlockPlaceEvent placeEvent = new BlockPlaceEvent(to, to.getRelative(face.getOppositeFace()).getState(), to.getRelative(face.getOppositeFace()), stack, player, true);
        Bukkit.getPluginManager().callEvent(placeEvent);
        if (placeEvent.isCancelled()) {
            from.getWorld().dropItem(from.getLocation(), stack);
            return true;
        }
        to.setType(state.getType());
        to.setData(state.getData().getData());
        return true;
    }

    public static boolean igniteEntity(Entity target, Player player, int duration) {
        EntityCombustByEntityEvent evt = new EntityCombustByEntityEvent(target, player, duration);
        Bukkit.getPluginManager().callEvent(evt);
        if (!evt.isCancelled()) {
            target.setFireTicks(duration);
            return true;
        }
        return false;
    }

    public static boolean damagePlayer(Player player, double damage, DamageCause cause) {
        EntityDamageEvent evt = new EntityDamageEvent(player, cause, damage);
        Bukkit.getPluginManager().callEvent(evt);
        if (!evt.isCancelled()) {
            player.setLastDamageCause(evt);
            player.damage(damage);
            return true;
        }
        return false;
    }

    public static boolean formBlock(Block block, Material mat, byte data, Player player) {
        EntityBlockFormEvent evt = new EntityBlockFormEvent(player, block, new MockBlockState(block, Material.FROSTED_ICE, (byte) 0));
        Bukkit.getPluginManager().callEvent(evt);
        if (!evt.isCancelled()) {
            block.setType(mat);
            block.setData(data);
            return true;
        }
        return false;
    }
    
}
