package zedly.zenchantments.compatibility;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.minecraft.server.v1_16_R2.BlockPosition;
import net.minecraft.server.v1_16_R2.DataWatcher;
import net.minecraft.server.v1_16_R2.DataWatcherObject;
import net.minecraft.server.v1_16_R2.DataWatcherRegistry;
import net.minecraft.server.v1_16_R2.DataWatcherSerializer;
import net.minecraft.server.v1_16_R2.EntityCreeper;
import net.minecraft.server.v1_16_R2.EntityExperienceOrb;
import net.minecraft.server.v1_16_R2.EntityHuman;
import net.minecraft.server.v1_16_R2.EntityMushroomCow;
import net.minecraft.server.v1_16_R2.EntityPlayer;
import net.minecraft.server.v1_16_R2.EntitySheep;
import net.minecraft.server.v1_16_R2.EntityTypes;
import net.minecraft.server.v1_16_R2.EnumHand;
import net.minecraft.server.v1_16_R2.EnumInteractionResult;
import net.minecraft.server.v1_16_R2.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_16_R2.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_16_R2.PacketPlayOutSpawnEntityLiving;
import org.bukkit.craftbukkit.v1_16_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_16_R2.entity.CraftMushroomCow;
import org.bukkit.craftbukkit.v1_16_R2.entity.CraftSheep;

import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.craftbukkit.v1_16_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R2.entity.CraftCreeper;

public class NMS_1_16_R1 extends CompatibilityAdapter {

    private static final NMS_1_16_R1 INSTANCE = new NMS_1_16_R1();

    public static NMS_1_16_R1 getInstance() {
        return INSTANCE;
    }    
    
    @Override
    public boolean breakBlockNMS(Block block, Player player) {
        EntityPlayer ep = ((CraftPlayer) player).getHandle();
        return ep.playerInteractManager.breakBlock(new BlockPosition(block.getX(), block.getY(), block.getZ()));
    }

    @Override
    public void collectXP(Player player, int amount) {
        EntityExperienceOrb eOrb = new EntityExperienceOrb(((CraftWorld) player.getWorld()).getHandle(),
                player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(), amount);
        EntityHuman ePlayer = ((CraftPlayer) player).getHandle();
        eOrb.pickup(ePlayer); // XP Orb Entity handles mending. Don't blame me, I didn't code it
        ePlayer.bu = 0; // Reset XP Pickup Timer
    }

    @Override
    public boolean explodeCreeper(Creeper creeper, boolean damageWorld) {
        EntityCreeper ec = ((CraftCreeper) creeper).getHandle();
        ec.explode();
        return true;
    }

    @Override
    public boolean shearEntityNMS(Entity target, Player player, boolean mainHand) {
        if (target instanceof CraftSheep) {
            EntitySheep entitySheep = ((CraftSheep) target).getHandle();
            EnumInteractionResult result = entitySheep.a(((CraftPlayer) player).getHandle(), mainHand ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND);
            return result == EnumInteractionResult.SUCCESS;
        } else if (target instanceof CraftMushroomCow) {
            EntityMushroomCow entityMushroomCow = ((CraftMushroomCow) target).getHandle();
            EnumInteractionResult result = entityMushroomCow.a(((CraftPlayer) player).getHandle(), mainHand ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND);
            return result == EnumInteractionResult.SUCCESS;
        }
        return false;
    }

    @Override
    public boolean showShulker(Block blockToHighlight, int entityId, Player player) {
        return showHighlightBlock(blockToHighlight, player);
    }

    @Override
    public boolean hideShulker(int entityId, Player player) {
        return hideHighlightBlock(entityId, player);
    }
    
    public boolean showHighlightBlock(Block block, Player player) {
        int entityId = 2000000000 + (block.hashCode()) % 10000000;
        return showHighlightBlock(block.getX(), block.getY(), block.getZ(), entityId, player);
    }
    
    public boolean hideHighlightBlock(int entityId, Player player) {
        PacketPlayOutEntityDestroy ppoed = new PacketPlayOutEntityDestroy(entityId);
        EntityPlayer ep = ((CraftPlayer) player).getHandle();
        ep.playerConnection.networkManager.sendPacket(ppoed);
        return true;
    }

    private boolean showHighlightBlock(int x, int y, int z, int entityId, Player player) {
        PacketPlayOutSpawnEntityLiving pposel = generateShulkerSpawnPacket(x, y, z, entityId);
        PacketPlayOutEntityMetadata ppoem = generateShulkerGlowPacket(entityId);
        if (pposel == null) {
            return false;
        }
        if (ppoem == null) {
            return false;
        }
        EntityPlayer ep = ((CraftPlayer) player).getHandle();
        ep.playerConnection.networkManager.sendPacket(pposel);
        ep.playerConnection.networkManager.sendPacket(ppoem);
        return true;
    }

    private static PacketPlayOutEntityMetadata generateShulkerGlowPacket(int entityId) {
        PacketPlayOutEntityMetadata ppoem = new PacketPlayOutEntityMetadata();
        Class clazz = ppoem.getClass();
        try {
            Field f = clazz.getDeclaredField("a");
            f.setAccessible(true);
            f.setInt(ppoem, entityId);

            // Build data structure for Entity Metadata. Requires an index, a type and a value. 
            // As of 1.15.2, an invisible + glowing LivingEntity is set by Index 0 Type Byte Value 0x60
            DataWatcherSerializer<Byte> dws = DataWatcherRegistry.a; // Type (Byte)
            DataWatcherObject<Byte> dwo = new DataWatcherObject<>(0, dws); // Index (0)
            DataWatcher.Item<Byte> dwi = new DataWatcher.Item<>(dwo, (byte) 0x60); // Value (0x60)
            List<DataWatcher.Item<Byte>> list = new ArrayList<>();
            list.add(dwi); // Pack it in a list

            f = clazz.getDeclaredField("b");
            f.setAccessible(true);
            f.set(ppoem, list);
        } catch (NoSuchFieldException | IllegalAccessException ex) {
            ex.printStackTrace();
            return null;
        }
        return ppoem;
    }

    private static PacketPlayOutSpawnEntityLiving generateShulkerSpawnPacket(int x, int y, int z, int entityId) {
        PacketPlayOutSpawnEntityLiving pposel = new PacketPlayOutSpawnEntityLiving();

        int mobTypeId = net.minecraft.server.v1_16_R2.IRegistry.ENTITY_TYPE.a(EntityTypes.SHULKER);

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
            f.setInt(pposel, mobTypeId);
            f = clazz.getDeclaredField("d");
            f.setAccessible(true);
            f.setDouble(pposel, x + 0.5);
            f = clazz.getDeclaredField("e");
            f.setAccessible(true);
            f.setDouble(pposel, y);
            f = clazz.getDeclaredField("f");
            f.setAccessible(true);
            f.setDouble(pposel, z + 0.5);
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
        } catch (NoSuchFieldException | IllegalAccessException ex) {
            ex.printStackTrace();
            return null;
        }
        return pposel;
    }
}
