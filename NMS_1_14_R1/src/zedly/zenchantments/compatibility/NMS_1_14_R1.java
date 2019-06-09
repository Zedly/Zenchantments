package zedly.zenchantments.compatibility;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.UUID;

import net.minecraft.server.v1_14_R1.BlockPosition;
import net.minecraft.server.v1_14_R1.DataWatcher;
import net.minecraft.server.v1_14_R1.EntityMushroomCow;
import net.minecraft.server.v1_14_R1.EntityPlayer;
import net.minecraft.server.v1_14_R1.EntitySheep;
import net.minecraft.server.v1_14_R1.EnumHand;
import net.minecraft.server.v1_14_R1.PacketDataSerializer;
import net.minecraft.server.v1_14_R1.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_14_R1.PacketPlayOutSpawnEntityLiving;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Bamboo;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftMushroomCow;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftSheep;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockGrowEvent;

import static org.bukkit.Material.*;
import static org.bukkit.Material.TROPICAL_FISH;
import static org.bukkit.entity.EntityType.*;
import static org.bukkit.entity.EntityType.VEX;

public class NMS_1_14_R1 extends CompatibilityAdapter {

    private static final NMS_1_14_R1 INSTANCE = new NMS_1_14_R1();
    public static NMS_1_14_R1 getInstance() {
        return INSTANCE;
    }

    private NMS_1_14_R1() {
    }

    //region Dyes
    private EnumStorage<Material> DYES;

    public EnumStorage<Material> Dyes(){
        dyesInit();
        return DYES;
    }

    private void dyesInit() {
        if (DYES == null) {
            DYES = new EnumStorage<>(new Material[]{WHITE_DYE, ORANGE_DYE, MAGENTA_DYE,
                LIGHT_BLUE_DYE, YELLOW_DYE, LIME_DYE, PINK_DYE, GRAY_DYE, LIGHT_GRAY_DYE, CYAN_DYE, PURPLE_DYE,
                BLUE_DYE, BROWN_DYE, GREEN_DYE, RED_DYE, BLACK_DYE});
        }
    }

    //endregion

    //region Storage Blocks
    private EnumStorage<Material> STORAGE_BLOCKS;

    @Override
    public EnumStorage<Material> StorageBlocks(){
        storageBlocksInit();
        return STORAGE_BLOCKS;
    }

    private void storageBlocksInit() {
        if (STORAGE_BLOCKS == null) {
            STORAGE_BLOCKS = new EnumStorage<>(new Material[]{DISPENSER, SPAWNER,
                CHEST, FURNACE, JUKEBOX, ENDER_CHEST, BEACON, TRAPPED_CHEST, HOPPER, DROPPER, BREWING_STAND, ANVIL,
                BARREL,
                BLAST_FURNACE, LECTERN, SMOKER},
                ShulkerBoxes(), CommandBlocks());
        }
    }

    //endregion

    //region Interactable Blocks
    private EnumStorage<Material> INTERACTABLE_BLOCKS;

    public EnumStorage<Material> InteractableBlocks(){
        interactableBlocksInit();
        return INTERACTABLE_BLOCKS;
    }

    private void interactableBlocksInit() {
        if (INTERACTABLE_BLOCKS == null) {
            INTERACTABLE_BLOCKS = new EnumStorage<>(new Material[]{
                NOTE_BLOCK, CRAFTING_TABLE, LEVER, REPEATER, ENCHANTING_TABLE, COMPARATOR, DAYLIGHT_DETECTOR, OBSERVER, BELL,
                CARTOGRAPHY_TABLE, COMPOSTER, FLETCHING_TABLE, GRINDSTONE, LOOM, SMITHING_TABLE, STONECUTTER},
                Beds(), Doors(), Trapdoors(), FenceGates(), CommandBlocks(), Buttons(), ShulkerBoxes(), StorageBlocks());
        }
    }

    //endregion

    //region Unbreakable Blocks
    private EnumStorage<Material> UNBREAKABLE_BLOCKS;

    public EnumStorage<Material> UnbreakableBlocks(){
        unbreakableBlocks();
        return UNBREAKABLE_BLOCKS;
    }

    private void unbreakableBlocks() {
        if (UNBREAKABLE_BLOCKS == null) {
            UNBREAKABLE_BLOCKS = new EnumStorage<>(new Material[]{BARRIER, BEDROCK,
                BUBBLE_COLUMN, DRAGON_BREATH, DRAGON_EGG, END_CRYSTAL, END_GATEWAY, END_PORTAL, END_PORTAL_FRAME, LAVA,
                STRUCTURE_VOID, STRUCTURE_BLOCK, WATER, PISTON_HEAD, MOVING_PISTON, JIGSAW}, Airs(), CommandBlocks());
        }
    }

    //endregion

    //region Signs
    private EnumStorage<Material> SIGNS;

    public EnumStorage<Material> Signs(){
        signInit();
        return SIGNS;
    }

    private void signInit() {
        if (SIGNS == null) {
            SIGNS = new EnumStorage<>(new Material[]{ACACIA_SIGN, BIRCH_SIGN,
                DARK_OAK_SIGN, JUNGLE_SIGN, OAK_SIGN, SPRUCE_SIGN});
        }
    }

    //endregion

    //region Sandstone Slabs
    private EnumStorage<Material> SANDSTONE_SLABS;

    public EnumStorage<Material> SandstoneSlabs(){
        sandstoneSlabInit();
        return SANDSTONE_SLABS;
    }

    private void sandstoneSlabInit() {
        if (SANDSTONE_SLABS == null) {
            SANDSTONE_SLABS = new EnumStorage<>(new Material[]{SANDSTONE_SLAB,
                SMOOTH_SANDSTONE_SLAB, CUT_SANDSTONE_SLAB, RED_SANDSTONE_SLAB, SMOOTH_RED_SANDSTONE_SLAB, CUT_RED_SANDSTONE_SLAB});
        }
    }

    //endregion

    //region Small Flowers
    private EnumStorage<Material> SMALL_FLOWERS;

    public EnumStorage<Material> SmallFlowers() {
        smallFlowersInit();
        return SMALL_FLOWERS;
    }

    private void smallFlowersInit() {
        if (SMALL_FLOWERS == null) {
            SMALL_FLOWERS = new EnumStorage<>(new Material[]{LILY_OF_THE_VALLEY, DANDELION,
                CORNFLOWER, POPPY, BLUE_ORCHID, ALLIUM, AZURE_BLUET, RED_TULIP, ORANGE_TULIP, WHITE_TULIP, PINK_TULIP, OXEYE_DAISY});
        }
    }

    //endregion

    //region Deadly Plants
    private EnumStorage<Material> DEADLY_PLANTS;

    public EnumStorage<Material> DeadlyPlants() {
        deadlyPlantsInit();
        return DEADLY_PLANTS;
    }

    private void deadlyPlantsInit() {
        if (DEADLY_PLANTS == null) {
            DEADLY_PLANTS = new EnumStorage<>(new Material[]{WITHER_ROSE});
        }
    }

    //endregion

    //region Dead Corals
    private EnumStorage<Material> DEAD_CORALS;

    public EnumStorage<Material> DeadCorals() {
        deadCoralsInit();
        return DEAD_CORALS;
    }

    private void deadCoralsInit() {
        if (DEAD_CORALS == null) {
            DEAD_CORALS = new EnumStorage<>(new Material[]{DEAD_BRAIN_CORAL,
                DEAD_BUBBLE_CORAL, DEAD_FIRE_CORAL, DEAD_HORN_CORAL, DEAD_TUBE_CORAL});
        }
    }

    //endregion

    //region Transformation Entity Types
    private EnumStorage<EntityType> TRANSFORMATION_ENTITY_TYPES;

    public EnumStorage<EntityType> TransformationEntityTypes(){
        transformationEntityTypesInit();
        return TRANSFORMATION_ENTITY_TYPES;
    }

    private void transformationEntityTypesInit() {
        if (TRANSFORMATION_ENTITY_TYPES == null) {
            TRANSFORMATION_ENTITY_TYPES = new EnumStorage<>(new EntityType[]{
                SKELETON, WITHER_SKELETON, ZOMBIE, DROWNED, WITCH, VILLAGER, COW, MUSHROOM_COW, PIG, PIG_ZOMBIE, SILVERFISH,
                ENDERMITE, OCELOT, WOLF, SLIME, MAGMA_CUBE, GUARDIAN, ELDER_GUARDIAN, PARROT, BAT, SPIDER, CAVE_SPIDER, COW,
                MUSHROOM_COW, DONKEY, LLAMA, HORSE, SKELETON_HORSE, BLAZE, VEX});
        }
    }

    //endregion

    //region Fire Raw
    private EnumStorage<Material> FIRE_RAW;

    public EnumStorage<Material> FireRaw(){
        fireRawInit();
        return FIRE_RAW;
    }

    private void fireRawInit() {
        if (FIRE_RAW == null) {
            FIRE_RAW = new EnumStorage<>(new Material[]{DIORITE, ANDESITE, GRANITE,
                IRON_ORE, GOLD_ORE, COBBLESTONE, MOSSY_COBBLESTONE, NETHERRACK, STONE_BRICKS, QUARTZ_BLOCK, SANDSTONE, RED_SANDSTONE, STONE}, Terracottas());
        }
    }

    //endregion

    //region Fire Cooked
    private EnumStorage<Material> FIRE_COOKED;

    public EnumStorage<Material> FireCooked(){
        fireCookedInit();
        return FIRE_COOKED;
    }

    private void fireCookedInit() {
        if (FIRE_COOKED == null) {
            FIRE_COOKED = new EnumStorage<>(new Material[]{POLISHED_DIORITE,
                POLISHED_ANDESITE, POLISHED_GRANITE, IRON_INGOT, GOLD_INGOT, STONE, MOSSY_STONE_BRICKS, NETHER_BRICK,
                CRACKED_STONE_BRICKS, SMOOTH_QUARTZ, SMOOTH_SANDSTONE, SMOOTH_RED_SANDSTONE, SMOOTH_STONE}, GlazedTerracottas());
        }
    }

    //endregion

    //region Gluttony

    //region Gluttony Food Levels
    private int[] GLUTTONY_FOOD_LEVELS;

    public int[] GluttonyFoodLevels(){
        gluttonyFoodLevelsInit();
        return GLUTTONY_FOOD_LEVELS;
    }

    private void gluttonyFoodLevelsInit() {
        if (GLUTTONY_FOOD_LEVELS == null) {
            GLUTTONY_FOOD_LEVELS = new int[]{4, 5, 1, 6, 5, 3, 1, 6, 5, 6, 8, 5, 6, 2, 1, 2, 6, 8, 10, 8, 2, 6};
        }
    }
    //endregion


    //region Gluttony Saturations
    private double[] GLUTTONY_SATURATIONS;

    public double[] GluttonySaturations() {
        gluttonySaturationsInit();
        return GLUTTONY_SATURATIONS;
}

    private void gluttonySaturationsInit() {
        if (GLUTTONY_SATURATIONS == null) {
            GLUTTONY_SATURATIONS = new double[]{2.4, 6, 1.2, 7.2, 6, 3.6, 0.2, 7.2, 6, 9.6, 12.8, 6, 9.6, 0.4, 0.6,
                1.2, 7.2, 4.8, 12, 12.8, 0.4, 7.2};
        }
    }
    //endregion


    //region Gluttony Food Items
    private Material[] GLUTTONY_FOOD_ITEMS;

    public Material[] GluttonyFoodItems(){
        gluttonyFoodItemsInit();
        return GLUTTONY_FOOD_ITEMS;
    }

    private void gluttonyFoodItemsInit() {
        if (GLUTTONY_FOOD_ITEMS == null) {
            GLUTTONY_FOOD_ITEMS  = new Material[]{
                APPLE, BAKED_POTATO, BEETROOT, BEETROOT_SOUP, BREAD, CARROT, TROPICAL_FISH, COOKED_CHICKEN, COOKED_COD,
                COOKED_MUTTON, COOKED_PORKCHOP, COOKED_RABBIT, COOKED_SALMON, COOKIE, DRIED_KELP, MELON_SLICE, MUSHROOM_STEW,
                PUMPKIN_PIE, RABBIT_STEW, COOKED_BEEF, SWEET_BERRIES, SUSPICIOUS_STEW};
        }
    }

    //endregion

    //endregion


    @Override
    public boolean grow(Block cropBlock, Player player) {
        Material mat = cropBlock.getType();
        BlockData data = cropBlock.getBlockData();

        switch (mat) {
            case PUMPKIN_STEM:
            case MELON_STEM:
            case CARROTS:
            case WHEAT:
            case POTATOES:
            case COCOA:
            case NETHER_WART:
            case BEETROOTS:
            case SWEET_BERRY_BUSH:


                BlockData cropState = cropBlock.getBlockData();
                if (cropState instanceof Ageable) {
                    Ageable ag = (Ageable) cropState;
                    if (ag.getAge() >= ag.getMaximumAge()) {
                        return false;
                    }
                    ag.setAge(ag.getAge() + 1);
                    data = ag;
                }
                break;
            case BAMBOO_SAPLING: {
                if (player != null) {
                    boolean result = placeBlock(cropBlock, player, BAMBOO, null);
                    if (!result) {
                        return false;
                    }
                }

                Bamboo bamboo = (Bamboo) cropBlock.getBlockData();
                cropBlock = cropBlock.getRelative(BlockFace.UP);
                bamboo.setLeaves(Bamboo.Leaves.SMALL);
                data = bamboo;

                break;
            }
            case BAMBOO: {
                Bamboo bamboo = (Bamboo) cropBlock.getBlockData();

                int height = 1;
                if (cropBlock.getRelative(BlockFace.DOWN).getType() == mat) { // Only grow if argument is the base
                    // block
                    return false;
                }
                Block testBlock = cropBlock;
                while ((testBlock = testBlock.getRelative(BlockFace.UP)).getType() == mat) {
                    if (++height >= 16) { // Cancel if cactus/cane is fully grown
                        return false;
                    }
                }
                height++;

                boolean result;
                if (player != null) {
                    result = placeBlock(testBlock, player, mat, null);

                    if (!result) {
                        return false;
                    }
                }
                bamboo.setAge(0);

                if (height == 4) {
                    // Top piece
                    bamboo.setLeaves(Bamboo.Leaves.LARGE);
                    bamboo.setAge(1);
                    result = placeBlock(cropBlock.getRelative(0, 3, 0), player, mat, bamboo);

                    if (!result) {
                        return false;
                    }
                }
                if (height == 3 || height == 4) {
                    // Top piece (height = 3) or second from top (height = 4)
                    bamboo.setLeaves(Bamboo.Leaves.SMALL);
                    bamboo.setAge(height == 4 ? 1 : 0);
                    result = placeBlock(cropBlock.getRelative(0, 2, 0), player, mat, bamboo);

                    if (!result) {
                        return false;
                    }

                    // Second from bottom piece
                    bamboo.setAge(0);
                    bamboo.setLeaves(Bamboo.Leaves.NONE);
                    result = placeBlock(cropBlock, player, mat, bamboo);

                    if (!result) {
                        return false;
                    }

                    bamboo.setLeaves(Bamboo.Leaves.SMALL);
                    result = placeBlock(cropBlock.getRelative(0, 1, 0), player, mat, bamboo);

                    if (!result) {
                        return false;
                    }


                }

                if (height > 4) {
                    for (int i = height - 1; i >= 0; i--) {
                        Bamboo.Leaves leaves = i < height - 3 ? Bamboo.Leaves.NONE : i == height - 3 ? Bamboo.Leaves.SMALL : Bamboo.Leaves.LARGE;
                        bamboo.setLeaves(leaves);
                        bamboo.setAge(height == 5 && i < 2 ? 0 : 1);
                        result = placeBlock(cropBlock.getRelative(0, i, 0), player, mat, bamboo);

                        if (!result) {
                            return false;
                        }
                    }
                }
                return true;
            }
            case CACTUS:
            case SUGAR_CANE:
                int height = 1;
                if (cropBlock.getRelative(BlockFace.DOWN).getType() == mat) { // Only grow if argument is the base
                    // block
                    return false;
                }
                while ((cropBlock = cropBlock.getRelative(BlockFace.UP)).getType() == mat) {
                    if (++height >= 3) { // Cancel if cactus/cane is fully grown
                        return false;
                    }
                }
                if (!Airs().contains(cropBlock.getType())) { // Only grow if argument is the base block
                    return false;
                }

                break;
            default:
                return false;
        }

        if (player != null) {
            return placeBlock(cropBlock, player, mat, data);
        }

        BlockState bs = cropBlock.getState();
        bs.setType(mat);
        BlockGrowEvent evt = new BlockGrowEvent(cropBlock, bs);
        Bukkit.getPluginManager().callEvent(evt);
        if (!evt.isCancelled()) {
            cropBlock.setType(mat);
            cropBlock.setBlockData(data);
            return true;
        }
        return false;
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
            f.setInt(pposel, 62); // Mod data (changes every NMS update)
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
