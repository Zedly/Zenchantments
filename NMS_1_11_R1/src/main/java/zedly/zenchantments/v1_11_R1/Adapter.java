/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zedly.zenchantments.v1_11_R1;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.UUID;
import net.minecraft.server.v1_11_R1.BlockPosition;
import net.minecraft.server.v1_11_R1.DataWatcher;
import net.minecraft.server.v1_11_R1.EntityMushroomCow;
import net.minecraft.server.v1_11_R1.EntityPlayer;
import net.minecraft.server.v1_11_R1.EntitySheep;
import net.minecraft.server.v1_11_R1.EnumHand;
import net.minecraft.server.v1_11_R1.PacketDataSerializer;
import net.minecraft.server.v1_11_R1.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_11_R1.PacketPlayOutSpawnEntityLiving;
import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.craftbukkit.v1_11_R1.block.CraftBlockState;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftMushroomCow;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftSheep;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.EntityBlockFormEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityCombustByEntityEvent;

import org.bukkit.inventory.ItemStack;
import zedly.zenchantments.NMSAdapter;

/**
 *
 * @author Dennis
 */
public class Adapter implements NMSAdapter {

    private static Adapter instance = new Adapter();

    public static Adapter getInstance() {
        return instance;
    }

    private Adapter() {
    }

    @Override
    public boolean breakBlockNMS(Block block, Player player) {
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
        if (target instanceof CraftSheep) {
            EntitySheep entitySheep = ((CraftSheep) target).getHandle();
            return entitySheep.a(((CraftPlayer) player).getHandle(), mainHand ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND);
        } else if (target instanceof CraftMushroomCow) {
            EntityMushroomCow entityMushroomCow = ((CraftMushroomCow) target).getHandle();
            return entityMushroomCow.a(((CraftPlayer) player).getHandle(), mainHand ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND);
        }
        return false;
    }

    @Override
    public boolean haulOrBreakBlock(Block from, Block to, BlockFace face, Player player) {
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
    public void showShulker(Block blockToHighlight, int entityId, Player player) {
        PacketPlayOutSpawnEntityLiving pposel = generateShulkerSpawnPacket(blockToHighlight, entityId);
        if (pposel == null) {
            return;
        }
        EntityPlayer ep = ((CraftPlayer) player).getHandle();
        ep.playerConnection.networkManager.sendPacket(pposel);
    }

    @Override
    public void hideShulker(int entityId, Player player) {
        PacketPlayOutEntityDestroy ppoed = new PacketPlayOutEntityDestroy(entityId);
        EntityPlayer ep = ((CraftPlayer) player).getHandle();
        ep.playerConnection.networkManager.sendPacket(ppoed);
    }

    private static PacketPlayOutSpawnEntityLiving generateShulkerSpawnPacket(Block blockToHighlight, int entityId) {
        PacketPlayOutSpawnEntityLiving pposel = new PacketPlayOutSpawnEntityLiving();
        Class clazz = pposel.getClass();

        try {
            Field f = clazz.getDeclaredField("a");
            f.setAccessible(true);
            f.setInt(pposel, entityId);
            f = clazz.getDeclaredField("b");
            f.setAccessible(true);
            f.set(pposel, new UUID(0xFF00FF00FF00FF00L, 0xFF00FF00FF00FF00L));
            f = clazz.getDeclaredField("c");
            f.setAccessible(true);
            f.setInt(pposel, 69);
            f = clazz.getDeclaredField("d");
            f.setAccessible(true);
            f.setDouble(pposel, blockToHighlight.getX() + 0.5);
            f = clazz.getDeclaredField("e");
            f.setAccessible(true);
            f.setDouble(pposel, blockToHighlight.getY());
            f = clazz.getDeclaredField("f");
            f.setAccessible(true);
            f.setDouble(pposel, blockToHighlight.getZ() + 0.5);
            f = clazz.getDeclaredField("g");
            f.setAccessible(true);
            f.setInt(pposel, 0);
            f = clazz.getDeclaredField("h");
            f.setAccessible(true);
            f.setInt(pposel, 0);
            f = clazz.getDeclaredField("i");
            f.setAccessible(true);
            f.setInt(pposel, 0);
            f = clazz.getDeclaredField("j");
            f.setAccessible(true);
            f.setByte(pposel, (byte) 0);
            f = clazz.getDeclaredField("k");
            f.setAccessible(true);
            f.setByte(pposel, (byte) 0);
            f = clazz.getDeclaredField("l");
            f.setAccessible(true);
            f.setByte(pposel, (byte) 0);

            DataWatcher m = new FakeDataWatcher();
            f = clazz.getDeclaredField("m");
            f.setAccessible(true);
            f.set(pposel, m);

        } catch (NoSuchFieldException | IllegalAccessException ex) {
            ex.printStackTrace();
            return null;
        }

        return pposel;
    }

    private static class FakeDataWatcher extends DataWatcher {

        public FakeDataWatcher() {
            super(null); // We don't actually need DataWatcher methods, just the inheritance
        }

        // Inject metadata into network stream
        @Override
        public void a(PacketDataSerializer pds) throws IOException {
            pds.writeByte(0); // Set Metadata at index 0
            pds.writeByte(0); // Value is type Byte
            pds.writeByte(0x60); // Set Glowing and Invisible bits
            pds.writeByte(0xFF); // Index -1 indicates end of Metadata
        }
    }
}
