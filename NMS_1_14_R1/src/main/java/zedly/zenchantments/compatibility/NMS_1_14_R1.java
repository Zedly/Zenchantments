package zedly.zenchantments.compatibility;

import java.lang.reflect.Field;
import java.util.Random;
import java.util.UUID;

import net.minecraft.server.v1_14_R1.BlockPosition;
import net.minecraft.server.v1_14_R1.DataWatcher;
import net.minecraft.server.v1_14_R1.EntityCreeper;
import net.minecraft.server.v1_14_R1.EntityExperienceOrb;
import net.minecraft.server.v1_14_R1.EntityHuman;
import net.minecraft.server.v1_14_R1.EntityMushroomCow;
import net.minecraft.server.v1_14_R1.EntityPlayer;
import net.minecraft.server.v1_14_R1.EntitySheep;
import net.minecraft.server.v1_14_R1.EnumHand;
import net.minecraft.server.v1_14_R1.PacketDataSerializer;
import net.minecraft.server.v1_14_R1.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_14_R1.PacketPlayOutSpawnEntityLiving;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
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
import org.bukkit.entity.*;
import org.bukkit.event.block.BlockGrowEvent;

import static org.bukkit.Material.*;
import static org.bukkit.Material.TROPICAL_FISH;
import org.bukkit.craftbukkit.v1_14_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftCreeper;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftExperienceOrb;
import static org.bukkit.entity.EntityType.*;
import static org.bukkit.entity.EntityType.PUFFERFISH;
import static org.bukkit.entity.EntityType.VEX;

public class NMS_1_14_R1 extends CompatibilityAdapter {

    private static final NMS_1_14_R1 INSTANCE = new NMS_1_14_R1();
    public static NMS_1_14_R1 getInstance() {
        return INSTANCE;
    }

    //region Enums

    //region Colors

    //region Dyes
    private EnumStorage<Material> DYES_E;

    @Override
    public EnumStorage<Material> Dyes(){
        dyesInit();
        return DYES_E;
    }

    private void dyesInit() {
        if (DYES_E == null) {
            DYES_E = new EnumStorage<>(new Material[]{WHITE_DYE, ORANGE_DYE, MAGENTA_DYE,
                LIGHT_BLUE_DYE, YELLOW_DYE, LIME_DYE, PINK_DYE, GRAY_DYE, LIGHT_GRAY_DYE, CYAN_DYE, PURPLE_DYE,
                BLUE_DYE, BROWN_DYE, GREEN_DYE, RED_DYE, BLACK_DYE});
        }
    }

    //endregion

    //endregion


    //region Woods

    //region Signs
    private EnumStorage<Material> SIGNS_E;

    @Override
    public EnumStorage<Material> Signs(){
        signInit();
        return SIGNS_E;
    }

    private void signInit() {
        if (SIGNS_E == null) {
            SIGNS_E = new EnumStorage<>(new Material[]{ACACIA_SIGN, BIRCH_SIGN,
                DARK_OAK_SIGN, JUNGLE_SIGN, OAK_SIGN, SPRUCE_SIGN});
        }
    }

    //endregion

    //endregion


    //region Plants

    //region Deadly Plants
    private EnumStorage<Material> DEADLY_PLANTS_E;

    @Override
    public EnumStorage<Material> DeadlyPlants() {
        deadlyPlantsInit();
        return DEADLY_PLANTS_E;
    }

    private void deadlyPlantsInit() {
        if (DEADLY_PLANTS_E == null) {
            DEADLY_PLANTS_E = new EnumStorage<>(new Material[]{WITHER_ROSE});
        }
    }

    //endregion


    //region Small Flowers
    private EnumStorage<Material> SMALL_FLOWERS_E;

    @Override
    public EnumStorage<Material> SmallFlowers() {
        smallFlowersInit();
        return SMALL_FLOWERS_E;
    }

    private void smallFlowersInit() {
        if (SMALL_FLOWERS_E == null) {
            SMALL_FLOWERS_E = new EnumStorage<>(new Material[]{LILY_OF_THE_VALLEY, DANDELION,
                CORNFLOWER, POPPY, BLUE_ORCHID, ALLIUM, AZURE_BLUET, RED_TULIP, ORANGE_TULIP, WHITE_TULIP, PINK_TULIP, OXEYE_DAISY});
        }
    }

    //endregion


    //region Partial Harvest Crops
    private EnumStorage<Material> PARTIAL_HARVEST_CROPS_E;

    public EnumStorage<Material> PartialHarvestCrops(){
        partialHarvestCropsInit();
        return PARTIAL_HARVEST_CROPS_E;
    }

    private void partialHarvestCropsInit() {
        if (PARTIAL_HARVEST_CROPS_E == null) {
            PARTIAL_HARVEST_CROPS_E  =
                new EnumStorage<>(new Material[]{SWEET_BERRY_BUSH});
        }
    }
    //endregion


    //region Partial Harvest Crop Yields
    private EnumStorage<Material> PARTIAL_HARVEST_CROP_YIELDS_E;

    public EnumStorage<Material> PartialHarvestCropYeilds(){
        partialHarvestCropYieldsInit();
        return PARTIAL_HARVEST_CROP_YIELDS_E;
    }

    private void partialHarvestCropYieldsInit() {
        if (PARTIAL_HARVEST_CROP_YIELDS_E == null) {
            PARTIAL_HARVEST_CROP_YIELDS_E  =
                new EnumStorage<>(new Material[]{SWEET_BERRIES});
        }
    }
    //endregion


    //region Dead Corals
    private EnumStorage<Material> DEAD_CORALS_E;

    @Override
    public EnumStorage<Material> DeadCorals() {
        deadCoralsInit();
        return DEAD_CORALS_E;
    }

    private void deadCoralsInit() {
        if (DEAD_CORALS_E == null) {
            DEAD_CORALS_E = new EnumStorage<>(new Material[]{DEAD_BRAIN_CORAL,
                DEAD_BUBBLE_CORAL, DEAD_FIRE_CORAL, DEAD_HORN_CORAL, DEAD_TUBE_CORAL});
        }
    }

    //endregion

    //endregion


    //region Misc

    //region Stone Slabs
    private EnumStorage<Material> STONE_SLABS_E;

    @Override
    public EnumStorage<Material> StoneSlabs() {
        StoneSlabsInit();
        return STONE_SLABS_E;
    }

    private void StoneSlabsInit(){
        if (STONE_SLABS_E == null) {
            STONE_SLABS_E = new EnumStorage<>(new Material[]{STONE_SLAB, SMOOTH_STONE_SLAB, ANDESITE_SLAB, POLISHED_ANDESITE_SLAB, DIORITE_SLAB, POLISHED_DIORITE_SLAB, GRANITE_SLAB, POLISHED_GRANITE_SLAB});
        }
    }

    //endregion


    //region Sandstone Slabs
    private EnumStorage<Material> SANDSTONE_SLABS_E;

    @Override
    public EnumStorage<Material> SandstoneSlabs(){
        sandstoneSlabInit();
        return SANDSTONE_SLABS_E;
    }

    private void sandstoneSlabInit() {
        if (SANDSTONE_SLABS_E == null) {
            SANDSTONE_SLABS_E = new EnumStorage<>(new Material[]{SANDSTONE_SLAB,
                SMOOTH_SANDSTONE_SLAB, CUT_SANDSTONE_SLAB, RED_SANDSTONE_SLAB, SMOOTH_RED_SANDSTONE_SLAB, CUT_RED_SANDSTONE_SLAB});
        }
    }

    //endregion


    //region Stone Brick Slabs
    private EnumStorage<Material> STONE_BRICK_SLABS_E;

    @Override
    public EnumStorage<Material> StoneBrickSlabs() {
        StoneBrickSlabsInit();
        return STONE_BRICK_SLABS_E;
    }

    private void StoneBrickSlabsInit(){
        if (STONE_BRICK_SLABS_E == null) {
            STONE_BRICK_SLABS_E = new EnumStorage<>(new Material[]{STONE_BRICK_SLAB, MOSSY_STONE_BRICK_SLAB});
        }
    }
    //endregion


    //region Cobblestone Slabs
    private EnumStorage<Material> COBBLESTONE_SLABS_E;

    @Override
    public EnumStorage<Material> CobblestoneSlabs() {
        CobblestoneSlabsInit();
        return COBBLESTONE_SLABS_E;
    }

    private void CobblestoneSlabsInit(){
        if (COBBLESTONE_SLABS_E == null) {
            COBBLESTONE_SLABS_E = new EnumStorage<>(new Material[]{COBBLESTONE_SLAB, MOSSY_COBBLESTONE_SLAB});
        }
    }
    //endregion


    //region Quartz Slabs
    private EnumStorage<Material> QUARTZ_SLABS_E;

    @Override
    public EnumStorage<Material> QuartzSlabs() {
        QuartzSlabsInit();
        return QUARTZ_SLABS_E;
    }

    private void QuartzSlabsInit(){
        if (QUARTZ_SLABS_E == null) {
            QUARTZ_SLABS_E = new EnumStorage<>(new Material[]{QUARTZ_SLAB, SMOOTH_QUARTZ_SLAB});
        }
    }
    //endregion


    //region Nether Brick Slabs
    private EnumStorage<Material> NETHER_BRICK_SLABS_E;

    @Override
    public EnumStorage<Material> NetherBrickSlabs() {
        NetherBrickSlabsInit();
        return NETHER_BRICK_SLABS_E;
    }

    private void NetherBrickSlabsInit(){
        if (NETHER_BRICK_SLABS_E == null) {
            NETHER_BRICK_SLABS_E = new EnumStorage<>(new Material[]{NETHER_BRICK_SLAB, RED_NETHER_BRICK_SLAB});
        }
    }
    //endregion




    //region Stone Stairs
    private EnumStorage<Material> STONE_STAIRS_E;

    @Override
    public EnumStorage<Material> StoneStairs() {
        StoneStairsInit();
        return STONE_STAIRS_E;
    }

    private void StoneStairsInit(){
        if (STONE_STAIRS_E == null) {
            STONE_STAIRS_E = new EnumStorage<>(new Material[]{STONE_STAIRS, ANDESITE_STAIRS, POLISHED_ANDESITE_STAIRS, DIORITE_STAIRS, POLISHED_DIORITE_STAIRS, GRANITE_STAIRS, POLISHED_GRANITE_STAIRS});
        }
    }
    //endregion


    //region Stone Brick Stairs
    private EnumStorage<Material> STONE_BRICK_STAIRS_E;

    @Override
    public EnumStorage<Material> StoneBrickStairs() {
        StoneBrickStairsInit();
        return STONE_BRICK_STAIRS_E;
    }

    private void StoneBrickStairsInit(){
        if (STONE_BRICK_STAIRS_E == null) {
            STONE_BRICK_STAIRS_E = new EnumStorage<>(new Material[]{STONE_BRICK_STAIRS, MOSSY_STONE_BRICK_STAIRS});
        }
    }
    //endregion


    //region Sandstone Stairs
    private EnumStorage<Material> SANDSTONE_STAIRS_E;

    @Override
    public EnumStorage<Material> SandstoneStairs(){
        SandstoneStairsInit();
        return SANDSTONE_STAIRS_E;
    }

    private void SandstoneStairsInit(){
        if (SANDSTONE_STAIRS_E == null) {
            SANDSTONE_STAIRS_E = new EnumStorage<>(new Material[]{SANDSTONE_STAIRS, SMOOTH_SANDSTONE_STAIRS, RED_SANDSTONE_STAIRS, SMOOTH_RED_SANDSTONE_STAIRS});
        }
    }
    //endregion


    //region Cobblestone Stairs
    private EnumStorage<Material> COBBLESTONE_STAIRS_E;

    @Override
    public EnumStorage<Material> CobblestoneStairs() {
        CobblestoneStairsInit();
        return COBBLESTONE_STAIRS_E;
    }

    private void CobblestoneStairsInit(){
        if (COBBLESTONE_STAIRS_E == null) {
            COBBLESTONE_STAIRS_E = new EnumStorage<>(new Material[]{COBBLESTONE_STAIRS, MOSSY_COBBLESTONE_STAIRS});
        }
    }
    //endregion


    //region Quartz Stairs
    private EnumStorage<Material> QUARTZ_STAIRS_E;

    @Override
    public EnumStorage<Material> QuartzStairs() {
        QuartzStairsInit();
        return QUARTZ_STAIRS_E;
    }

    private void QuartzStairsInit(){
        if (QUARTZ_STAIRS_E == null) {
            QUARTZ_STAIRS_E = new EnumStorage<>(new Material[]{QUARTZ_STAIRS, SMOOTH_QUARTZ_STAIRS});
        }
    }
    //endregion


    //region Nether Brick Stairs
    private EnumStorage<Material> NETHER_BRICK_STAIRS_E;

    @Override
    public EnumStorage<Material> NetherBrickStairs() {
        NetherBrickStairsInit();
        return NETHER_BRICK_STAIRS_E;
    }

    private void NetherBrickStairsInit(){
        if (NETHER_BRICK_STAIRS_E == null) {
            NETHER_BRICK_STAIRS_E = new EnumStorage<>(new Material[]{NETHER_BRICK_STAIRS, RED_NETHER_BRICK_STAIRS});
        }
    }
    //endregion




    //region Stone Walls
    private EnumStorage<Material> STONE_WALLS_E;

    @Override
    public EnumStorage<Material> StoneWalls() {
        StoneWallsInit();
        return STONE_WALLS_E;
    }

    private void StoneWallsInit(){
        if (STONE_WALLS_E == null) {
            STONE_WALLS_E = new EnumStorage<>(new Material[]{ANDESITE_WALL, DIORITE_WALL, GRANITE_WALL});
        }
    }
    //endregion


    //region Stone Brick Walls
    private EnumStorage<Material> STONE_BRICK_WALLS_E;

    @Override
    public EnumStorage<Material> StoneBrickWalls() {
        StoneBrickWallsInit();
        return STONE_BRICK_WALLS_E;
    }

    private void StoneBrickWallsInit(){
        if (STONE_BRICK_WALLS_E == null) {
            STONE_BRICK_WALLS_E = new EnumStorage<>(new Material[]{STONE_BRICK_WALL, MOSSY_STONE_BRICK_WALL});
        }
    }
    //endregion


    //region Sandstone Walls
    private EnumStorage<Material> SANDSTONE_WALLS_E;

    @Override
    public EnumStorage<Material> SandstoneWalls() {
        SandstoneWallsInit();
        return SANDSTONE_WALLS_E;
    }

    private void SandstoneWallsInit(){
        if (SANDSTONE_WALLS_E == null) {
            SANDSTONE_WALLS_E = new EnumStorage<>(new Material[]{SANDSTONE_WALL, RED_SANDSTONE_WALL});
        }
    }
    //endregion


    //region Nether Brick Walls
    private EnumStorage<Material> NETHER_BRICK_WALLS_E;

    @Override
    public EnumStorage<Material> NetherBrickWalls() {
        NetherBrickWallsInit();
        return NETHER_BRICK_WALLS_E;
    }

    private void NetherBrickWallsInit(){
        if (NETHER_BRICK_WALLS_E == null) {
            NETHER_BRICK_WALLS_E = new EnumStorage<>(new Material[]{NETHER_BRICK_WALL, RED_NETHER_BRICK_WALL});
        }
    }
    //endregion

    //endregion


    //region Block Categories

    //region Storage Blocks
    private EnumStorage<Material> STORAGE_BLOCKS_E;

    @Override
    public EnumStorage<Material> StorageBlocks(){
        storageBlocksInit();
        return STORAGE_BLOCKS_E;
    }

    private void storageBlocksInit() {
        if (STORAGE_BLOCKS_E == null) {
            STORAGE_BLOCKS_E = new EnumStorage<>(new Material[]{DISPENSER, SPAWNER,
                CHEST, FURNACE, JUKEBOX, ENDER_CHEST, BEACON, TRAPPED_CHEST, HOPPER, DROPPER, BREWING_STAND, ANVIL,
                BARREL,
                BLAST_FURNACE, LECTERN, SMOKER},
                ShulkerBoxes(), CommandBlocks());
        }
    }

    //endregion


    //region Interactable Blocks
    private EnumStorage<Material> INTERACTABLE_BLOCKS_E;

    @Override
    public EnumStorage<Material> InteractableBlocks(){
        interactableBlocksInit();
        return INTERACTABLE_BLOCKS_E;
    }

    private void interactableBlocksInit() {
        if (INTERACTABLE_BLOCKS_E == null) {
            INTERACTABLE_BLOCKS_E = new EnumStorage<>(new Material[]{
                NOTE_BLOCK, CRAFTING_TABLE, LEVER, REPEATER, ENCHANTING_TABLE, COMPARATOR, DAYLIGHT_DETECTOR, OBSERVER, BELL,
                CARTOGRAPHY_TABLE, COMPOSTER, FLETCHING_TABLE, GRINDSTONE, LOOM, SMITHING_TABLE, STONECUTTER},
                Beds(), Doors(), Trapdoors(), FenceGates(), CommandBlocks(), Buttons(), ShulkerBoxes(), StorageBlocks());
        }
    }

    //endregion


    //region Unbreakable Blocks
    private EnumStorage<Material> UNBREAKABLE_BLOCKS_E;


    @Override
    public EnumStorage<Material> UnbreakableBlocks(){
        unbreakableBlocks();
        return UNBREAKABLE_BLOCKS_E;
    }

    private void unbreakableBlocks() {
        if (UNBREAKABLE_BLOCKS_E == null) {
            UNBREAKABLE_BLOCKS_E = new EnumStorage<>(new Material[]{BARRIER, BEDROCK,
                BUBBLE_COLUMN, DRAGON_BREATH, DRAGON_EGG, END_CRYSTAL, END_GATEWAY, END_PORTAL, END_PORTAL_FRAME, LAVA,
                STRUCTURE_VOID, STRUCTURE_BLOCK, WATER, PISTON_HEAD, MOVING_PISTON, JIGSAW}, Airs(), CommandBlocks());
        }
    }

    //endregion

    //endregion


    //region Enchantment Enum Storage

    //region Lumber Whitelist
    private EnumStorage<Material> LUMBER_WHITELIST_E;

    @Override
    public EnumStorage<Material> LumberWhitelist(){
        LumberWhitelistInit();
        return LUMBER_WHITELIST_E;
    }

    private void LumberWhitelistInit() {
        if (LUMBER_WHITELIST_E == null) {
            LUMBER_WHITELIST_E = new EnumStorage<>(new Material[]{
                DIRT, GRASS, VINE, SNOW, COCOA, GRAVEL, STONE, DIORITE, GRANITE, ANDESITE, WATER, LAVA, SAND, BROWN_MUSHROOM,
                RED_MUSHROOM, MOSSY_COBBLESTONE, CLAY, BROWN_MUSHROOM, RED_MUSHROOM, MYCELIUM, TORCH, SUGAR_CANE, GRASS_BLOCK,
                PODZOL, FERN, GRASS, MELON, PUMPKIN, SWEET_BERRY_BUSH, BAMBOO, BAMBOO_SAPLING}, TrunkBlocks(), Leaves(),
                SmallFlowers(), LargeFlowers(), Saplings(), Airs(), DeadlyPlants());
        }
    }
    //endregion


    //region Transformation Entity Types
    private EnumStorage<EntityType> TRANSFORMATION_ENTITY_TYPES_FROM_E;

    @Override
    public EnumStorage<EntityType> TransformationEntityTypesFrom(){
        TransformationEntityTypesFromInit();
        return TRANSFORMATION_ENTITY_TYPES_FROM_E;
    }

    private void TransformationEntityTypesFromInit(){
        if (TRANSFORMATION_ENTITY_TYPES_FROM_E == null) {
            TRANSFORMATION_ENTITY_TYPES_FROM_E = new EnumStorage<>(new EntityType[]{
                HUSK, WITCH, EntityType.COD, VILLAGER, SKELETON, HORSE, FOX, EntityType.CHICKEN, SQUID, POLAR_BEAR,
                PHANTOM, COW, PIG, SPIDER, CAT, SLIME, GUARDIAN, ENDERMITE, SKELETON_HORSE, EntityType.RABBIT,
                TRADER_LLAMA, SHULKER, SNOWMAN, RAVAGER, DROWNED, VINDICATOR, EntityType.SALMON, PILLAGER,
                WITHER_SKELETON, DONKEY, PARROT, DOLPHIN, PANDA, VEX, MUSHROOM_COW, PIG_ZOMBIE, CAVE_SPIDER,
                OCELOT, MAGMA_CUBE, ELDER_GUARDIAN, SILVERFISH, ZOMBIE_HORSE, EntityType.RABBIT, LLAMA, ENDERMAN,
                IRON_GOLEM, GHAST, ZOMBIE, EVOKER, PUFFERFISH, WANDERING_TRADER, STRAY, MULE, WOLF, BAT, TURTLE, SHEEP,
                BLAZE, ZOMBIE_VILLAGER, ILLUSIONER, EntityType.TROPICAL_FISH, CREEPER});
        }
    }



    private EnumStorage<EntityType> TRANSFORMATION_ENTITY_TYPES_TO_E;

    @Override
    public EnumStorage<EntityType> TransformationEntityTypesTo(){
        TransformationEntityTypesToInit();
        return TRANSFORMATION_ENTITY_TYPES_TO_E;
    }

    private void TransformationEntityTypesToInit(){
        if (TRANSFORMATION_ENTITY_TYPES_TO_E == null) {
            TRANSFORMATION_ENTITY_TYPES_TO_E = new EnumStorage<>(new EntityType[]{
                DROWNED, VINDICATOR, EntityType.SALMON, PILLAGER, WITHER_SKELETON, DONKEY, WOLF, PARROT, DOLPHIN, PANDA,
                VEX, MUSHROOM_COW, PIG_ZOMBIE, CAVE_SPIDER, OCELOT, MAGMA_CUBE, ELDER_GUARDIAN, SILVERFISH,
                ZOMBIE_HORSE, EntityType.RABBIT, LLAMA, ENDERMAN, IRON_GOLEM, GHAST, ZOMBIE, EVOKER, PUFFERFISH,
                WANDERING_TRADER, STRAY, MULE, BAT, TURTLE, SHEEP, BLAZE, COW, PIG, SPIDER, CAT, SLIME, GUARDIAN,
                ENDERMITE, SKELETON_HORSE, EntityType.RABBIT, TRADER_LLAMA, SHULKER, SNOWMAN, RAVAGER, ZOMBIE_VILLAGER,
                ILLUSIONER, EntityType.TROPICAL_FISH, VILLAGER, SKELETON, HORSE, FOX, EntityType.CHICKEN, SQUID,
                POLAR_BEAR, PHANTOM, HUSK, WITCH, EntityType.COD, CREEPER});
        }
    }

    @Override
    public LivingEntity TransformationCycle(LivingEntity ent, Random rnd) {
        int newTypeID = TransformationEntityTypesFrom().indexOf(ent.getType());
        if (newTypeID == -1) {
            return null;
        }
        EntityType newType = TransformationEntityTypesTo().get(newTypeID);
        LivingEntity newEnt = (LivingEntity) ent.getWorld().spawnEntity(ent.getLocation(), newType);

        switch (newType) {
            case HORSE:
                ((Horse) newEnt).setColor(Horse.Color.values()[rnd.nextInt(Horse.Color.values().length)]);
                ((Horse) newEnt).setStyle(Horse.Style.values()[rnd.nextInt(Horse.Style.values().length)]);
                break;
            case RABBIT:
                if (((Rabbit) ent).getRabbitType().equals(Rabbit.Type.THE_KILLER_BUNNY)) {
                    ((Rabbit) newEnt).setRabbitType(Rabbit.Type.values()[rnd.nextInt(Rabbit.Type.values().length - 1)]);
                } else {
                    ((Rabbit) newEnt).setRabbitType(Rabbit.Type.THE_KILLER_BUNNY);
                }
                break;
            case VILLAGER:
                ((Villager) newEnt).setProfession(
                    Villager.Profession.values()[rnd.nextInt(Villager.Profession.values().length)]);
                ((Villager) newEnt).setVillagerType(Villager.Type.values()[rnd.nextInt(Villager.Type.values().length)]);
                break;
            case LLAMA:
                ((Llama) newEnt).setColor(Llama.Color.values()[rnd.nextInt(Llama.Color.values().length)]);
                break;
            case TROPICAL_FISH:
                ((TropicalFish) newEnt).setBodyColor(DyeColor.values()[rnd.nextInt(DyeColor.values().length)]);
                ((TropicalFish) newEnt).setPatternColor(DyeColor.values()[rnd.nextInt(DyeColor.values().length)]);
                ((TropicalFish) newEnt).setPattern(
                    TropicalFish.Pattern.values()[rnd.nextInt(TropicalFish.Pattern.values().length)]);
                break;
            case PARROT:
                ((Parrot) newEnt).setVariant(Parrot.Variant.values()[rnd.nextInt(Parrot.Variant.values().length)]);
                break;
            case CAT:
                ((Cat) newEnt).setCatType(Cat.Type.values()[rnd.nextInt(Cat.Type.values().length)]);
                break;
            case SHEEP:
                ((Sheep) newEnt).setColor(DyeColor.values()[rnd.nextInt(DyeColor.values().length)]);
                break;
            case CREEPER:
                ((Creeper) newEnt).setPowered(!((Creeper) ent).isPowered());
                break;
            case MUSHROOM_COW:
                ((MushroomCow) newEnt).setVariant(
                    MushroomCow.Variant.values()[rnd.nextInt(MushroomCow.Variant.values().length)]);
                break;
            case FOX:
                ((Fox) newEnt).setFoxType(Fox.Type.values()[rnd.nextInt(Fox.Type.values().length)]);
                break;
            case ILLUSIONER:
                ((Panda) newEnt).setHiddenGene(Panda.Gene.values()[rnd.nextInt(Panda.Gene.values().length)]);
                ((Panda) newEnt).setMainGene(Panda.Gene.values()[rnd.nextInt(Panda.Gene.values().length)]);
                break;
        }

        newEnt.setCustomName(ent.getCustomName());
        newEnt.setCustomNameVisible(ent.isCustomNameVisible());
        return ent;
    }

    //endregion


    //region Fire Raw
    private EnumStorage<Material> FIRE_RAW_E;

    public EnumStorage<Material> FireRaw(){
        fireRawInit();
        return FIRE_RAW_E;
    }

    private void fireRawInit() {
        if (FIRE_RAW_E == null) {
            FIRE_RAW_E = new EnumStorage<>(new Material[]{DIORITE, ANDESITE, GRANITE,
                IRON_ORE, GOLD_ORE, COBBLESTONE, MOSSY_COBBLESTONE, NETHERRACK, STONE_BRICKS, QUARTZ_BLOCK, SANDSTONE,
                RED_SANDSTONE, STONE}, Terracottas());
        }
    }

    //endregion


    //region Fire Cooked
    private EnumStorage<Material> FIRE_COOKED_E;

    public EnumStorage<Material> FireCooked(){
        fireCookedInit();
        return FIRE_COOKED_E;
    }

    private void fireCookedInit() {
        if (FIRE_COOKED_E == null) {
            FIRE_COOKED_E = new EnumStorage<>(new Material[]{POLISHED_DIORITE,
                POLISHED_ANDESITE, POLISHED_GRANITE, IRON_INGOT, GOLD_INGOT, STONE, MOSSY_STONE_BRICKS, NETHER_BRICK,
                CRACKED_STONE_BRICKS, SMOOTH_QUARTZ, SMOOTH_SANDSTONE, SMOOTH_RED_SANDSTONE, SMOOTH_STONE}, GlazedTerracottas());
        }
    }

    //endregion

    //endregion


    //endregion


    //region Gluttony

    //region Gluttony Food Levels
    private int[] GLUTTONY_FOOD_LEVELS;

    @Override
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

    @Override
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

    @Override
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
        return ep.playerInteractManager.breakBlock(new BlockPosition(block.getX(), block.getY(), block.getZ()));
    }
    
    @Override
    public void collectXP(Player player, int amount) {
        EntityExperienceOrb eOrb = new EntityExperienceOrb(((CraftWorld)player.getWorld()).getHandle(), 
                player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(), amount);
        EntityHuman ePlayer = ((CraftPlayer) player).getHandle();
        eOrb.pickup(ePlayer); // XP Orb Entity handles mending. Don't blame me, I didn't code it
        ePlayer.bF = 0; // Reset XP Pickup Timer
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
        public void a(PacketDataSerializer pds) {
            pds.writeByte(0); // Set Metadata at index 0
            pds.writeByte(0); // Value is type Byte
            pds.writeByte(0x60); // Set Glowing and Invisible bits
            pds.writeByte(0xFF); // Index -1 indicates end of Metadata
        }
    }
}