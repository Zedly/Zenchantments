package zedly.zenchantments;

import java.util.*;
import org.bukkit.*;
import static org.bukkit.Material.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
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

    // Blocks made to glow by the Reveal enchantment
    public static final HashMap<Block, Integer> glowingBlocks = new HashMap<>();

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

    public static final Material[] UNBREAKABLE_BLOCKS = {AIR, BEDROCK, WATER, STATIONARY_WATER,
        LAVA, STATIONARY_LAVA, PISTON_EXTENSION, PISTON_MOVING_PIECE, PORTAL, ENDER_PORTAL,
        ENDER_PORTAL_FRAME, DRAGON_EGG, BARRIER, END_GATEWAY, STRUCTURE_BLOCK};

    public static final Material[] STORAGE_BLOCKS = {DISPENSER, MOB_SPAWNER, CHEST, FURNACE,
        BURNING_FURNACE, JUKEBOX, ENDER_CHEST, COMMAND, BEACON, TRAPPED_CHEST, HOPPER, DROPPER,
        OBSERVER, PURPLE_SHULKER_BOX};

    public static final Material[] INTERACTABLE_BLOCKS = {
        DISPENSER, NOTE_BLOCK, BED_BLOCK, CHEST, WORKBENCH, FURNACE, BURNING_FURNACE,
        WOODEN_DOOR, LEVER, STONE_BUTTON, JUKEBOX, DIODE_BLOCK_OFF, DIODE_BLOCK_ON, TRAP_DOOR,
        FENCE_GATE, ENCHANTMENT_TABLE, BREWING_STAND, ENDER_CHEST, COMMAND, BEACON, WOOD_BUTTON,
        ANVIL, TRAPPED_CHEST, REDSTONE_COMPARATOR_OFF, REDSTONE_COMPARATOR_ON, DAYLIGHT_DETECTOR,
        HOPPER, DROPPER, SPRUCE_FENCE_GATE, BIRCH_FENCE_GATE, JUNGLE_FENCE_GATE, DARK_OAK_FENCE_GATE,
        ACACIA_FENCE_GATE, SPRUCE_DOOR, BIRCH_DOOR, JUNGLE_DOOR, ACACIA_DOOR, DARK_OAK_DOOR, OBSERVER,
        PURPLE_SHULKER_BOX, STRUCTURE_BLOCK};

    public static final BlockFace[] CARDINAL_BLOCK_FACES = {
        BlockFace.UP,
        BlockFace.DOWN,
        BlockFace.NORTH,
        BlockFace.EAST,
        BlockFace.SOUTH,
        BlockFace.WEST
    };

    // The plugin Logo to be used in chat commands
    public static final String logo = ChatColor.BLUE + "[" + ChatColor.DARK_AQUA + "Zenchantments"
            + ChatColor.BLUE + "] " + ChatColor.AQUA;

    public static final Material ores[] = new Material[]{COAL_ORE, REDSTONE_ORE, DIAMOND_ORE, GOLD_ORE,
        IRON_ORE, LAPIS_ORE, GLOWSTONE, QUARTZ_ORE, EMERALD_ORE, GLOWING_REDSTONE_ORE};

}
