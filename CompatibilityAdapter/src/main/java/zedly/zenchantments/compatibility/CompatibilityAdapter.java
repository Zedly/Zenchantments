/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zedly.zenchantments.compatibility;

import java.util.Random;

import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.*;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.EntityBlockFormEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;

import static org.bukkit.Material.*;
import static org.bukkit.Material.TROPICAL_FISH;
import static org.bukkit.entity.EntityType.*;
import static org.bukkit.entity.EntityType.PUFFERFISH;
import org.bukkit.event.block.Action;
import static org.bukkit.potion.PotionEffectType.*;
import static org.bukkit.potion.PotionEffectType.DOLPHINS_GRACE;
import static org.bukkit.potion.PotionEffectType.WATER_BREATHING;

import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;

public class CompatibilityAdapter {

    private static final CompatibilityAdapter INSTANCE = new CompatibilityAdapter();
    private static final Random RND = new Random();

    //region Enums
    //region Colors
    //region Beds
    private final EnumStorage<Material> BEDS_E = new EnumStorage<>(new Material[]{WHITE_BED, ORANGE_BED, MAGENTA_BED,
        LIGHT_BLUE_BED, YELLOW_BED, LIME_BED, PINK_BED, GRAY_BED, LIGHT_GRAY_BED, CYAN_BED, PURPLE_BED, BLUE_BED,
        BROWN_BED, GREEN_BED, RED_BED, BLACK_BED});

    public EnumStorage<Material> Beds() {
        return BEDS_E;
    }
    //endregion

    //region Wools
    private final EnumStorage<Material> WOOLS_E = new EnumStorage<>(new Material[]{WHITE_WOOL, ORANGE_WOOL, MAGENTA_WOOL,
        LIGHT_BLUE_WOOL, YELLOW_WOOL, LIME_WOOL, PINK_WOOL, GRAY_WOOL, LIGHT_GRAY_WOOL, CYAN_WOOL, PURPLE_WOOL,
        BLUE_WOOL, BROWN_WOOL, GREEN_WOOL, RED_WOOL, BLACK_WOOL});

    public EnumStorage<Material> Wools() {
        return WOOLS_E;
    }
    //endregion

    //region Shulker Boxes
    private final EnumStorage<Material> SHULKER_BOXES_E = new EnumStorage<>(new Material[]{WHITE_SHULKER_BOX,
        ORANGE_SHULKER_BOX, MAGENTA_SHULKER_BOX, LIGHT_BLUE_SHULKER_BOX, YELLOW_SHULKER_BOX, LIME_SHULKER_BOX,
        PINK_SHULKER_BOX, GRAY_SHULKER_BOX, LIGHT_GRAY_SHULKER_BOX, CYAN_SHULKER_BOX, PURPLE_SHULKER_BOX,
        BLUE_SHULKER_BOX, BROWN_SHULKER_BOX, GREEN_SHULKER_BOX, RED_SHULKER_BOX, BLACK_SHULKER_BOX, SHULKER_BOX});

    public EnumStorage<Material> ShulkerBoxes() {
        return SHULKER_BOXES_E;
    }
    //endregion

    //region Concrete Powders
    private final EnumStorage<Material> CONCRETE_POWDERS_E = new EnumStorage<>(new Material[]{WHITE_CONCRETE_POWDER,
        ORANGE_CONCRETE_POWDER, MAGENTA_CONCRETE_POWDER, LIGHT_BLUE_CONCRETE_POWDER, YELLOW_CONCRETE_POWDER,
        LIME_CONCRETE_POWDER, PINK_CONCRETE_POWDER, GRAY_CONCRETE_POWDER, LIGHT_GRAY_CONCRETE_POWDER,
        CYAN_CONCRETE_POWDER, PURPLE_CONCRETE_POWDER, BLUE_CONCRETE_POWDER, BROWN_CONCRETE_POWDER,
        GREEN_CONCRETE_POWDER, RED_CONCRETE_POWDER, BLACK_CONCRETE_POWDER});

    public EnumStorage<Material> ConcretePowders() {
        return CONCRETE_POWDERS_E;
    }
    //endregion

    //region Concretes
    private final EnumStorage<Material> CONCRETES_E = new EnumStorage<>(new Material[]{WHITE_CONCRETE, ORANGE_CONCRETE,
        MAGENTA_CONCRETE, LIGHT_BLUE_CONCRETE, YELLOW_CONCRETE, LIME_CONCRETE, PINK_CONCRETE, GRAY_CONCRETE,
        LIGHT_GRAY_CONCRETE, CYAN_CONCRETE, PURPLE_CONCRETE, BLUE_CONCRETE, BROWN_CONCRETE, GREEN_CONCRETE,
        RED_CONCRETE, BLACK_CONCRETE});

    public EnumStorage<Material> Concretes() {
        return CONCRETES_E;
    }
    //endregion

    //region Glazed Terracottas
    private final EnumStorage<Material> GLAZED_TERRACOTTAS_E = new EnumStorage<>(new Material[]{WHITE_GLAZED_TERRACOTTA,
        ORANGE_GLAZED_TERRACOTTA, MAGENTA_GLAZED_TERRACOTTA, LIGHT_BLUE_GLAZED_TERRACOTTA, YELLOW_GLAZED_TERRACOTTA,
        LIME_GLAZED_TERRACOTTA, PINK_GLAZED_TERRACOTTA, GRAY_GLAZED_TERRACOTTA, LIGHT_GRAY_GLAZED_TERRACOTTA,
        CYAN_GLAZED_TERRACOTTA, PURPLE_GLAZED_TERRACOTTA, BLUE_GLAZED_TERRACOTTA, BROWN_GLAZED_TERRACOTTA,
        GREEN_GLAZED_TERRACOTTA, RED_GLAZED_TERRACOTTA, BLACK_GLAZED_TERRACOTTA});

    public EnumStorage<Material> GlazedTerracottas() {
        return GLAZED_TERRACOTTAS_E;
    }
    //endregion

    //region Terracottas
    private final EnumStorage<Material> TERRACOTTAS_E = new EnumStorage<>(new Material[]{WHITE_TERRACOTTA,
        ORANGE_TERRACOTTA, MAGENTA_TERRACOTTA, LIGHT_BLUE_TERRACOTTA, YELLOW_TERRACOTTA, LIME_TERRACOTTA,
        PINK_TERRACOTTA, GRAY_TERRACOTTA, LIGHT_GRAY_TERRACOTTA, CYAN_TERRACOTTA, PURPLE_TERRACOTTA, BLUE_TERRACOTTA,
        BROWN_TERRACOTTA, GREEN_TERRACOTTA, RED_TERRACOTTA, BLACK_TERRACOTTA});

    public EnumStorage<Material> Terracottas() {
        return TERRACOTTAS_E;
    }
    //endregion

    //region Carpets
    private final EnumStorage<Material> CARPETS_E = new EnumStorage<>(new Material[]{WHITE_CARPET, ORANGE_CARPET,
        MAGENTA_CARPET, LIGHT_BLUE_CARPET, YELLOW_CARPET, LIME_CARPET, PINK_CARPET, GRAY_CARPET, LIGHT_GRAY_CARPET,
        CYAN_CARPET, PURPLE_CARPET, BLUE_CARPET, BROWN_CARPET, GREEN_CARPET, RED_CARPET, BLACK_CARPET});

    public EnumStorage<Material> Carpets() {
        return CARPETS_E;
    }
    //endregion

    //region Stained Glass
    private final EnumStorage<Material> STAINED_GLASS_E = new EnumStorage<>(new Material[]{WHITE_STAINED_GLASS,
        ORANGE_STAINED_GLASS, MAGENTA_STAINED_GLASS, LIGHT_BLUE_STAINED_GLASS, YELLOW_STAINED_GLASS, LIME_STAINED_GLASS,
        PINK_STAINED_GLASS, GRAY_STAINED_GLASS, LIGHT_GRAY_STAINED_GLASS, CYAN_STAINED_GLASS, PURPLE_STAINED_GLASS,
        BLUE_STAINED_GLASS, BROWN_STAINED_GLASS, GREEN_STAINED_GLASS, RED_STAINED_GLASS, BLACK_STAINED_GLASS});

    public EnumStorage<Material> StainedGlass() {
        return STAINED_GLASS_E;
    }
    //endregion

    //region Stained Glass Panes
    private final EnumStorage<Material> STAINED_GLASS_PANES_E = new EnumStorage<>(new Material[]{WHITE_STAINED_GLASS_PANE,
        ORANGE_STAINED_GLASS_PANE, MAGENTA_STAINED_GLASS_PANE, LIGHT_BLUE_STAINED_GLASS_PANE, YELLOW_STAINED_GLASS_PANE,
        LIME_STAINED_GLASS_PANE, PINK_STAINED_GLASS_PANE, GRAY_STAINED_GLASS_PANE, LIGHT_GRAY_STAINED_GLASS_PANE,
        CYAN_STAINED_GLASS_PANE, PURPLE_STAINED_GLASS_PANE, BLUE_STAINED_GLASS_PANE, BROWN_STAINED_GLASS_PANE,
        GREEN_STAINED_GLASS_PANE, RED_STAINED_GLASS_PANE, BLACK_STAINED_GLASS_PANE});

    public EnumStorage<Material> StainedGlassPanes() {
        return STAINED_GLASS_PANES_E;
    }
    //endregion

    //region Banners
    private final EnumStorage<Material> BANNERS_E = new EnumStorage<>(new Material[]{WHITE_BANNER, ORANGE_BANNER,
        MAGENTA_BANNER, LIGHT_BLUE_BANNER, YELLOW_BANNER, LIME_BANNER, PINK_BANNER, GRAY_BANNER, LIGHT_GRAY_BANNER,
        CYAN_BANNER, PURPLE_BANNER, BLUE_BANNER, BROWN_BANNER, GREEN_BANNER, RED_BANNER, BLACK_BANNER});

    public EnumStorage<Material> Banners() {
        return BANNERS_E;
    }
    //endregion

    //region Wall Banners
    private final EnumStorage<Material> WALL_BANNERS_E = new EnumStorage<>(new Material[]{WHITE_WALL_BANNER,
        ORANGE_WALL_BANNER, MAGENTA_WALL_BANNER, LIGHT_BLUE_WALL_BANNER, YELLOW_WALL_BANNER, LIME_WALL_BANNER,
        PINK_WALL_BANNER, GRAY_WALL_BANNER, LIGHT_GRAY_WALL_BANNER, CYAN_WALL_BANNER, PURPLE_WALL_BANNER,
        BLUE_WALL_BANNER, BROWN_WALL_BANNER, GREEN_WALL_BANNER, RED_WALL_BANNER, BLACK_WALL_BANNER});

    public EnumStorage<Material> WallBanners() {
        return WALL_BANNERS_E;
    }
    //endregion

    //region Dyes
    private final EnumStorage<Material> DYES_E = new EnumStorage<>(new Material[]{BONE_MEAL, ORANGE_DYE, MAGENTA_DYE,
        LIGHT_BLUE_DYE, YELLOW_DYE, LIME_DYE, PINK_DYE, GRAY_DYE, LIGHT_GRAY_DYE, CYAN_DYE, PURPLE_DYE,
        LAPIS_LAZULI, COCOA_BEANS, GREEN_DYE, RED_DYE, INK_SAC});

    public EnumStorage<Material> Dyes() {
        return DYES_E;
    }
    //endregion

    //endregion
    //region Woods
    //region Signs
    private final EnumStorage<Material> SIGNS_E = new EnumStorage<>(new Material[]{ACACIA_SIGN});

    public EnumStorage<Material> Signs() {
        return SIGNS_E;
    }
    //endregion

    //region Boats
    private final EnumStorage<Material> BOATS_E = new EnumStorage<>(new Material[]{ACACIA_BOAT, BIRCH_BOAT,
        DARK_OAK_BOAT, JUNGLE_BOAT, OAK_BOAT, SPRUCE_BOAT});

    public EnumStorage<Material> Boats() {
        return BOATS_E;
    }
    //endregion

    //region Wood Buttons
    private final EnumStorage<Material> WOOD_BUTTONS_E = new EnumStorage<>(new Material[]{ACACIA_BUTTON, BIRCH_BUTTON,
        DARK_OAK_BUTTON, JUNGLE_BUTTON, OAK_BUTTON, SPRUCE_BUTTON});

    public EnumStorage<Material> WoodButtons() {
        return WOOD_BUTTONS_E;
    }
    //endregion

    //region Wood Doors
    private final EnumStorage<Material> WOOD_DOORS_E = new EnumStorage<>(new Material[]{ACACIA_DOOR, BIRCH_DOOR,
        DARK_OAK_DOOR, JUNGLE_DOOR, OAK_DOOR, SPRUCE_DOOR});

    public EnumStorage<Material> WoodDoors() {
        return WOOD_DOORS_E;
    }
    //endregion

    //region Wood Fences
    private final EnumStorage<Material> WOOD_FENCES_E = new EnumStorage<>(new Material[]{ACACIA_FENCE, BIRCH_FENCE,
        DARK_OAK_FENCE, JUNGLE_FENCE, OAK_FENCE, SPRUCE_FENCE});

    public EnumStorage<Material> WoodFences() {
        return WOOD_FENCES_E;
    }
    //endregion

    //region Fence Gates
    private final EnumStorage<Material> FENCE_GATES_E = new EnumStorage<>(new Material[]{ACACIA_FENCE_GATE,
        BIRCH_FENCE_GATE, DARK_OAK_FENCE_GATE, JUNGLE_FENCE_GATE, OAK_FENCE_GATE, SPRUCE_FENCE_GATE});

    public EnumStorage<Material> FenceGates() {
        return FENCE_GATES_E;
    }
    //endregion

    //region Leaves
    private final EnumStorage<Material> LEAVES_E = new EnumStorage<>(new Material[]{ACACIA_LEAVES, BIRCH_LEAVES,
        DARK_OAK_LEAVES, JUNGLE_LEAVES, OAK_LEAVES, SPRUCE_LEAVES});

    public EnumStorage<Material> Leaves() {
        return LEAVES_E;
    }
    //endregion

    //region Logs
    private final EnumStorage<Material> LOGS_E = new EnumStorage<>(new Material[]{ACACIA_LOG, BIRCH_LOG,
        DARK_OAK_LOG, JUNGLE_LOG, OAK_LOG, SPRUCE_LOG});

    public EnumStorage<Material> Logs() {
        return LOGS_E;
    }
    //endregion

    //region Planks
    private final EnumStorage<Material> PLANKS_E = new EnumStorage<>(new Material[]{ACACIA_PLANKS, BIRCH_PLANKS,
        DARK_OAK_PLANKS, JUNGLE_PLANKS, OAK_PLANKS, SPRUCE_PLANKS});

    public EnumStorage<Material> Planks() {
        return PLANKS_E;
    }
    //endregion

    //region Wood Pressure Plates
    private final EnumStorage<Material> WOOD_PRESSURE_PLATES_E = new EnumStorage<>(new Material[]{ACACIA_PRESSURE_PLATE,
        BIRCH_PRESSURE_PLATE, DARK_OAK_PRESSURE_PLATE, JUNGLE_PRESSURE_PLATE, OAK_PRESSURE_PLATE,
        SPRUCE_PRESSURE_PLATE});

    public EnumStorage<Material> WoodPressurePlates() {
        return WOOD_PRESSURE_PLATES_E;
    }
    //endregion

    //region Saplings
    private final EnumStorage<Material> SAPLINGS_E = new EnumStorage<>(new Material[]{ACACIA_SAPLING, BIRCH_SAPLING,
        DARK_OAK_SAPLING, JUNGLE_SAPLING, OAK_SAPLING, SPRUCE_SAPLING});

    public EnumStorage<Material> Saplings() {
        return SAPLINGS_E;
    }
    //endregion

    //region Wood Slabs
    private final EnumStorage<Material> WOOD_SLABS_E = new EnumStorage<>(new Material[]{ACACIA_SLAB, BIRCH_SLAB,
        DARK_OAK_SLAB, JUNGLE_SLAB, OAK_SLAB, SPRUCE_SLAB});

    public EnumStorage<Material> WoodSlabs() {
        return WOOD_SLABS_E;
    }
    //endregion

    //region Wood Stairs
    private final EnumStorage<Material> WOOD_STAIRS_E = new EnumStorage<>(new Material[]{ACACIA_STAIRS, BIRCH_STAIRS,
        DARK_OAK_STAIRS, JUNGLE_STAIRS, OAK_STAIRS, SPRUCE_STAIRS});

    public EnumStorage<Material> WoodStairs() {
        return WOOD_STAIRS_E;
    }
    //endregion

    //region Wood Trapdoors
    private final EnumStorage<Material> WOOD_TRAPDOORS_E
            = new EnumStorage<>(new Material[]{ACACIA_TRAPDOOR, BIRCH_TRAPDOOR,
        DARK_OAK_TRAPDOOR, JUNGLE_TRAPDOOR, OAK_TRAPDOOR, SPRUCE_TRAPDOOR});

    public EnumStorage<Material> WoodTrapdoors() {
        return WOOD_TRAPDOORS_E;
    }
    //endregion

    //region Woods
    private final EnumStorage<Material> WOODS_E = new EnumStorage<>(new Material[]{ACACIA_WOOD, BIRCH_WOOD,
        DARK_OAK_WOOD, JUNGLE_WOOD, OAK_WOOD, SPRUCE_WOOD});

    public EnumStorage<Material> Woods() {
        return WOODS_E;
    }
    //endregion

    //region Stripped Logs
    private final EnumStorage<Material> STRIPPED_LOGS_E
            = new EnumStorage<>(new Material[]{STRIPPED_ACACIA_LOG, STRIPPED_BIRCH_LOG,
        STRIPPED_DARK_OAK_LOG, STRIPPED_JUNGLE_LOG, STRIPPED_OAK_LOG, STRIPPED_SPRUCE_LOG});

    public EnumStorage<Material> StrippedLogs() {
        return STRIPPED_LOGS_E;
    }
    //endregion

    //region Stripped Woods
    private final EnumStorage<Material> STRIPPED_WOODS_E
            = new EnumStorage<>(new Material[]{STRIPPED_ACACIA_WOOD, STRIPPED_BIRCH_WOOD,
        STRIPPED_DARK_OAK_WOOD, STRIPPED_JUNGLE_WOOD, STRIPPED_OAK_WOOD, STRIPPED_SPRUCE_WOOD});

    public EnumStorage<Material> StrippedWoods() {
        return STRIPPED_WOODS_E;
    }

    //endregion
    //endregion
    //region Plants
    //region Deadly Plants
    private final EnumStorage<Material> DEADLY_PLANTS_E = new EnumStorage<>(new Material[]{});

    public EnumStorage<Material> DeadlyPlants() {
        return DEADLY_PLANTS_E;
    }
    //endregion

    //region Small Flowers
    private final EnumStorage<Material> SMALL_FLOWERS_E = new EnumStorage<>(new Material[]{DANDELION, POPPY, BLUE_ORCHID,
        ALLIUM, AZURE_BLUET, RED_TULIP, ORANGE_TULIP, WHITE_TULIP, PINK_TULIP, OXEYE_DAISY});

    public EnumStorage<Material> SmallFlowers() {
        return SMALL_FLOWERS_E;
    }
    //endregion

    //region Large Flowers
    private final EnumStorage<Material> LARGE_FLOWERS_E = new EnumStorage<>(new Material[]{SUNFLOWER, LILAC,
        TALL_GRASS, LARGE_FERN, ROSE_BUSH, PEONY});

    public EnumStorage<Material> LargeFlowers() {
        return LARGE_FLOWERS_E;
    }
    //endregion

    //region Grown Crops
    private final EnumStorage<Material> GROWN_CROPS_E
            = new EnumStorage<>(new Material[]{WHEAT, POTATOES, CARROTS, COCOA, BEETROOTS, NETHER_WART, SWEET_BERRY_BUSH});

    public EnumStorage<Material> GrownCrops() {
        return GROWN_CROPS_E;
    }
    //endregion

    //region Crop Yields
    private final EnumStorage<Material> CROP_YIELDS_E
            = new EnumStorage<>(new Material[]{WHEAT, POTATO, CARROT, COCOA_BEANS, BEETROOT, NETHER_WART});

    public EnumStorage<Material> CropYields() {
        return CROP_YIELDS_E;
    }

    //endregion
    //region Partial Harvest Crops
    private final EnumStorage<Material> PARTIAL_HARVEST_CROPS_E = new EnumStorage<>(new Material[]{});

    public EnumStorage<Material> PartialHarvestCrops() {
        return PARTIAL_HARVEST_CROPS_E;
    }
    //endregion

    //region Partial Harvest Crop Yields
    private final EnumStorage<Material> PARTIAL_HARVEST_CROP_YIELDS_E = new EnumStorage<>(new Material[]{});

    public EnumStorage<Material> PartialHarvestCropYeilds() {
        return PARTIAL_HARVEST_CROP_YIELDS_E;
    }
    //endregion

    //region Grown Melon
    private final EnumStorage<Material> GROWN_MELON_E = new EnumStorage<>(new Material[]{MELON, PUMPKIN});

    public EnumStorage<Material> GrownMelon() {
        return GROWN_MELON_E;
    }

    //endregion
    //region Melon Yeilds
    private final EnumStorage<Material> MELON_YIELDS_E = new EnumStorage<>(new Material[]{MELON_SLICE, PUMPKIN});

    public EnumStorage<Material> MelonYields() {
        return MELON_YIELDS_E;
    }

    //endregion
    //region Trunk Blocks
    private final EnumStorage<Material> TRUNK_BLOCKS_E = new EnumStorage<>(new Material[]{MUSHROOM_STEM,
        BROWN_MUSHROOM_BLOCK, RED_MUSHROOM_BLOCK}, Logs(), Woods(), StrippedWoods(), StrippedLogs());

    public EnumStorage<Material> TrunkBlocks() {
        return TRUNK_BLOCKS_E;
    }
    //endregion

    //region Coral Blocks
    private final EnumStorage<Material> CORAL_BLOCKS_E = new EnumStorage<>(new Material[]{BRAIN_CORAL_BLOCK,
        BUBBLE_CORAL_BLOCK, FIRE_CORAL_BLOCK, HORN_CORAL_BLOCK, TUBE_CORAL_BLOCK});

    public EnumStorage<Material> CoralBlocks() {
        return CORAL_BLOCKS_E;
    }
    //endregion

    //region Dead Coral Blocks
    private final EnumStorage<Material> DEAD_CORAL_BLOCKS_E = new EnumStorage<>(new Material[]{DEAD_BRAIN_CORAL_BLOCK,
        DEAD_BUBBLE_CORAL_BLOCK, DEAD_FIRE_CORAL_BLOCK, DEAD_HORN_CORAL_BLOCK, DEAD_TUBE_CORAL_BLOCK});

    public EnumStorage<Material> DeadCoralBlocks() {
        return DEAD_CORAL_BLOCKS_E;
    }
    //endregion

    //region Corals
    private final EnumStorage<Material> CORALS_E = new EnumStorage<>(new Material[]{BRAIN_CORAL, BUBBLE_CORAL,
        FIRE_CORAL, HORN_CORAL, TUBE_CORAL});

    public EnumStorage<Material> Corals() {
        return CORALS_E;
    }
    //endregion

    //region Coral Fans
    private final EnumStorage<Material> CORAL_FANS_E = new EnumStorage<>(new Material[]{BRAIN_CORAL_FAN, BUBBLE_CORAL_FAN,
        FIRE_CORAL_FAN, HORN_CORAL_FAN, TUBE_CORAL_FAN});

    public EnumStorage<Material> CoralFans() {
        return CORAL_FANS_E;
    }
    //endregion

    //region Dead Coral Fans
    private final EnumStorage<Material> DEAD_CORAL_FANS_E = new EnumStorage<>(new Material[]{DEAD_BRAIN_CORAL_FAN,
        DEAD_BUBBLE_CORAL_FAN, DEAD_FIRE_CORAL_FAN, DEAD_HORN_CORAL_FAN, DEAD_TUBE_CORAL_FAN});

    public EnumStorage<Material> DeadCoralFans() {
        return DEAD_CORAL_FANS_E;
    }
    //endregion

    //region Dead Corals
    private final EnumStorage<Material> DEAD_CORALS_E = new EnumStorage<>(new Material[]{});

    public EnumStorage<Material> DeadCorals() {
        return DEAD_CORALS_E;
    }
    //endregion

    //region Dead Coral Wall Fans
    private final EnumStorage<Material> DEAD_CORAL_WALL_FANS_E = new EnumStorage<>(new Material[]{DEAD_BRAIN_CORAL_WALL_FAN,
        DEAD_BUBBLE_CORAL_WALL_FAN, DEAD_FIRE_CORAL_WALL_FAN, DEAD_HORN_CORAL_WALL_FAN, DEAD_TUBE_CORAL_WALL_FAN});

    public EnumStorage<Material> DeadCoralWallFans() {
        return DEAD_CORAL_WALL_FANS_E;
    }
    //endregion

    //region Mushrooms
    private final EnumStorage<Material> MUSHROOMS_E = new EnumStorage<>(new Material[]{RED_MUSHROOM, BROWN_MUSHROOM});

    public EnumStorage<Material> Mushrooms() {
        return MUSHROOMS_E;
    }
    //endregion

    //region Mushroom Blocks
    private final EnumStorage<Material> MUSHROOM_BLOCKS_E = new EnumStorage<>(new Material[]{BROWN_MUSHROOM_BLOCK,
        MUSHROOM_STEM, RED_MUSHROOM_BLOCK});

    public EnumStorage<Material> MushroomBlocks() {
        return MUSHROOM_BLOCKS_E;
    }
    //endregion

    //region Short Grasses
    private final EnumStorage<Material> SHORT_GRASSES_E = new EnumStorage<>(new Material[]{GRASS, DEAD_BUSH, FERN});

    public EnumStorage<Material> ShortGrasses() {
        return SHORT_GRASSES_E;
    }
    //endregion

    //endregion
    //region Misc
    //region Buttons
    private final EnumStorage<Material> BUTTONS_E = new EnumStorage<>(new Material[]{STONE_BUTTON}, WoodButtons());

    public EnumStorage<Material> Buttons() {
        return BUTTONS_E;
    }
    //endregion

    //region Doors
    private final EnumStorage<Material> DOORS_E = new EnumStorage<>(new Material[]{IRON_DOOR}, WoodDoors());

    public EnumStorage<Material> Doors() {
        return DOORS_E;
    }
    //endregion

    //region Trapdoors
    private final EnumStorage<Material> TRAPDOORS_E = new EnumStorage<>(new Material[]{IRON_TRAPDOOR}, WoodTrapdoors());

    public EnumStorage<Material> Trapdoors() {
        return TRAPDOORS_E;
    }
    //endregion

    //region Pressure Plates
    private final EnumStorage<Material> PRESSURE_PLATES_E = new EnumStorage<>(new Material[]{STONE_PRESSURE_PLATE,
        LIGHT_WEIGHTED_PRESSURE_PLATE, HEAVY_WEIGHTED_PRESSURE_PLATE}, WoodPressurePlates());

    public EnumStorage<Material> PressurePlates() {
        return PRESSURE_PLATES_E;
    }
    //endregion

    //region Airs
    private final EnumStorage<Material> AIRS_E = new EnumStorage<>(new Material[]{AIR, CAVE_AIR, VOID_AIR});

    public EnumStorage<Material> Airs() {
        return AIRS_E;
    }
    //endregion

    //region Command Blocks
    private final EnumStorage<Material> COMMAND_BLOCKS_E = new EnumStorage<>(new Material[]{COMMAND_BLOCK,
        CHAIN_COMMAND_BLOCK, REPEATING_COMMAND_BLOCK});

    public EnumStorage<Material> CommandBlocks() {
        return COMMAND_BLOCKS_E;
    }
    //endregion

    //region Ores
    private final EnumStorage<Material> ORES_E = new EnumStorage<>(new Material[]{COAL_ORE, REDSTONE_ORE, DIAMOND_ORE,
        GOLD_ORE, IRON_ORE, LAPIS_ORE, GLOWSTONE, NETHER_QUARTZ_ORE, EMERALD_ORE});

    public EnumStorage<Material> Ores() {
        return ORES_E;
    }
    //endregion

    //region Sands
    private final EnumStorage<Material> SANDS_E = new EnumStorage<>(new Material[]{SAND, RED_SAND});

    public EnumStorage<Material> Sands() {
        return SANDS_E;
    }
    //endregion

    //region Ices
    private final EnumStorage<Material> ICES_E = new EnumStorage<>(new Material[]{ICE, BLUE_ICE, PACKED_ICE});

    public EnumStorage<Material> Ices() {
        return ICES_E;
    }
    //endregion

    //region Dirts
    private final EnumStorage<Material> DIRTS_E = new EnumStorage<>(new Material[]{DIRT, COARSE_DIRT, MYCELIUM, PODZOL,
        GRASS_BLOCK, GRASS_PATH});

    public EnumStorage<Material> Dirts() {
        return DIRTS_E;
    }
    //endregion

    //region Stones
    private final EnumStorage<Material> STONES_E = new EnumStorage<>(new Material[]{STONE, GRANITE, ANDESITE, DIORITE});

    public EnumStorage<Material> Stones() {
        return STONES_E;
    }
    //endregion

    //region Cobblestones
    private final EnumStorage<Material> COBBLESTONES_E = new EnumStorage<>(new Material[]{COBBLESTONE, MOSSY_COBBLESTONE});

    public EnumStorage<Material> Cobblestones() {
        return COBBLESTONES_E;
    }
    //endregion

    //region Netherbricks
    private final EnumStorage<Material> NETHERBRICKS_E = new EnumStorage<>(new Material[]{NETHER_BRICKS, RED_NETHER_BRICKS});

    public EnumStorage<Material> Netherbricks() {
        return NETHERBRICKS_E;
    }
    //endregion

    //region Stone Bricks
    private final EnumStorage<Material> STONE_BRICKS_E = new EnumStorage<>(new Material[]{STONE_BRICKS,
        CRACKED_STONE_BRICKS, MOSSY_STONE_BRICKS, CHISELED_STONE_BRICKS});

    public EnumStorage<Material> Stonebricks() {
        return STONE_BRICKS_E;
    }

    //endregion
    //region Quartz
    private final EnumStorage<Material> QUARTZ_E = new EnumStorage<>(new Material[]{QUARTZ_BLOCK, CHISELED_QUARTZ_BLOCK,
        QUARTZ_PILLAR});

    public EnumStorage<Material> Quartz() {
        return QUARTZ_E;
    }
    //endregion

    //region Polished Stones
    private final EnumStorage<Material> POLISHED_STONES_E = new EnumStorage<>(new Material[]{POLISHED_ANDESITE,
        POLISHED_DIORITE, POLISHED_GRANITE});

    public EnumStorage<Material> PolishedStones() {
        return POLISHED_STONES_E;
    }
    //endregion

    //region Prismarines
    private final EnumStorage<Material> PRISMARINES_E = new EnumStorage<>(new Material[]{PRISMARINE,
        PRISMARINE_BRICKS, DARK_PRISMARINE});

    public EnumStorage<Material> Prismarines() {
        return PRISMARINES_E;
    }
    //endregion

    //region Endstones
    private final EnumStorage<Material> ENDSTONES_E = new EnumStorage<>(new Material[]{END_STONE, END_STONE_BRICKS});

    public EnumStorage<Material> Endstones() {
        return ENDSTONES_E;
    }
    //endregion

    //region Purpurs
    private final EnumStorage<Material> PURPURS_E = new EnumStorage<>(new Material[]{PURPUR_BLOCK, PURPUR_PILLAR});

    public EnumStorage<Material> Purpurs() {
        return PURPURS_E;
    }
    //endregion

    //region Sandstones
    private final EnumStorage<Material> SANDSTONES_E = new EnumStorage<>(new Material[]{SANDSTONE, CUT_SANDSTONE,
        CHISELED_SANDSTONE, SMOOTH_SANDSTONE, RED_SANDSTONE, CUT_RED_SANDSTONE, CHISELED_RED_SANDSTONE,
        SMOOTH_RED_SANDSTONE});

    public EnumStorage<Material> Sandstones() {
        return SANDSTONES_E;
    }
    //endregion

    //region Stone Slabs
    private final EnumStorage<Material> STONE_SLABS_E = new EnumStorage<>(new Material[]{STONE_SLAB});

    public EnumStorage<Material> StoneSlabs() {
        return STONE_SLABS_E;
    }
    //endregion

    //region Sandstone Slabs
    private final EnumStorage<Material> SANDSTONE_SLABS_E = new EnumStorage<>(new Material[]{SANDSTONE_SLAB,
        RED_SANDSTONE_SLAB});

    public EnumStorage<Material> SandstoneSlabs() {
        return SANDSTONE_SLABS_E;
    }
    //endregion

    //region Stone Brick Slabs
    private final EnumStorage<Material> STONE_BRICK_SLABS_E = new EnumStorage<>(new Material[]{STONE_BRICK_SLAB});

    public EnumStorage<Material> StoneBrickSlabs() {
        return STONE_BRICK_SLABS_E;
    }
    //endregion

    //region Cobblestone Slabs
    private final EnumStorage<Material> COBBLESTONE_SLABS_E = new EnumStorage<>(new Material[]{COBBLESTONE_SLAB});

    public EnumStorage<Material> CobblestoneSlabs() {
        return COBBLESTONE_SLABS_E;
    }
    //endregion

    //region Quartz Slabs
    private final EnumStorage<Material> QUARTZ_SLABS_E = new EnumStorage<>(new Material[]{QUARTZ_SLAB});

    public EnumStorage<Material> QuartzSlabs() {
        return QUARTZ_SLABS_E;
    }
    //endregion

    //region Nether Brick Slabs
    private final EnumStorage<Material> NETHER_BRICK_SLABS_E = new EnumStorage<>(new Material[]{NETHER_BRICK_SLAB});

    public EnumStorage<Material> NetherBrickSlabs() {
        return NETHER_BRICK_SLABS_E;
    }
    //endregion

    //region Prismarine Slabs
    private final EnumStorage<Material> PRISMARINE_SLABS_E = new EnumStorage<>(new Material[]{PRISMARINE_SLAB,
        PRISMARINE_BRICK_SLAB, DARK_PRISMARINE_SLAB});

    public EnumStorage<Material> PrismarineSlabs() {
        return PRISMARINE_SLABS_E;
    }
    //endregion

    //region Stone Stairs
    private final EnumStorage<Material> STONE_STAIRS_E = new EnumStorage<>(new Material[]{});

    public EnumStorage<Material> StoneStairs() {
        return STONE_STAIRS_E;
    }
    //endregion

    //region Sandstone Stairs
    private final EnumStorage<Material> SANDSTONE_STAIRS_E = new EnumStorage<>(new Material[]{SANDSTONE_STAIRS,
        RED_SANDSTONE_STAIRS});

    public EnumStorage<Material> SandstoneStairs() {
        return SANDSTONE_STAIRS_E;
    }
    //endregion

    //region Stone Brick Stairs
    private final EnumStorage<Material> STONE_BRICK_STAIRS_E = new EnumStorage<>(new Material[]{STONE_BRICK_STAIRS});

    public EnumStorage<Material> StoneBrickStairs() {
        return STONE_BRICK_STAIRS_E;
    }
    //endregion

    //region Cobblestone Stairs
    private final EnumStorage<Material> COBBLESTONE_STAIRS_E = new EnumStorage<>(new Material[]{COBBLESTONE_STAIRS});

    public EnumStorage<Material> CobblestoneStairs() {
        return COBBLESTONE_STAIRS_E;
    }
    //endregion

    //region Quartz Stairs
    private final EnumStorage<Material> QUARTZ_STAIRS_E = new EnumStorage<>(new Material[]{QUARTZ_STAIRS});

    public EnumStorage<Material> QuartzStairs() {
        return QUARTZ_STAIRS_E;
    }
    //endregion

    //region Nether Brick Stairs
    private final EnumStorage<Material> NETHER_BRICK_STAIRS_E = new EnumStorage<>(new Material[]{NETHER_BRICK_STAIRS});

    public EnumStorage<Material> NetherBrickStairs() {
        return NETHER_BRICK_STAIRS_E;
    }
    //endregion

    //region Prismarine Stairs
    private final EnumStorage<Material> PRISMARINE_STAIRS_E = new EnumStorage<>(new Material[]{PRISMARINE_STAIRS,
        PRISMARINE_BRICK_STAIRS, DARK_PRISMARINE_STAIRS});

    public EnumStorage<Material> PrismarineStairs() {
        return PRISMARINE_STAIRS_E;
    }
    //endregion

    //region Stone Walls
    private final EnumStorage<Material> STONE_WALLS_E = new EnumStorage<>(new Material[]{});

    public EnumStorage<Material> StoneWalls() {
        return STONE_WALLS_E;
    }
    //endregion

    //region Stone Brick Walls
    private final EnumStorage<Material> STONE_BRICK_WALLS_E = new EnumStorage<>(new Material[]{});

    public EnumStorage<Material> StoneBrickWalls() {
        return STONE_BRICK_WALLS_E;
    }
    //endregion

    //region Cobblestone Walls
    private final EnumStorage<Material> COBBLESTONE_WALLS_E = new EnumStorage<>(new Material[]{COBBLESTONE_WALL,
        MOSSY_COBBLESTONE_WALL});

    public EnumStorage<Material> CobblestoneWalls() {
        return COBBLESTONE_WALLS_E;
    }
    //endregion

    //region Sandstone Walls
    private final EnumStorage<Material> SANDSTONE_WALLS_E = new EnumStorage<>(new Material[]{});

    public EnumStorage<Material> SandstoneWalls() {
        return SANDSTONE_WALLS_E;
    }
    //endregion

    //region Nether Brick Walls
    private final EnumStorage<Material> NETHER_BRICK_WALLS_E = new EnumStorage<>(new Material[]{});

    public EnumStorage<Material> NetherBrickWalls() {
        return NETHER_BRICK_WALLS_E;
    }
    //endregion

    //endregion
    //region Block Categories
    //region Unbreakable Blocks
    private final EnumStorage<Material> UNBREAKABLE_BLOCKS_E = new EnumStorage<>(new Material[]{BARRIER, BEDROCK,
        BUBBLE_COLUMN, DRAGON_BREATH, DRAGON_EGG, END_CRYSTAL, END_GATEWAY, END_PORTAL, END_PORTAL_FRAME, LAVA,
        STRUCTURE_VOID, STRUCTURE_BLOCK, WATER, PISTON_HEAD, MOVING_PISTON}, Airs(), CommandBlocks());

    public EnumStorage<Material> UnbreakableBlocks() {
        return UNBREAKABLE_BLOCKS_E;
    }
    //endregion

    //region Storage Blocks
    private final EnumStorage<Material> STORAGE_BLOCKS_E = new EnumStorage<>(new Material[]{DISPENSER, SPAWNER,
        CHEST, FURNACE, JUKEBOX, ENDER_CHEST, BEACON, TRAPPED_CHEST, HOPPER, DROPPER, BREWING_STAND, ANVIL},
            ShulkerBoxes(), CommandBlocks());

    public EnumStorage<Material> StorageBlocks() {
        return STORAGE_BLOCKS_E;
    }

    //endregion
    //region Interactable Blocks
    private final EnumStorage<Material> INTERACTABLE_BLOCKS_E = new EnumStorage<>(new Material[]{
        NOTE_BLOCK, CRAFTING_TABLE, LEVER, REPEATER, ENCHANTING_TABLE, COMPARATOR, DAYLIGHT_DETECTOR, OBSERVER},
            Beds(),
            Doors(),
            Trapdoors(),
            FenceGates(),
            CommandBlocks(),
            Buttons(),
            ShulkerBoxes(),
            StorageBlocks());

    public EnumStorage<Material> InteractableBlocks() {
        return INTERACTABLE_BLOCKS_E;
    }
    //endregion

    //endregion
    //region Enchantment Enum Storage
    //region Terraformer Materials
    private final EnumStorage<Material> TERRAFORMER_MATERIALS_E = new EnumStorage<>(new Material[]{STONE, GRASS_BLOCK,
        DIRT, COBBLESTONE, SAND, GRAVEL, SANDSTONE, BRICK, TNT, BOOKSHELF, MOSSY_COBBLESTONE, ICE, SNOW_BLOCK, CLAY,
        NETHERRACK, SOUL_SAND, STONE_BRICKS, MYCELIUM, NETHER_BRICK, END_STONE, EMERALD_ORE, QUARTZ_BLOCK, SLIME_BLOCK,
        PRISMARINE, PACKED_ICE, RED_SANDSTONE}, Ores(), Terracottas(), GlazedTerracottas(), Wools(), Woods(), Planks(),
            StrippedLogs(), Logs(), Concretes(), ConcretePowders(), StainedGlass(), StrippedWoods());

    public EnumStorage<Material> TerraformerMaterials() {
        return TERRAFORMER_MATERIALS_E;
    }
    //endregion

    //region Lumber Whitelist
    private final EnumStorage<Material> LUMBER_WHITELIST_E = new EnumStorage<>(new Material[]{
        DIRT, GRASS, VINE, SNOW, COCOA, GRAVEL, STONE, DIORITE, GRANITE, ANDESITE, WATER, LAVA, SAND, BROWN_MUSHROOM,
        RED_MUSHROOM, MOSSY_COBBLESTONE, CLAY, BROWN_MUSHROOM, RED_MUSHROOM, MYCELIUM, TORCH, SUGAR_CANE, GRASS_BLOCK,
        PODZOL, FERN, GRASS, MELON, PUMPKIN}, TrunkBlocks(), Leaves(), SmallFlowers(), LargeFlowers(), Saplings(),
            Airs(), DeadlyPlants());

    public EnumStorage<Material> LumberWhitelist() {
        return LUMBER_WHITELIST_E;
    }
    //endregion

    //region Transformation Entity Types
    private final EnumStorage<EntityType> TRANSFORMATION_ENTITY_TYPES_FROM_E = new EnumStorage<>(new EntityType[]{
        HUSK, WITCH, EntityType.COD, PHANTOM, HORSE, SKELETON, EntityType.CHICKEN, SQUID, OCELOT, POLAR_BEAR, COW, PIG,
        SPIDER, SLIME, GUARDIAN, ENDERMITE, SKELETON_HORSE, EntityType.RABBIT, SHULKER, SNOWMAN, DROWNED, VINDICATOR,
        EntityType.SALMON, BLAZE, DONKEY, STRAY, PARROT, DOLPHIN, WOLF, SHEEP, MUSHROOM_COW, PIG_ZOMBIE, CAVE_SPIDER,
        MAGMA_CUBE, ELDER_GUARDIAN, SILVERFISH, ZOMBIE_HORSE, EntityType.RABBIT, ENDERMAN, IRON_GOLEM, ZOMBIE, EVOKER,
        PUFFERFISH, VEX, MULE, WITHER_SKELETON, BAT, TURTLE, ZOMBIE_VILLAGER, VILLAGER, EntityType.TROPICAL_FISH, GHAST,
        LLAMA, CREEPER});

    public EnumStorage<EntityType> TransformationEntityTypesFrom() {
        return TRANSFORMATION_ENTITY_TYPES_FROM_E;
    }

    private final EnumStorage<EntityType> TRANSFORMATION_ENTITY_TYPES_TO_E = new EnumStorage<>(new EntityType[]{
        DROWNED, VINDICATOR, EntityType.SALMON, BLAZE, DONKEY, STRAY, PARROT, DOLPHIN, WOLF, SHEEP, MUSHROOM_COW,
        PIG_ZOMBIE, CAVE_SPIDER, MAGMA_CUBE, ELDER_GUARDIAN, SILVERFISH, ZOMBIE_HORSE, EntityType.RABBIT, ENDERMAN,
        IRON_GOLEM, ZOMBIE, EVOKER, PUFFERFISH, VEX, MULE, WITHER_SKELETON, BAT, TURTLE, OCELOT, POLAR_BEAR, COW, PIG,
        SPIDER, SLIME, GUARDIAN, ENDERMITE, SKELETON_HORSE, EntityType.RABBIT, SHULKER, SNOWMAN, ZOMBIE_VILLAGER,
        VILLAGER, EntityType.TROPICAL_FISH, GHAST, LLAMA, SKELETON, EntityType.CHICKEN, SQUID, HUSK, WITCH,
        EntityType.COD, PHANTOM, HORSE, CREEPER});

    public EnumStorage<EntityType> TransformationEntityTypesTo() {
        return TRANSFORMATION_ENTITY_TYPES_TO_E;
    }

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
                Villager.Profession career = Villager.Profession.values()[rnd.nextInt(Villager.Profession.values().length)];
                ((Villager) newEnt).setProfession(career);
                //((Villager) newEnt).setCareer(career);
                break;
            case LLAMA:
                ((Llama) newEnt).setColor(Llama.Color.values()[rnd.nextInt(Llama.Color.values().length)]);
                break;
            case TROPICAL_FISH:
                ((TropicalFish) newEnt).setBodyColor(DyeColor.values()[rnd.nextInt(DyeColor.values().length)]);
                ((TropicalFish) newEnt).setPatternColor(DyeColor.values()[rnd.nextInt(DyeColor.values().length)]);
                ((TropicalFish) newEnt).setPattern(TropicalFish.Pattern.values()[rnd.nextInt(TropicalFish.Pattern.values().length)]);
                break;
            case PARROT:
                ((Parrot) newEnt).setVariant(Parrot.Variant.values()[rnd.nextInt(Parrot.Variant.values().length)]);
                break;
            case SHEEP:
                ((Sheep) newEnt).setColor(DyeColor.values()[rnd.nextInt(DyeColor.values().length)]);
                break;
            case CREEPER:
                ((Creeper) newEnt).setPowered(!((Creeper) ent).isPowered());
                break;
            case MUSHROOM_COW:
                ((MushroomCow) newEnt).setVariant(MushroomCow.Variant.values()[rnd.nextInt(MushroomCow.Variant.values().length)]);
                break;
        }
        newEnt.setCustomName(ent.getCustomName());
        newEnt.setCustomNameVisible(ent.isCustomNameVisible());
        return ent;
    }
    //endregion

    //region Fire Raw
    private final EnumStorage<Material> FIRE_RAW_E = new EnumStorage<>(new Material[]{STONE, DIORITE, ANDESITE, GRANITE,
        IRON_ORE, GOLD_ORE, COBBLESTONE, MOSSY_COBBLESTONE, NETHERRACK, STONE_BRICKS}, Terracottas());

    public EnumStorage<Material> FireRaw() {
        return FIRE_RAW_E;
    }

    //endregion
    //region Fire Cooked
    private final EnumStorage<Material> FIRE_COOKED_E = new EnumStorage<>(new Material[]{STONE_BRICKS, POLISHED_DIORITE,
        POLISHED_ANDESITE, POLISHED_GRANITE, IRON_INGOT, GOLD_INGOT, STONE, MOSSY_STONE_BRICKS, NETHER_BRICK,
        CRACKED_STONE_BRICKS}, GlazedTerracottas());

    public EnumStorage<Material> FireCooked() {
        return FIRE_COOKED_E;
    }
    //endregion

    //region Shred Picks
    private final EnumStorage<Material> SHRED_PICKS_E = new EnumStorage<>(new Material[]{STONE, GRANITE,
        ANDESITE, DIORITE, NETHERRACK, GLOWSTONE, SANDSTONE, RED_SANDSTONE, ICE, PACKED_ICE, BLUE_ICE}, Ores(),
            Terracottas());

    public EnumStorage<Material> ShredPicks() {
        return SHRED_PICKS_E;
    }

    //endregion
    //region Shred Shovels
    private final EnumStorage<Material> SHRED_SHOVELS_E = new EnumStorage<>(new Material[]{GRASS_BLOCK, PODZOL, DIRT,
        MYCELIUM, SOUL_SAND, GRAVEL, SOUL_SAND, CLAY}, Sands());

    public EnumStorage<Material> ShredShovels() {
        return SHRED_SHOVELS_E;
    }
    //endregion

    //region Persephone Crops
    private final EnumStorage<Material> PERSEPHONE_CROPS_E = new EnumStorage<>(new Material[]{WHEAT, POTATO, CARROT,
        BEETROOT, NETHER_WART, SOUL_SAND, FARMLAND});

    public EnumStorage<Material> PersephoneCrops() {
        return PERSEPHONE_CROPS_E;
    }

    //endregion
    //region Dry Biomes
    private final EnumStorage<Biome> DRY_BIOMES_E = new EnumStorage<>(new Biome[]{Biome.DESERT, Biome.FROZEN_OCEAN,
        Biome.FROZEN_RIVER, Biome.SNOWY_TUNDRA, Biome.SNOWY_MOUNTAINS, Biome.DESERT_HILLS, Biome.SNOWY_BEACH,
        Biome.SNOWY_TAIGA, Biome.SNOWY_TAIGA_HILLS, Biome.SAVANNA, Biome.SAVANNA_PLATEAU, Biome.BADLANDS,
        Biome.WOODED_BADLANDS_PLATEAU, Biome.BADLANDS_PLATEAU, Biome.DESERT_LAKES, Biome.ICE_SPIKES,
        Biome.SNOWY_TAIGA_MOUNTAINS, Biome.SHATTERED_SAVANNA, Biome.SHATTERED_SAVANNA_PLATEAU, Biome.ERODED_BADLANDS,
        Biome.MODIFIED_WOODED_BADLANDS_PLATEAU, Biome.MODIFIED_BADLANDS_PLATEAU});

    public EnumStorage<Biome> DryBiomes() {
        return DRY_BIOMES_E;
    }
    //endregion

    //region Potion Potions
    private final EnumStorage<PotionEffectType> POTION_POTIONS_E = new EnumStorage<>(new PotionEffectType[]{ABSORPTION,
        DAMAGE_RESISTANCE, FIRE_RESISTANCE, SPEED, JUMP, INVISIBILITY, INCREASE_DAMAGE, HEALTH_BOOST, HEAL,
        REGENERATION, NIGHT_VISION, SATURATION, FAST_DIGGING, WATER_BREATHING, DOLPHINS_GRACE});

    public EnumStorage<PotionEffectType> PotionPotions() {
        return POTION_POTIONS_E;
    }
    //endregion

    //endregion
    //endregion
    //region Gluttony
    //region Gluttony Food Levels
    private final int[] GLUTTONY_FOOD_LEVELS = {4, 5, 1, 6, 5, 3, 1, 6, 5, 6, 8, 5, 6, 2, 1, 2, 6, 8, 10, 8};

    public int[] GluttonyFoodLevels() {
        return GLUTTONY_FOOD_LEVELS;
    }
    //endregion

    //region Gluttony Saturations
    private final double[] GLUTTONY_SATURATIONS = {2.4, 6, 1.2, 7.2, 6, 3.6, 0.2, 7.2, 6, 9.6, 12.8, 6, 9.6, 0.4, 0.6,
        1.2, 7.2, 4.8, 12, 12.8};

    public double[] GluttonySaturations() {
        return GLUTTONY_SATURATIONS;
    }
    //endregion

    //region Gluttony Food Items
    private final Material[] GLUTTONY_FOOD_ITEMS = new Material[]{
        APPLE, BAKED_POTATO, BEETROOT, BEETROOT_SOUP, BREAD, CARROT, TROPICAL_FISH, COOKED_CHICKEN, COOKED_COD,
        COOKED_MUTTON, COOKED_PORKCHOP, COOKED_RABBIT, COOKED_SALMON, COOKIE, DRIED_KELP, MELON_SLICE, MUSHROOM_STEW,
        PUMPKIN_PIE, RABBIT_STEW, COOKED_BEEF};

    public Material[] GluttonyFoodItems() {
        return GLUTTONY_FOOD_ITEMS;
    }
    //endregion

    //endregion
    public static CompatibilityAdapter getInstance() {
        return INSTANCE;
    }

    // Removes the given ItemStack's durability by the given 'damage' and then sets the item direction the given
    // players hand.
    //      This also takes into account the unbreaking enchantment
    public static void damageTool(Player player, int damage, boolean handUsed) {
        if (!player.getGameMode().equals(GameMode.CREATIVE)) {
            ItemStack hand
                    = handUsed ? player.getInventory().getItemInMainHand() : player.getInventory().getItemInOffHand();
            for (int i = 0; i < damage; i++) {
                if (RND.nextInt(100) <= (100 / (hand.getEnchantmentLevel(org.bukkit.enchantments.Enchantment.DURABILITY) + 1))) {
                    setDamage(hand, getDamage(hand) + 1);
                }
            }
            if (handUsed) {
                player.getInventory().setItemInMainHand(
                        getDamage(hand) > hand.getType().getMaxDurability() ? new ItemStack(AIR) : hand);
            } else {
                player.getInventory().setItemInOffHand(
                        getDamage(hand) > hand.getType().getMaxDurability() ? new ItemStack(AIR) : hand);
            }
        }
    }

    // Displays a particle with the given data
    public static void display(Location loc, Particle particle, int amount, double speed, double xO, double yO,
            double zO) {
        loc.getWorld().spawnParticle(particle, loc.getX(), loc.getY(), loc.getZ(), amount, (float) xO, (float) yO,
                (float) zO, (float) speed);
    }

    // Removes the given ItemStack's durability by the given 'damage'
    //      This also takes into account the unbreaking enchantment
    public static void addUnbreaking(Player player, ItemStack is, int damage) {
        if (!player.getGameMode().equals(GameMode.CREATIVE)) {
            for (int i = 0; i < damage; i++) {
                if (RND.nextInt(100) <= (100 / (is.getEnchantmentLevel(org.bukkit.enchantments.Enchantment.DURABILITY) + 1))) {
                    setDamage(is, getDamage(is) + 1);
                }
            }
        }
    }

    public static void setDamage(ItemStack is, int damage) {
        if (is.getItemMeta() instanceof org.bukkit.inventory.meta.Damageable) {
            org.bukkit.inventory.meta.Damageable dm = ((org.bukkit.inventory.meta.Damageable) is.getItemMeta());
            dm.setDamage(damage);
            is.setItemMeta((ItemMeta) dm);
        }
    }

    public static int getDamage(ItemStack is) {
        if (is.getItemMeta() instanceof org.bukkit.inventory.meta.Damageable) {
            org.bukkit.inventory.meta.Damageable dm = ((org.bukkit.inventory.meta.Damageable) is.getItemMeta());
            return dm.getDamage();
        }
        return 0;
    }

    protected CompatibilityAdapter() {
    }

    public void collectXP(Player player, int amount) {
        player.giveExp(amount);
    }

    public boolean breakBlockNMS(Block block, Player player) {
        BlockBreakEvent evt = new BlockBreakEvent(block, player);
        Bukkit.getPluginManager().callEvent(evt);
        if (!evt.isCancelled()) {
            block.breakNaturally(player.getInventory().getItemInMainHand());
            damageTool(player, 1, true);
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
     * @param data the block data to set for the block, if allowed
     *
     * @return true if the block placement has been successful
     */
    public boolean placeBlock(Block blockPlaced, Player player, Material mat, BlockData data) {
        Block blockAgainst = blockPlaced.getRelative((blockPlaced.getY() == 0) ? BlockFace.UP : BlockFace.DOWN);
        ItemStack itemHeld = new ItemStack(mat);
        BlockPlaceEvent placeEvent
                = new BlockPlaceEvent(blockPlaced, blockPlaced.getState(), blockAgainst, itemHeld, player, true,
                        EquipmentSlot.HAND);

        Bukkit.getPluginManager().callEvent(placeEvent);
        if (!placeEvent.isCancelled()) {
            blockPlaced.setType(mat);
            if (data != null) {
                blockPlaced.setBlockData(data);
            }
            return true;
        }
        return false;
    }

    public boolean placeBlock(Block blockPlaced, Player player, ItemStack is) {
        return placeBlock(blockPlaced, player, is.getType(), (BlockData) is.getData());
    }

    public boolean attackEntity(LivingEntity target, Player attacker, double damage) {
        EntityDamageByEntityEvent damageEvent
                = new EntityDamageByEntityEvent(attacker, target, DamageCause.ENTITY_ATTACK, damage);
        Bukkit.getPluginManager().callEvent(damageEvent);
        if (damage == 0) {
            return !damageEvent.isCancelled();
        }
        if (!damageEvent.isCancelled()) {
            target.damage(damage, attacker);
            target.setLastDamageCause(damageEvent);
            //damageTool(attacker, 1, true);
            return true;
        }
        return false;
    }

    public boolean shearEntityNMS(Entity target, Player player, boolean mainHand) {
        if ((target instanceof Sheep && !((Sheep) target).isSheared()) || target instanceof MushroomCow) {
            PlayerShearEntityEvent evt = new PlayerShearEntityEvent(player, target);
            Bukkit.getPluginManager().callEvent(evt);
            if (!evt.isCancelled()) {
                if (target instanceof Sheep) {
                    Sheep sheep = (Sheep) target;
                    sheep.getLocation().getWorld().dropItem(sheep.getLocation(),
                            new ItemStack(Wools().get(sheep.getColor().ordinal()), RND.nextInt(3) + 1));
                    ((Sheep) target).setSheared(true);

                    // TODO: Apply damage to tool
                } else if (target instanceof MushroomCow) {
                    MushroomCow cow = (MushroomCow) target;
                    // TODO: DO
                }
                return true;
            }
        }
        return false;
    }

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
        ItemStack stack = new ItemStack(state.getType(), 1);
        from.setType(AIR);
        BlockPlaceEvent placeEvent = new BlockPlaceEvent(to, to.getRelative(face.getOppositeFace()).getState(),
                to.getRelative(face.getOppositeFace()), stack, player, true,
                EquipmentSlot.HAND);
        Bukkit.getPluginManager().callEvent(placeEvent);
        if (placeEvent.isCancelled()) {
            from.getWorld().dropItem(from.getLocation(), stack);
            return true;
        }
        to.setType(state.getType());
        return true;
    }

    public boolean igniteEntity(Entity target, Player player, int duration) {
        EntityCombustByEntityEvent evt = new EntityCombustByEntityEvent(target, player, duration);
        Bukkit.getPluginManager().callEvent(evt);
        if (!evt.isCancelled()) {
            target.setFireTicks(duration);
            return true;
        }
        return false;
    }

    public boolean damagePlayer(Player player, double damage, DamageCause cause) {
        EntityDamageEvent evt = new EntityDamageEvent(player, cause, damage);
        Bukkit.getPluginManager().callEvent(evt);
        if (damage == 0) {
            return !evt.isCancelled();
        }
        if (!evt.isCancelled()) {
            player.setLastDamageCause(evt);
            player.damage(damage);
            return true;
        }
        return false;
    }

    public boolean explodeCreeper(Creeper c, boolean damage) {
        float power;
        Location l = c.getLocation();
        if (c.isPowered()) {
            power = 6f;
        } else {
            power = 3.1f;
        }
        if (damage) {
            c.getWorld().createExplosion(l, power);
        } else {
            c.getWorld().createExplosion(l.getX(), l.getY(), l.getZ(), power, false, false);
        }
        c.remove();

        return true;
    }

    public boolean formBlock(Block block, Material mat, Player player) {
        BlockState bs = block.getState();
        bs.setType(mat);
        EntityBlockFormEvent evt = new EntityBlockFormEvent(player, block, bs);
        Bukkit.getPluginManager().callEvent(evt);
        if (!evt.isCancelled()) {
            block.setType(mat);
            return true;
        }
        return false;
    }

    public boolean showShulker(Block blockToHighlight, int entityId, Player player) {
        // This cannot be done without NMS
        return false;
    }

    public boolean hideShulker(int entityId, Player player) {
        // This cannot be done without NMS
        return false;
    }

    public Entity spawnGuardian(Location loc, boolean elderGuardian) {
        return loc.getWorld().spawnEntity(loc, elderGuardian ? EntityType.ELDER_GUARDIAN : EntityType.GUARDIAN);
    }

    public boolean isZombie(Entity e) {
        return e.getType() == EntityType.ZOMBIE || e.getType() == EntityType.ZOMBIE_VILLAGER || e.getType() == EntityType.HUSK;
    }

    public boolean isBlockSafeToBreak(Block b) {
        Material mat = b.getType();
        return mat.isSolid() && !b.isLiquid() && !InteractableBlocks().contains(mat) && !UnbreakableBlocks().contains(mat)
                && !StorageBlocks().contains(mat);
    }

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

    public boolean pickBerries(Block berryBlock, Player player) {
        BlockData data = berryBlock.getBlockData();
        Ageable a = (Ageable) data;
        if (a.getAge() > 1) { // Age of ripe Berries
            PlayerInteractEvent pie = new PlayerInteractEvent(player, Action.RIGHT_CLICK_BLOCK, player.getInventory().getItemInMainHand(), berryBlock, player.getFacing());
            Bukkit.getPluginManager().callEvent(pie);
            if (!pie.isCancelled()) {
                int numDropped = (a.getAge() == 3 ? 2 : 1) + (RND.nextBoolean() ? 1 : 0); // Natural drop rate. Age 2 -> 1-2 berries, Age 3 -> 2-3 berries
                a.setAge(1); // Picked adult berry bush
                berryBlock.setBlockData(a);
                berryBlock.getWorld().dropItem(berryBlock.getLocation(),
                        new ItemStack(Material.SWEET_BERRIES, numDropped));
                return true;
            }
        }
        return false;
    }
}
