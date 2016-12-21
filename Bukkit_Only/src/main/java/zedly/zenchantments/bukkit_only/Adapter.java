/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zedly.zenchantments.bukkit_only;

import java.util.Random;

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
import org.bukkit.event.player.PlayerShearEntityEvent;

import org.bukkit.inventory.ItemStack;
import zedly.zenchantments.NMSAdapter;

/**
 *
 * @author Dennis
 */
public class Adapter implements NMSAdapter {

    private static final Adapter INSTANCE = new Adapter();
    private static final Random RND = new Random();

    public static Adapter getInstance() {
        return INSTANCE;
    }

    private Adapter() {
    }

    @Override
    public boolean breakBlockNMS(Block block, Player player) {
        BlockBreakEvent evt = new BlockBreakEvent(block, player);
        Bukkit.getPluginManager().callEvent(evt);
        if (!evt.isCancelled()) {
            block.breakNaturally(player.getInventory().getItemInMainHand());
            return true;
        }
        return false;
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
    @Override
    public boolean placeBlock(Block blockPlaced, Player player, Material mat, int blockData) {
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

    @Override
    public boolean attackEntity(LivingEntity target, Player attacker, double damage) {
        EntityDamageByEntityEvent damageEvent = new EntityDamageByEntityEvent(attacker, target, DamageCause.ENTITY_ATTACK, damage);
        Bukkit.getPluginManager().callEvent(damageEvent);
        if (!damageEvent.isCancelled()) {
            target.damage(damage);
            return true;
        }
        return false;
    }

    @Override
    public boolean shearEntityNMS(Entity target, Player player, boolean mainHand) {
        if ((target instanceof Sheep && !((Sheep) target).isSheared()) || target instanceof MushroomCow) {
            PlayerShearEntityEvent evt = new PlayerShearEntityEvent(player, target);
            Bukkit.getPluginManager().callEvent(evt);
            if (!evt.isCancelled()) {
                if (target instanceof Sheep) {
                    Sheep sheep = (Sheep) target;
                    sheep.getLocation().getWorld().dropItem(sheep.getLocation(), new ItemStack(Material.WOOL, RND.nextInt(3) + 1, sheep.getColor().getWoolData()));
                    ((Sheep) target).setSheared(true);
                    // TODO: Apply damage to tool
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean haulOrBreakBlock(Block from, Block to, BlockFace face, Player player) {
        BlockState state = from.getState();
        if (state.getClass().getName().endsWith("CraftBlockState")) {
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

    @Override
    public boolean igniteEntity(Entity target, Player player, int duration) {
        EntityCombustByEntityEvent evt = new EntityCombustByEntityEvent(target, player, duration);
        Bukkit.getPluginManager().callEvent(evt);
        if (!evt.isCancelled()) {
            target.setFireTicks(duration);
            return true;
        }
        return false;
    }

    @Override
    public boolean damagePlayer(Player player, double damage, DamageCause cause) {
        EntityDamageEvent evt = new EntityDamageEvent(player, cause, damage);
        Bukkit.getPluginManager().callEvent(evt);
        if (!evt.isCancelled()) {
            player.setLastDamageCause(evt);
            player.damage(damage);
            return true;
        }
        return false;
    }

    @Override
    public boolean formBlock(Block block, Material mat, byte data, Player player) {
        EntityBlockFormEvent evt = new EntityBlockFormEvent(player, block, new MockBlockState(block, Material.FROSTED_ICE, (byte) 0));
        Bukkit.getPluginManager().callEvent(evt);
        if (!evt.isCancelled()) {
            block.setType(mat);
            block.setData(data);
            return true;
        }
        return false;
    }

    @Override
    public boolean showShulker(Block blockToHighlight, int entityId, Player player) {
        // This cannot be done without NMS
        return false;
    }

    @Override
    public boolean hideShulker(int entityId, Player player) {
        // This cannot be done without NMS
        return false;
    }
}
