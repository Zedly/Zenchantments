/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zedly.zenchantments.compatibility;

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
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftMushroomCow;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftSheep;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.Location;
import static org.bukkit.Material.*;
import org.bukkit.entity.EntityType;
import static org.bukkit.entity.EntityType.*;


/**
 *
 * @author Dennis
 */
public class NMS_1_11_R1 extends CompatibilityAdapter {

    private static final NMS_1_11_R1 INSTANCE = new NMS_1_11_R1();

    private static final Material[] UNBREAKABLE_BLOCKS = {AIR, BEDROCK, WATER, STATIONARY_WATER,
        LAVA, STATIONARY_LAVA, PISTON_EXTENSION, PISTON_MOVING_PIECE, PORTAL, ENDER_PORTAL,
        ENDER_PORTAL_FRAME, DRAGON_EGG, BARRIER, END_GATEWAY, STRUCTURE_BLOCK};

    private static final Material[] STORAGE_BLOCKS = {DISPENSER, MOB_SPAWNER, CHEST, FURNACE,
        BURNING_FURNACE, JUKEBOX, ENDER_CHEST, COMMAND, BEACON, TRAPPED_CHEST, HOPPER, DROPPER,
        OBSERVER, 
        PURPLE_SHULKER_BOX, RED_SHULKER_BOX, ORANGE_SHULKER_BOX, YELLOW_SHULKER_BOX, 
        LIME_SHULKER_BOX, GREEN_SHULKER_BOX, LIGHT_BLUE_SHULKER_BOX, BLUE_SHULKER_BOX,
        BLACK_SHULKER_BOX, GRAY_SHULKER_BOX, WHITE_SHULKER_BOX, BROWN_SHULKER_BOX,
        CYAN_SHULKER_BOX, MAGENTA_SHULKER_BOX, PINK_SHULKER_BOX, SILVER_SHULKER_BOX
    };

    private static final Material[] INTERACTABLE_BLOCKS = {
        DISPENSER, NOTE_BLOCK, BED_BLOCK, CHEST, WORKBENCH, FURNACE, BURNING_FURNACE,
        WOODEN_DOOR, LEVER, STONE_BUTTON, JUKEBOX, DIODE_BLOCK_OFF, DIODE_BLOCK_ON, TRAP_DOOR,
        FENCE_GATE, ENCHANTMENT_TABLE, BREWING_STAND, ENDER_CHEST, COMMAND, BEACON, WOOD_BUTTON,
        ANVIL, TRAPPED_CHEST, REDSTONE_COMPARATOR_OFF, REDSTONE_COMPARATOR_ON, DAYLIGHT_DETECTOR,
        HOPPER, DROPPER, SPRUCE_FENCE_GATE, BIRCH_FENCE_GATE, JUNGLE_FENCE_GATE, DARK_OAK_FENCE_GATE,
        ACACIA_FENCE_GATE, SPRUCE_DOOR, BIRCH_DOOR, JUNGLE_DOOR, ACACIA_DOOR, DARK_OAK_DOOR, OBSERVER,
        PURPLE_SHULKER_BOX, STRUCTURE_BLOCK};

    private static final Material ORES[] = new Material[]{COAL_ORE, REDSTONE_ORE, DIAMOND_ORE, GOLD_ORE,
        IRON_ORE, LAPIS_ORE, GLOWSTONE, QUARTZ_ORE, EMERALD_ORE, GLOWING_REDSTONE_ORE};
    
    private static final EntityType[] TRANSFORMATION_ENTITY_TYPES = new EntityType[]{BAT, VEX, STRAY, SKELETON, HUSK, ZOMBIE, SILVERFISH, ENDERMITE, ZOMBIE, PIG_ZOMBIE, VILLAGER, WITCH, COW, MUSHROOM_COW, SLIME, MAGMA_CUBE, WITHER_SKULL, SKELETON, OCELOT, WOLF};


    public static NMS_1_11_R1 getInstance() {
        return INSTANCE;
    }

    /**
     * @return the UNBREAKABLE_BLOCKS
     */
    @Override
    public Material[] getUnbreakableBlocks() {
        return UNBREAKABLE_BLOCKS;
    }

    /**
     * @return the STORAGE_BLOCKS
     */
    @Override
    public Material[] getStorageBlocks() {
        return STORAGE_BLOCKS;
    }

    /**
     * @return the INTERACTABLE_BLOCKS
     */
    @Override
    public Material[] getInteractableBlocks() {
        return INTERACTABLE_BLOCKS;
    }

    /**
     * @return the ores
     */
    @Override
    public Material[] getOres() {
        return ORES;
    }
    
    @Override
    public EntityType[] getTransformationEntityTypes() {
        return TRANSFORMATION_ENTITY_TYPES;
    }
    
    private NMS_1_11_R1() {
    }

    @Override
    public boolean breakBlockNMS(Block block, Player player) {
        EntityPlayer ep = ((CraftPlayer) player).getHandle();
        boolean success = ep.playerInteractManager.breakBlock(new BlockPosition(block.getX(), block.getY(), block.getZ()));
        return success;
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
    public boolean showShulker(Block blockToHighlight, int entityId, Player player) {
        PacketPlayOutSpawnEntityLiving pposel = generateShulkerSpawnPacket(blockToHighlight, entityId);
        if (pposel == null) {
            return false;
        }
        EntityPlayer ep = ((CraftPlayer) player).getHandle();
        ep.playerConnection.networkManager.sendPacket(pposel);
        return true;
    }

    @Override
    public boolean hideShulker(int entityId, Player player) {
        PacketPlayOutEntityDestroy ppoed = new PacketPlayOutEntityDestroy(entityId);
        EntityPlayer ep = ((CraftPlayer) player).getHandle();
        ep.playerConnection.networkManager.sendPacket(ppoed);
        return true;
    }
    
    @Override
    public Entity spawnGuardian(Location loc, boolean elderGuardian) {
        return loc.getWorld().spawnEntity(loc, elderGuardian? EntityType.ELDER_GUARDIAN : EntityType.GUARDIAN);
    }
    
    @Override
    public boolean isZombie(Entity e) {
        return e.getType() == EntityType.ZOMBIE || e.getType() == EntityType.ZOMBIE_VILLAGER;
    }
    
    @Override
    public boolean isBlockSafeToBreak(Block b) {
        Material mat = b.getType();
        return mat.isSolid()
                && !b.isLiquid()
                && !ArrayUtils.contains(INTERACTABLE_BLOCKS, mat)
                && !ArrayUtils.contains(UNBREAKABLE_BLOCKS, mat)
                && !ArrayUtils.contains(STORAGE_BLOCKS, mat);
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
