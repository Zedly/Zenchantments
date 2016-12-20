package zedly.zenchantments;

import java.util.*;
import org.bukkit.*;
import static org.bukkit.Material.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;

public class Storage {

    // Instance of the Zenchantments plugin to be used by the rest of the classes
    public static Zenchantments zenchantments;

    // Current Zenchantments version
    public static String version;

    // Determines if falling entities from Anthropomorphism should fall up or down
    public static boolean fallBool = false;

    // Random object
    public static final Random rnd = new Random();

    // Map of tracer arrows to their expected accuracy
    public static final Map<Arrow, Integer> tracer = new HashMap<>();

    // The players using glide and their most recent Y coordinate
    public static final Map<Player, Double> sneakGlide = new HashMap<>();

    // Guardians from the Mystery Fish enchantment and the player they should move towards
    public static final Map<Guardian, Player> guardianMove = new HashMap<>();

    // Time at which a later enchantment was fired; this is used to prevent double firing when clicking an entity
    public static final Map<Player, Long> laserTimes = new HashMap<>();

    // Entities an advanced arrow has damaged or killed
    public static final Map<Entity, AdvancedArrow> killedEntities = new HashMap<>();

    // Map of players who use pierce and what mode they are currently using
    public static final Map<Player, Integer> pierceModes = new HashMap<>();

    // The falling blocks from the Anthropomorphism enchantment that are idle, staying within the relative region
    public static final Map<FallingBlock, Entity> idleBlocks = new HashMap<>();

    // The falling blocks from the Anthropomorphism enchantment that are attacking, moving towards a set target
    public static final Map<FallingBlock, Double> attackBlocks = new HashMap<>();

    // Players currently using the Anthropomorphism enchantment
    public static final List<Entity> anthVortex = new ArrayList<>();

    // Players mapped to a set of enchantment strings to prevent infinite recursion of certain enchantment events
    public static final Map<Player, Set<String>> duringEvents = new HashMap<>();

    // Locations where Grab has been used on a block and are waiting for the Watcher to handle their teleportation
    public static final Map<Block, Location> grabLocs = new HashMap<>();

    // Locations where Vortex has been used on a block and are waiting for the Watcher to handle their teleportation
    public static final Map<Block, Location> vortexLocs = new HashMap<>();

    // Blocks spawned from the Fire Walker enchantment
    public static final Map<Location, Long> fireLocs = new HashMap<>();

    // Blocks spawned from the Water Walker enchantment
    public static final Map<Location, Long> waterLocs = new HashMap<>();

    // Entites that are being tested by Utilities' canDamage method to prevent infinite recursion
    public static final Set<Entity> damagingPlayer = new HashSet<>();

    // Arrows mapped to different advanced arrow effects, to be used by the Arrow Watcher to perform these effects
    public static final Map<Entity, Set<AdvancedArrow>> advancedProjectiles = new HashMap<>();

    // Players that have been affected by the Toxic enchantment who cannot currently eat
    public static final Map<Player, Integer> hungerPlayers = new HashMap<>();

    // Locations of black holes from the singularity enchantment and whether or not they are attracting or repelling
    public static final Map<Location, Boolean> blackholes = new HashMap<>();

    // Players and the most recent block they are moving with the Haul enchantment
    public static final Map<Player, Block> haulBlocks = new HashMap<>();

    // Players and how long it has been since they last move a block with haul
    public static final Map<Player, Integer> haulBlockDelay = new HashMap<>();

    // Block IDs that are not to be used within a player's inventory
    public static final Material[] badItems = {AIR, WATER, STATIONARY_WATER, 
        LAVA, STATIONARY_LAVA, BED_BLOCK, PISTON_EXTENSION, PISTON_MOVING_PIECE, DOUBLE_STEP,
        FIRE, REDSTONE_WIRE, CROPS, SIGN_POST, WOODEN_DOOR, WALL_SIGN, IRON_DOOR_BLOCK, 
        GLOWING_REDSTONE_ORE, REDSTONE_TORCH_OFF, SUGAR_CANE_BLOCK, PORTAL, CAKE_BLOCK, 
        DIODE_BLOCK_OFF, DIODE_BLOCK_ON, PUMPKIN_STEM, MELON_STEM, NETHER_WARTS, BREWING_STAND, 
        CAULDRON, ENDER_PORTAL, REDSTONE_LAMP_ON, WOOD_DOUBLE_STEP, COCOA, TRIPWIRE, 
        FLOWER_POT, CARROT, POTATO, SKULL, REDSTONE_COMPARATOR_OFF, REDSTONE_COMPARATOR_ON, 
        STANDING_BANNER, WALL_BANNER, DAYLIGHT_DETECTOR_INVERTED, DOUBLE_STONE_SLAB2, SPRUCE_DOOR, 
        BIRCH_DOOR, JUNGLE_DOOR, ACACIA_DOOR, DARK_OAK_DOOR};

    // Block IDs that are not to be used in events
    public static final Material[] badBlocks = {SAPLING, BEDROCK, DISPENSER, NOTE_BLOCK, 
        PISTON_STICKY_BASE, LONG_GRASS, DEAD_BUSH, PISTON_BASE, YELLOW_FLOWER, RED_ROSE, 
        BROWN_MUSHROOM, RED_MUSHROOM, OBSIDIAN, MOB_SPAWNER, CHEST, WORKBENCH, FURNACE, 
        BURNING_FURNACE, SIGN_POST, LADDER, WALL_SIGN, LEVER, STONE_BUTTON, CACTUS, 
        SUGAR_CANE_BLOCK, JUKEBOX, PORTAL, VINE, FENCE_GATE, WATER_LILY, ENDER_PORTAL, 
        ENDER_PORTAL_FRAME, ENDER_CHEST, TRIPWIRE_HOOK, WOOD_BUTTON, ANVIL, TRAPPED_CHEST,
        DAYLIGHT_DETECTOR, DROPPER, BARRIER, BARRIER, DOUBLE_PLANT, DAYLIGHT_DETECTOR_INVERTED, 
        SPRUCE_FENCE_GATE, BIRCH_FENCE_GATE, JUNGLE_FENCE_GATE, DARK_OAK_FENCE_GATE, 
        ACACIA_FENCE_GATE, END_GATEWAY, STRUCTURE_BLOCK, SIGN, WOOD_DOOR, IRON_DOOR, BED, 
        SKULL_ITEM, SPRUCE_DOOR_ITEM, BIRCH_DOOR_ITEM, JUNGLE_DOOR_ITEM, ACACIA_DOOR_ITEM, 
        DARK_OAK_DOOR_ITEM};

    // The plugin Logo to be used in chat commands
    public static final String logo = ChatColor.BLUE + "[" + ChatColor.DARK_AQUA + "Zenchantments"
            + ChatColor.BLUE + "] " + ChatColor.AQUA;

    public static final Material ores[] = new Material[]{COAL_ORE, REDSTONE_ORE, DIAMOND_ORE, GOLD_ORE,
        IRON_ORE, LAPIS_ORE, GLOWSTONE, QUARTZ_ORE, EMERALD_ORE, GLOWING_REDSTONE_ORE};

}
