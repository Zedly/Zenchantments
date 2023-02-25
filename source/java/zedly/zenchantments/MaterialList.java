package zedly.zenchantments;

import net.minecraft.util.Tuple;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.AbstractList;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

import static org.bukkit.Material.*;

public final class MaterialList extends AbstractList<Material> {
    public static final MaterialList EMPTY = new MaterialList(new Material[0]);

    public static final MaterialList BEDS = new MaterialList(
        WHITE_BED,
        ORANGE_BED,
        MAGENTA_BED,
        LIGHT_BLUE_BED,
        YELLOW_BED,
        LIME_BED,
        PINK_BED,
        GRAY_BED,
        LIGHT_GRAY_BED,
        CYAN_BED,
        PURPLE_BED,
        BLUE_BED,
        BROWN_BED,
        GREEN_BED,
        RED_BED,
        BLACK_BED
    );

    public static final MaterialList WOOL = new MaterialList(
        WHITE_WOOL,
        ORANGE_WOOL,
        MAGENTA_WOOL,
        LIGHT_BLUE_WOOL,
        YELLOW_WOOL,
        LIME_WOOL,
        PINK_WOOL,
        GRAY_WOOL,
        LIGHT_GRAY_WOOL,
        CYAN_WOOL,
        PURPLE_WOOL,
        BLUE_WOOL,
        BROWN_WOOL,
        GREEN_WOOL,
        RED_WOOL,
        BLACK_WOOL
    );

    public static final MaterialList SHULKER_BOXES = new MaterialList(
        WHITE_SHULKER_BOX,
        ORANGE_SHULKER_BOX,
        MAGENTA_SHULKER_BOX,
        LIGHT_BLUE_SHULKER_BOX,
        YELLOW_SHULKER_BOX,
        LIME_SHULKER_BOX,
        PINK_SHULKER_BOX,
        GRAY_SHULKER_BOX,
        LIGHT_GRAY_SHULKER_BOX,
        CYAN_SHULKER_BOX,
        PURPLE_SHULKER_BOX,
        BLUE_SHULKER_BOX,
        BROWN_SHULKER_BOX,
        GREEN_SHULKER_BOX,
        RED_SHULKER_BOX,
        BLACK_SHULKER_BOX,
        SHULKER_BOX
    );

    public static final MaterialList CONCRETE_POWDER = new MaterialList(
        WHITE_CONCRETE_POWDER,
        ORANGE_CONCRETE_POWDER,
        MAGENTA_CONCRETE_POWDER,
        LIGHT_BLUE_CONCRETE_POWDER,
        YELLOW_CONCRETE_POWDER,
        LIME_CONCRETE_POWDER,
        PINK_CONCRETE_POWDER,
        GRAY_CONCRETE_POWDER,
        LIGHT_GRAY_CONCRETE_POWDER,
        CYAN_CONCRETE_POWDER,
        PURPLE_CONCRETE_POWDER,
        BLUE_CONCRETE_POWDER,
        BROWN_CONCRETE_POWDER,
        GREEN_CONCRETE_POWDER,
        RED_CONCRETE_POWDER,
        BLACK_CONCRETE_POWDER
    );

    public static final MaterialList CONCRETE = new MaterialList(
        WHITE_CONCRETE,
        ORANGE_CONCRETE,
        MAGENTA_CONCRETE,
        LIGHT_BLUE_CONCRETE,
        YELLOW_CONCRETE,
        LIME_CONCRETE,
        PINK_CONCRETE,
        GRAY_CONCRETE,
        LIGHT_GRAY_CONCRETE,
        CYAN_CONCRETE,
        PURPLE_CONCRETE,
        BLUE_CONCRETE,
        BROWN_CONCRETE,
        GREEN_CONCRETE,
        RED_CONCRETE,
        BLACK_CONCRETE
    );

    public static final MaterialList GLAZED_TERRACOTTA = new MaterialList(
        WHITE_GLAZED_TERRACOTTA,
        ORANGE_GLAZED_TERRACOTTA,
        MAGENTA_GLAZED_TERRACOTTA,
        LIGHT_BLUE_GLAZED_TERRACOTTA,
        YELLOW_GLAZED_TERRACOTTA,
        LIME_GLAZED_TERRACOTTA,
        PINK_GLAZED_TERRACOTTA,
        GRAY_GLAZED_TERRACOTTA,
        LIGHT_GRAY_GLAZED_TERRACOTTA,
        CYAN_GLAZED_TERRACOTTA,
        PURPLE_GLAZED_TERRACOTTA,
        BLUE_GLAZED_TERRACOTTA,
        BROWN_GLAZED_TERRACOTTA,
        GREEN_GLAZED_TERRACOTTA,
        RED_GLAZED_TERRACOTTA,
        BLACK_GLAZED_TERRACOTTA
    );

    public static final MaterialList TERRACOTTA = new MaterialList(
        WHITE_TERRACOTTA,
        ORANGE_TERRACOTTA,
        MAGENTA_TERRACOTTA,
        LIGHT_BLUE_TERRACOTTA,
        YELLOW_TERRACOTTA,
        LIME_TERRACOTTA,
        PINK_TERRACOTTA,
        GRAY_TERRACOTTA,
        LIGHT_GRAY_TERRACOTTA,
        CYAN_TERRACOTTA,
        PURPLE_TERRACOTTA,
        BLUE_TERRACOTTA,
        BROWN_TERRACOTTA,
        GREEN_TERRACOTTA,
        RED_TERRACOTTA,
        BLACK_TERRACOTTA
    );

    public static final MaterialList CARPETS = new MaterialList(
        WHITE_CARPET,
        ORANGE_CARPET,
        MAGENTA_CARPET,
        LIGHT_BLUE_CARPET,
        YELLOW_CARPET,
        LIME_CARPET,
        PINK_CARPET,
        GRAY_CARPET,
        LIGHT_GRAY_CARPET,
        CYAN_CARPET,
        PURPLE_CARPET,
        BLUE_CARPET,
        BROWN_CARPET,
        GREEN_CARPET,
        RED_CARPET,
        BLACK_CARPET,
        MOSS_CARPET
    );

    public static final MaterialList STAINED_GLASS = new MaterialList(
        WHITE_STAINED_GLASS,
        ORANGE_STAINED_GLASS,
        MAGENTA_STAINED_GLASS,
        LIGHT_BLUE_STAINED_GLASS,
        YELLOW_STAINED_GLASS,
        LIME_STAINED_GLASS,
        PINK_STAINED_GLASS,
        GRAY_STAINED_GLASS,
        LIGHT_GRAY_STAINED_GLASS,
        CYAN_STAINED_GLASS,
        PURPLE_STAINED_GLASS,
        BLUE_STAINED_GLASS,
        BROWN_STAINED_GLASS,
        GREEN_STAINED_GLASS,
        RED_STAINED_GLASS,
        BLACK_STAINED_GLASS
    );

    public static final MaterialList STAINED_GLASS_PANES = new MaterialList(
        WHITE_STAINED_GLASS_PANE,
        ORANGE_STAINED_GLASS_PANE,
        MAGENTA_STAINED_GLASS_PANE,
        LIGHT_BLUE_STAINED_GLASS_PANE,
        YELLOW_STAINED_GLASS_PANE,
        LIME_STAINED_GLASS_PANE,
        PINK_STAINED_GLASS_PANE,
        GRAY_STAINED_GLASS_PANE,
        LIGHT_GRAY_STAINED_GLASS_PANE,
        CYAN_STAINED_GLASS_PANE,
        PURPLE_STAINED_GLASS_PANE,
        BLUE_STAINED_GLASS_PANE,
        BROWN_STAINED_GLASS_PANE,
        GREEN_STAINED_GLASS_PANE,
        RED_STAINED_GLASS_PANE,
        BLACK_STAINED_GLASS_PANE
    );

    public static final MaterialList DYES = new MaterialList(
        BONE_MEAL,
        ORANGE_DYE,
        MAGENTA_DYE,
        LIGHT_BLUE_DYE,
        YELLOW_DYE,
        LIME_DYE,
        PINK_DYE,
        GRAY_DYE,
        LIGHT_GRAY_DYE,
        CYAN_DYE,
        PURPLE_DYE,
        LAPIS_LAZULI,
        COCOA_BEANS,
        GREEN_DYE,
        RED_DYE,
        INK_SAC
    );

    public static final MaterialList WOODEN_BUTTONS = new MaterialList(
        OAK_BUTTON,
        BIRCH_BUTTON,
        SPRUCE_BUTTON,
        ACACIA_BUTTON,
        DARK_OAK_BUTTON,
        JUNGLE_BUTTON,
        CRIMSON_BUTTON,
        WARPED_BUTTON,
        MANGROVE_BUTTON
    );

    public static final MaterialList WOODEN_DOORS = new MaterialList(
        OAK_DOOR,
        BIRCH_DOOR,
        SPRUCE_DOOR,
        ACACIA_DOOR,
        DARK_OAK_DOOR,
        JUNGLE_DOOR,
        CRIMSON_DOOR,
        WARPED_DOOR,
        MANGROVE_DOOR
    );

    public static final MaterialList WOODEN_FENCES = new MaterialList(
        OAK_FENCE,
        BIRCH_FENCE,
        SPRUCE_FENCE,
        ACACIA_FENCE,
        DARK_OAK_FENCE,
        JUNGLE_FENCE,
        CRIMSON_FENCE,
        WARPED_FENCE,
        MANGROVE_FENCE
    );

    public static final MaterialList FENCE_GATES = new MaterialList(
        OAK_FENCE_GATE,
        BIRCH_FENCE_GATE,
        SPRUCE_FENCE_GATE,
        ACACIA_FENCE_GATE,
        DARK_OAK_FENCE_GATE,
        JUNGLE_FENCE_GATE,
        CRIMSON_FENCE_GATE,
        WARPED_FENCE_GATE,
        MANGROVE_FENCE_GATE
    );

    public static final MaterialList LEAVES = new MaterialList(
        OAK_LEAVES,
        BIRCH_LEAVES,
        SPRUCE_LEAVES,
        ACACIA_LEAVES,
        DARK_OAK_LEAVES,
        JUNGLE_LEAVES,
        AZALEA_LEAVES,
        FLOWERING_AZALEA_LEAVES,
        MANGROVE_LEAVES
    );

    public static final MaterialList LOGS = new MaterialList(
        OAK_LOG,
        BIRCH_LOG,
        SPRUCE_LOG,
        ACACIA_LOG,
        DARK_OAK_LOG,
        JUNGLE_LOG,
        CRIMSON_STEM,
        WARPED_STEM,
        CRIMSON_HYPHAE,
        WARPED_HYPHAE,
        MANGROVE_LOG
    );

    public static final MaterialList WOODEN_PLANKS = new MaterialList(
        OAK_PLANKS,
        BIRCH_PLANKS,
        SPRUCE_PLANKS,
        ACACIA_PLANKS,
        DARK_OAK_PLANKS,
        JUNGLE_PLANKS,
        CRIMSON_PLANKS,
        WARPED_PLANKS,
        MANGROVE_PLANKS
    );

    public static final MaterialList WOODEN_PRESSURE_PLATES = new MaterialList(
        OAK_PRESSURE_PLATE,
        BIRCH_PRESSURE_PLATE,
        SPRUCE_PRESSURE_PLATE,
        ACACIA_PRESSURE_PLATE,
        DARK_OAK_PRESSURE_PLATE,
        JUNGLE_PRESSURE_PLATE,
        CRIMSON_PRESSURE_PLATE,
        WARPED_PRESSURE_PLATE,
        MANGROVE_PRESSURE_PLATE
    );

    public static final MaterialList SAPLINGS = new MaterialList(
        OAK_SAPLING,
        BIRCH_SAPLING,
        SPRUCE_SAPLING,
        ACACIA_SAPLING,
        DARK_OAK_SAPLING,
        JUNGLE_SAPLING,
        AZALEA,
        FLOWERING_AZALEA,
        MANGROVE_PROPAGULE
    );

    public static final MaterialList WOODEN_SLABS = new MaterialList(
        OAK_SLAB,
        BIRCH_SLAB,
        SPRUCE_SLAB,
        ACACIA_SLAB,
        DARK_OAK_SLAB,
        JUNGLE_SLAB,
        CRIMSON_SLAB,
        WARPED_SLAB,
        MANGROVE_SLAB
    );

    public static final MaterialList WOODEN_STAIRS = new MaterialList(
        OAK_STAIRS,
        BIRCH_STAIRS,
        SPRUCE_STAIRS,
        ACACIA_STAIRS,
        DARK_OAK_STAIRS,
        JUNGLE_STAIRS,
        CRIMSON_STAIRS,
        WARPED_STAIRS,
        MANGROVE_STAIRS
    );

    public static final MaterialList WOODEN_TRAPDOORS = new MaterialList(
        OAK_TRAPDOOR,
        BIRCH_TRAPDOOR,
        SPRUCE_TRAPDOOR,
        ACACIA_TRAPDOOR,
        DARK_OAK_TRAPDOOR,
        JUNGLE_TRAPDOOR,
        CRIMSON_TRAPDOOR,
        WARPED_TRAPDOOR,
        MANGROVE_TRAPDOOR
    );

    public static final MaterialList WOOD = new MaterialList(
        OAK_WOOD,
        BIRCH_WOOD,
        SPRUCE_WOOD,
        ACACIA_WOOD,
        DARK_OAK_WOOD,
        JUNGLE_WOOD,
        CRIMSON_HYPHAE,
        WARPED_HYPHAE,
        MANGROVE_WOOD
    );

    public static final MaterialList STRIPPED_LOGS = new MaterialList(
        STRIPPED_OAK_LOG,
        STRIPPED_BIRCH_LOG,
        STRIPPED_SPRUCE_LOG,
        STRIPPED_ACACIA_LOG,
        STRIPPED_DARK_OAK_LOG,
        STRIPPED_JUNGLE_LOG,
        STRIPPED_CRIMSON_STEM,
        STRIPPED_WARPED_STEM,
        STRIPPED_MANGROVE_LOG
    );

    public static final MaterialList STRIPPED_WOOD = new MaterialList(
        STRIPPED_OAK_WOOD,
        STRIPPED_BIRCH_WOOD,
        STRIPPED_SPRUCE_WOOD,
        STRIPPED_ACACIA_WOOD,
        STRIPPED_DARK_OAK_WOOD,
        STRIPPED_JUNGLE_WOOD,
        STRIPPED_CRIMSON_HYPHAE,
        STRIPPED_WARPED_HYPHAE,
        STRIPPED_MANGROVE_WOOD
    );

    public static final MaterialList DEADLY_PLANTS = new MaterialList(WITHER_ROSE);

    public static final MaterialList SMALL_FLOWERS = new MaterialList(
        DANDELION,
        POPPY,
        BLUE_ORCHID,
        ALLIUM,
        AZURE_BLUET,
        RED_TULIP,
        ORANGE_TULIP,
        WHITE_TULIP,
        PINK_TULIP,
        OXEYE_DAISY,
        LILY_OF_THE_VALLEY
    );

    public static final MaterialList LARGE_FLOWERS = new MaterialList(
        SUNFLOWER,
        LILAC,
        TALL_GRASS,
        LARGE_FERN,
        ROSE_BUSH,
        PEONY
    );

    public static final MaterialList GROWN_CROPS = new MaterialList(
        WHEAT,
        POTATOES,
        CARROTS,
        COCOA,
        BEETROOTS,
        NETHER_WART,
        SWEET_BERRY_BUSH
    );

    public static final MaterialList GROWN_CROP_BLOCKS = new MaterialList(MELON, PUMPKIN);

    public static final MaterialList CORAL_BLOCKS = new MaterialList(
        BRAIN_CORAL_BLOCK,
        BUBBLE_CORAL_BLOCK,
        FIRE_CORAL_BLOCK,
        HORN_CORAL_BLOCK,
        TUBE_CORAL_BLOCK
    );

    public static final MaterialList DEAD_CORAL_BLOCKS = new MaterialList(
        DEAD_BRAIN_CORAL_BLOCK,
        DEAD_BUBBLE_CORAL_BLOCK,
        DEAD_FIRE_CORAL_BLOCK,
        DEAD_HORN_CORAL_BLOCK,
        DEAD_TUBE_CORAL_BLOCK
    );

    public static final MaterialList CORAL = new MaterialList(
        BRAIN_CORAL,
        BUBBLE_CORAL,
        FIRE_CORAL,
        HORN_CORAL,
        TUBE_CORAL
    );

    public static final MaterialList CORAL_FANS = new MaterialList(
        BRAIN_CORAL_FAN,
        BUBBLE_CORAL_FAN,
        FIRE_CORAL_FAN,
        HORN_CORAL_FAN,
        TUBE_CORAL_FAN
    );

    public static final MaterialList DEAD_CORAL_FANS = new MaterialList(
        DEAD_BRAIN_CORAL_FAN,
        DEAD_BUBBLE_CORAL_FAN,
        DEAD_FIRE_CORAL_FAN,
        DEAD_HORN_CORAL_FAN,
        DEAD_TUBE_CORAL_FAN
    );

    public static final MaterialList DEAD_CORAL = EMPTY;

    public static final MaterialList DEAD_CORAL_WALL_FANS = new MaterialList(
        DEAD_BRAIN_CORAL_WALL_FAN,
        DEAD_BUBBLE_CORAL_WALL_FAN,
        DEAD_FIRE_CORAL_WALL_FAN,
        DEAD_HORN_CORAL_WALL_FAN,
        DEAD_TUBE_CORAL_WALL_FAN
    );

    public static final MaterialList MUSHROOMS = new MaterialList(
        RED_MUSHROOM,
        BROWN_MUSHROOM,
        CRIMSON_FUNGUS,
        WARPED_FUNGUS
    );

    public static final MaterialList MUSHROOM_BLOCKS = new MaterialList(
        BROWN_MUSHROOM_BLOCK,
        MUSHROOM_STEM,
        RED_MUSHROOM_BLOCK
    );

    public static final MaterialList CHORUS_PLANTS = new MaterialList(CHORUS_PLANT, CHORUS_FLOWER);

    public static final MaterialList SHORT_GRASS = new MaterialList(GRASS, DEAD_BUSH, FERN);

    public static final MaterialList BUTTONS = new MaterialList(new Material[] { STONE_BUTTON }, WOODEN_BUTTONS);

    public static final MaterialList DOORS = new MaterialList(new Material[] { IRON_DOOR }, WOODEN_DOORS);

    public static final MaterialList TRAPDOORS = new MaterialList(new Material[] { IRON_TRAPDOOR }, WOODEN_TRAPDOORS);

    public static final MaterialList TRUNKS = new MaterialList(
        new Material[]{
            MUSHROOM_STEM,
            BROWN_MUSHROOM_BLOCK,
            RED_MUSHROOM_BLOCK,
            Material.CACTUS
        },
        CHORUS_PLANTS,
        LOGS,
        WOOD,
        STRIPPED_WOOD,
        STRIPPED_LOGS
    );

    public static final MaterialList PRESSURE_PLATES = new MaterialList(
        new Material[] {
            STONE_PRESSURE_PLATE,
            LIGHT_WEIGHTED_PRESSURE_PLATE,
            HEAVY_WEIGHTED_PRESSURE_PLATE
        },
        WOODEN_PRESSURE_PLATES
    );

    public static final MaterialList AIR = new MaterialList(Material.AIR, CAVE_AIR, VOID_AIR);

    public static final MaterialList COMMAND_BLOCKS = new MaterialList(
        COMMAND_BLOCK,
        CHAIN_COMMAND_BLOCK,
        REPEATING_COMMAND_BLOCK
    );

    public static final MaterialList DEEPSLATE_ORES = new MaterialList(
        DEEPSLATE_COAL_ORE,
        DEEPSLATE_REDSTONE_ORE,
        DEEPSLATE_IRON_ORE,
        DEEPSLATE_GOLD_ORE,
        DEEPSLATE_COPPER_ORE,
        DEEPSLATE_DIAMOND_ORE,
        DEEPSLATE_LAPIS_ORE
    );

    public static final MaterialList NETHER_ORES = new MaterialList(
        GLOWSTONE,
        NETHER_QUARTZ_ORE,
        NETHER_GOLD_ORE,
        ANCIENT_DEBRIS
    );

    public static final MaterialList ORES = new MaterialList(
        new Material[]{COAL_ORE,
            REDSTONE_ORE,
            DIAMOND_ORE,
            GOLD_ORE,
            IRON_ORE,
            COPPER_ORE,
            LAPIS_ORE,
            EMERALD_ORE},
        DEEPSLATE_ORES,
        NETHER_ORES
    );

    public static final MaterialList SANDS = new MaterialList(Material.SAND, RED_SAND);

    public static final MaterialList ICE = new MaterialList(Material.ICE, BLUE_ICE, PACKED_ICE);

    public static final MaterialList DIRT = new MaterialList(
        Material.DIRT,
        COARSE_DIRT,
        ROOTED_DIRT,
        MYCELIUM,
        PODZOL,
        GRASS_BLOCK,
        DIRT_PATH,
        CRIMSON_NYLIUM,
        WARPED_NYLIUM
    );

    public static final MaterialList STONES = new MaterialList(Material.STONE, GRANITE, ANDESITE, DIORITE, BASALT, TUFF);

    public static final MaterialList COBBLESTONES = new MaterialList(COBBLESTONE, MOSSY_COBBLESTONE, COBBLED_DEEPSLATE);

    public static final MaterialList NETHER_BRICKS = new MaterialList(Material.NETHER_BRICKS, RED_NETHER_BRICKS);

    public static final MaterialList STONE_BRICKS = new MaterialList(
        Material.STONE_BRICKS,
        CRACKED_STONE_BRICKS,
        MOSSY_STONE_BRICKS,
        CHISELED_STONE_BRICKS
    );

    public static final MaterialList QUARTZ_BLOCKS = new MaterialList(
        QUARTZ_BLOCK,
        CHISELED_QUARTZ_BLOCK,
        QUARTZ_PILLAR
    );

    public static final MaterialList POLISHED_STONES = new MaterialList(
        POLISHED_ANDESITE,
        POLISHED_DIORITE,
        POLISHED_GRANITE
    );

    public static final MaterialList PRISMARINE = new MaterialList(
        Material.PRISMARINE,
        PRISMARINE_BRICKS,
        DARK_PRISMARINE
    );

    public static final MaterialList END_STONES = new MaterialList(END_STONE, END_STONE_BRICKS);

    public static final MaterialList PURPUR = new MaterialList(PURPUR_BLOCK, PURPUR_PILLAR);

    public static final MaterialList SANDSTONE = new MaterialList(
        Material.SANDSTONE,
        CUT_SANDSTONE,
        CHISELED_SANDSTONE,
        SMOOTH_SANDSTONE,
        RED_SANDSTONE,
        CUT_RED_SANDSTONE,
        CHISELED_RED_SANDSTONE,
        SMOOTH_RED_SANDSTONE
    );

    public static final MaterialList STONE_SLABS = new MaterialList(STONE_SLAB);

    public static final MaterialList SANDSTONE_SLABS = new MaterialList(SANDSTONE_SLAB, RED_SANDSTONE_SLAB);

    public static final MaterialList STONE_BRICK_SLABS = new MaterialList(STONE_BRICK_SLAB);

    public static final MaterialList COBBLESTONE_SLABS = new MaterialList(COBBLESTONE_SLAB);

    public static final MaterialList QUARTZ_SLABS = new MaterialList(QUARTZ_SLAB);

    public static final MaterialList NETHER_BRICK_SLABS = new MaterialList(NETHER_BRICK_SLAB);

    public static final MaterialList PRISMARINE_SLABS = new MaterialList(
        PRISMARINE_SLAB,
        PRISMARINE_BRICK_SLAB,
        DARK_PRISMARINE_SLAB
    );

    public static final MaterialList STONE_STAIRS = EMPTY;

    public static final MaterialList SANDSTONE_STAIRS = new MaterialList(
        Material.SANDSTONE_STAIRS,
        RED_SANDSTONE_STAIRS
    );

    public static final MaterialList STONE_BRICK_STAIRS = new MaterialList(Material.STONE_BRICK_STAIRS);

    public static final MaterialList COBBLESTONE_STAIRS = new MaterialList(Material.COBBLESTONE_STAIRS);

    public static final MaterialList QUARTZ_STAIRS = new MaterialList(Material.QUARTZ_STAIRS);

    public static final MaterialList NETHER_BRICK_STAIRS = new MaterialList(Material.NETHER_BRICK_STAIRS);

    public static final MaterialList PRISMARINE_STAIRS = new MaterialList(
        Material.PRISMARINE_STAIRS,
        PRISMARINE_BRICK_STAIRS,
        DARK_PRISMARINE_STAIRS
    );

    public static final MaterialList STONE_WALLS = EMPTY;

    public static final MaterialList STONE_BRICK_WALLS = EMPTY;

    public static final MaterialList COBBLESTONE_WALLS = new MaterialList(
        COBBLESTONE_WALL,
        MOSSY_COBBLESTONE_WALL
    );

    public static final MaterialList SANDSTONE_WALLS = EMPTY;

    public static final MaterialList NETHER_BRICK_WALLS = EMPTY;

    public static final MaterialList UNBREAKABLE_BLOCKS = new MaterialList(
        new Material[] {
            BARRIER,
            BEDROCK,
            BUBBLE_COLUMN,
            DRAGON_BREATH,
            DRAGON_EGG,
            END_CRYSTAL,
            END_GATEWAY,
            END_PORTAL,
            END_PORTAL_FRAME,
            LAVA,
            STRUCTURE_VOID,
            STRUCTURE_BLOCK,
            WATER,
            PISTON_HEAD,
            MOVING_PISTON
        },
        AIR,
        COMMAND_BLOCKS
    );

    public static final MaterialList LASER_BLACKLIST_BLOCKS = new MaterialList(OBSIDIAN, CRYING_OBSIDIAN);

    public static final MaterialList STORAGE_BLOCKS = new MaterialList(
        new Material[] {
            DISPENSER,
            SPAWNER,
            CHEST,
            FURNACE,
            JUKEBOX,
            ENDER_CHEST,
            BEACON,
            TRAPPED_CHEST,
            HOPPER,
            DROPPER,
            BREWING_STAND,
            ANVIL,
            BARREL,
            BEE_NEST,
            BEEHIVE,
        },
        SHULKER_BOXES
    );

    public static final MaterialList INTERACTABLE_BLOCKS = new MaterialList(
        new Material[] {
            NOTE_BLOCK,
            CRAFTING_TABLE,
            LEVER,
            REPEATER,
            ENCHANTING_TABLE,
            COMPARATOR,
            DAYLIGHT_DETECTOR,
            PISTON,
            OBSERVER,
            CARTOGRAPHY_TABLE,
            GRINDSTONE,
            SMITHING_TABLE,
            LECTERN,
            SMOKER,
            BLAST_FURNACE
        },
        BEDS,
        DOORS,
        TRAPDOORS,
        FENCE_GATES,
        COMMAND_BLOCKS,
        BUTTONS,
        STORAGE_BLOCKS
    );

    public static final MaterialList TERRAFORMER_MATERIALS = new MaterialList(
        new Material[] {
            GRASS_BLOCK,
            Material.DIRT,
            GRAVEL,
            Material.SANDSTONE,
            BRICK,
            TNT,
            BOOKSHELF,
            Material.ICE,
            SNOW_BLOCK,
            CLAY,
            NETHERRACK,
            SOUL_SAND,
            Material.STONE_BRICKS,
            MYCELIUM,
            NETHER_BRICK,
            END_STONE,
            EMERALD_ORE,
            QUARTZ_BLOCK,
            SLIME_BLOCK,
            Material.PRISMARINE,
            PACKED_ICE,
            RED_SANDSTONE
        },
        STONES,
        COBBLESTONES,
        SANDS,
        ORES,
        TERRACOTTA,
        GLAZED_TERRACOTTA,
        WOOL,
        WOOD,
        WOODEN_PLANKS,
        STRIPPED_LOGS,
        LOGS,
        CONCRETE,
        CONCRETE_POWDER,
        STAINED_GLASS,
        STRIPPED_WOOD
    );

    public static final MaterialList LUMBER_WHITELIST = new MaterialList(
        new Material[] {
            Material.DIRT,
            GRASS,
            VINE,
            SNOW,
            COCOA,
            GRAVEL,
            STONE,
            DIORITE,
            GRANITE,
            ANDESITE,
            WATER,
            LAVA,
            Material.SAND,
            RED_SAND,
            BROWN_MUSHROOM,
            RED_MUSHROOM,
            MOSSY_COBBLESTONE,
            CLAY,
            MYCELIUM,
            TORCH,
            SUGAR_CANE,
            GRASS_BLOCK,
            PODZOL,
            FERN,
            GRASS,
            MELON,
            PUMPKIN,
            NETHERRACK,
            NETHER_QUARTZ_ORE,
            NETHER_GOLD_ORE,
            NETHER_WART_BLOCK,
            CRIMSON_NYLIUM,
            CRIMSON_ROOTS,
            CRIMSON_FUNGUS,
            CRIMSON_HYPHAE,
            WEEPING_VINES,
            WEEPING_VINES_PLANT,
            WARPED_NYLIUM,
            WARPED_WART_BLOCK,
            WARPED_FUNGUS,
            WARPED_ROOTS,
            WARPED_HYPHAE,
            SHROOMLIGHT,
            FIRE,
            END_STONE
        },
        TRUNKS,
        LEAVES,
        SMALL_FLOWERS,
        LARGE_FLOWERS,
        SAPLINGS,
        AIR,
        DEADLY_PLANTS
    );

    public static final HashMap<Material, Tuple<Material, Double>> FIRE_SMELT_MAP = new HashMap<>();

    static {
        FIRE_SMELT_MAP.put(STONE, new Tuple<Material, Double>(STONE, 0.1));
        FIRE_SMELT_MAP.put(DIORITE, new Tuple<Material, Double>(POLISHED_DIORITE, 0.1));
        FIRE_SMELT_MAP.put(ANDESITE, new Tuple<Material, Double>(POLISHED_ANDESITE, 0.1));
        FIRE_SMELT_MAP.put(GRANITE, new Tuple<Material, Double>(POLISHED_GRANITE, 0.1));
        FIRE_SMELT_MAP.put(COBBLESTONE, new Tuple<Material, Double>(STONE, 0.5));
        FIRE_SMELT_MAP.put(COBBLED_DEEPSLATE, new Tuple<Material, Double>(DEEPSLATE, 0.1));
        FIRE_SMELT_MAP.put(Material.SANDSTONE, new Tuple<Material, Double>(SMOOTH_SANDSTONE, 0.1));
        FIRE_SMELT_MAP.put(RED_SANDSTONE, new Tuple<Material, Double>(SMOOTH_RED_SANDSTONE, 0.1));
        FIRE_SMELT_MAP.put(BASALT, new Tuple<Material, Double>(SMOOTH_BASALT, 0.1));
        FIRE_SMELT_MAP.put(NETHERRACK, new Tuple<Material, Double>(NETHER_BRICK, 0.1));
        FIRE_SMELT_MAP.put(RAW_IRON, new Tuple<Material, Double>(IRON_INGOT, 0.7));
        FIRE_SMELT_MAP.put(RAW_GOLD, new Tuple<Material, Double>(GOLD_INGOT, 1.0));
        FIRE_SMELT_MAP.put(RAW_COPPER, new Tuple<Material, Double>(COPPER_INGOT, 0.7));
        FIRE_SMELT_MAP.put(ANCIENT_DEBRIS, new Tuple<Material, Double>(NETHERITE_SCRAP, 2.0));
        FIRE_SMELT_MAP.put(QUARTZ_BLOCK, new Tuple<Material, Double>(SMOOTH_QUARTZ, 0.1));
        FIRE_SMELT_MAP.put(CLAY_BALL, new Tuple<Material, Double>(BRICK, 0.3));
        FIRE_SMELT_MAP.put(CLAY, new Tuple<Material, Double>(Material.TERRACOTTA, 0.35));
        FIRE_SMELT_MAP.put(Material.CACTUS, new Tuple<Material, Double>(GREEN_DYE, 1.0));
        FIRE_SMELT_MAP.put(CHORUS_FRUIT, new Tuple<Material, Double>(POPPED_CHORUS_FRUIT, 0.1));
        LOGS.stream().forEach((m) -> {
            FIRE_SMELT_MAP.put(m, new Tuple<Material, Double>(CHARCOAL, 0.15));
        });
    }

    public static final MaterialList SHRED_PICKS = new MaterialList(
        new Material[] {
            STONE,
            GRANITE,
            ANDESITE,
            DIORITE,
            BASALT,
            TUFF,
            DEEPSLATE,
            NETHERRACK,
            GLOWSTONE,
            Material.SANDSTONE,
            RED_SANDSTONE,
            Material.ICE,
            PACKED_ICE,
            BLUE_ICE,
            MUD
        },
        ORES,
        TERRACOTTA
    );

    public static final MaterialList SHRED_SHOVELS = new MaterialList(
        new Material[] {
            GRASS_BLOCK,
            PODZOL,
            Material.DIRT,
            MYCELIUM,
            SOUL_SAND,
            SOUL_SOIL,
            CRIMSON_NYLIUM,
            WARPED_NYLIUM,
            GRAVEL,
            SOUL_SAND,
            CLAY,
            MUD,
            MUDDY_MANGROVE_ROOTS
        },
        SANDS
    );

    public static final MaterialList PERSEPHONE_CROPS = new MaterialList(
        WHEAT,
        POTATO,
        CARROT,
        BEETROOT,
        NETHER_WART,
        SOUL_SAND,
        FARMLAND
    );

    private final Material[] values;

    public MaterialList(final @NotNull Material... values) {
        this(values, new MaterialList[0]);
    }

    public MaterialList(final @NotNull MaterialList... listsToAdd) {
        this(new Material[0], listsToAdd);
    }

    public MaterialList(final @NotNull Material[] values, final @NotNull MaterialList... listsToAdd) {
        // Calculate total array length needed for all values.
        int totalLength = values.length;
        for (final MaterialList other : listsToAdd) {
            totalLength += other.values.length;
        }

        this.values = new Material[totalLength];

        // No need to add anything to the array if it's 0 length.
        if (totalLength == 0) {
            return;
        }

        int currentIndex = 0;

        // Add all materials from base values array.
        for (final Material material : values) {
            this.values[currentIndex++] = material;
        }

        // Add all materials from each extra list.
        for (final MaterialList materialList : listsToAdd) {
            for (final Material material : materialList) {
                this.values[currentIndex++] = material;
            }
        }
    }

    @Override
    public Material get(int index) {
        return this.values[index];
    }

    @Override
    public int size() {
        return this.values.length;
    }

    @Override
    public int indexOf(final @NotNull Object object) {
        if (!(object instanceof Material)) {
            throw new IllegalArgumentException("Object must be a Material.");
        }

        final Material search = (Material) object;

        for (int i = 0; i < this.values.length; i++) {
            if (this.values[i] == search) {
                return i;
            }
        }

        return -1;
    }

    @NotNull
    public Material getNext(final @NotNull Material material) {
        final int index = this.indexOf(material);

        if (index == -1) {
            throw new IllegalArgumentException("Material is not contained in the MaterialList.");
        }

        return this.values[(index + 1) % this.values.length];
    }

    @NotNull
    public Material getRandom() {
        return this.values[ThreadLocalRandom.current().nextInt(this.values.length)];
    }
}
