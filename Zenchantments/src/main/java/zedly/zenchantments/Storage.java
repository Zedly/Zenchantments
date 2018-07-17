package zedly.zenchantments;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import zedly.zenchantments.compatibility.CompatibilityAdapter;

import java.util.*;

public class Storage {

    // Instance of the Zenchantments plugin to be used by the rest of the classes
    public static Zenchantments zenchantments;

    // The plugin Logo to be used in chat commands
    public static final String logo = ChatColor.BLUE + "[" + ChatColor.DARK_AQUA + "Zenchantments"
            + ChatColor.BLUE + "] " + ChatColor.AQUA;
    
    // Current Zenchantments version
    public static String version = "";

    public static final CompatibilityAdapter COMPATIBILITY_ADAPTER;
    
    // Determines if falling entities from Anthropomorphism should fall up or down
    public static boolean fallBool = false;

    // Random object
    public static final Random rnd = new Random();

    // Map of tracer arrows to their expected accuracy
    public static final Map<Arrow, Integer> tracer = new HashMap<>();

    // The players using glide and their most recent Y coordinate
    public static final Map<Player, Double> sneakGlide = new HashMap<>();

    // Guardians from the Mystery Fish enchantment and the player they should move towards
    public static final Map<Entity, Player> guardianMove = new HashMap<>();

    // Time at which a later enchantment was fired; this is used to prevent double firing when clicking an entity
    public static final Map<Player, Long> laserTimes = new HashMap<>();

    // Entities an advanced arrow has damaged or killed
    public static final Map<Entity, AdvancedArrow> killedEntities = new HashMap<>();

    // Map of players who use pierce and what mode they are currently using
    public static final Map<Player, Integer> pierceModes = new HashMap<>();

    // The falling blocks from the Anthropomorphism enchantment that are idle, staying within the relative region
    public static final Map<FallingBlock, Entity> idleBlocks = new HashMap<>();

    // The falling blocks from the Anthropomorphism enchantment that are attacking, moving towards a set target
    public static final HashMap<FallingBlock, Double> attackBlocks = new HashMap<>();

    // Players currently using the Anthropomorphism enchantment
    public static final List<Entity> anthVortex = new ArrayList<>();

    // Entities affected by Rainbow Slam, protected against fall damage in order to deal damage as the attacker
    public static final Set<Entity> rainbowSlamNoFallEntities = new HashSet<>();

    // Locations where Grab has been used on a block and are waiting for the Watcher to handle their teleportation
    public static final Map<Block, Location> grabLocs = new HashMap<>();

    // Locations where Grab has been used on a block and are waiting for the Watcher to handle their teleportation
    public static final Set<Block> fireDropLocs = new HashSet<>();
    
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

    public static final BlockFace[] CARDINAL_BLOCK_FACES = {
        BlockFace.UP,
        BlockFace.DOWN,
        BlockFace.NORTH,
        BlockFace.EAST,
        BlockFace.SOUTH,
        BlockFace.WEST
    };
    
    public static final Material[] UNBREAKABLE_BLOCKS;

    public static final Material[] STORAGE_BLOCKS;

    public static final Material[] INTERACTABLE_BLOCKS;

    public static final Material[] ORES;

    public static final EntityType[] TRANSFORMATION_ENTITY_TYPES;
    
    static {
        String versionString = Bukkit.getServer().getClass().getPackage().getName();
        String nmsVersionString = versionString.substring(versionString.lastIndexOf('.') + 1);
        System.out.println("Zenchantments: Detected NMS version \"" + nmsVersionString + "\"");
        switch (nmsVersionString) {
            case "v1_12_R1":
                COMPATIBILITY_ADAPTER = zedly.zenchantments.compatibility.NMS_1_12_R1.getInstance();
                break;
            case "v1_11_R1":
                COMPATIBILITY_ADAPTER = zedly.zenchantments.compatibility.NMS_1_11_R1.getInstance();
                break;
            case "v1_10_R1":
                COMPATIBILITY_ADAPTER = zedly.zenchantments.compatibility.NMS_1_10_R1.getInstance();
                break;
            default:
                System.out.println("No compatible adapter available, falling back to Bukkit. Not everything will work!");
                COMPATIBILITY_ADAPTER = zedly.zenchantments.compatibility.CompatibilityAdapter.getInstance();
                break;
        }
        UNBREAKABLE_BLOCKS = COMPATIBILITY_ADAPTER.getUnbreakableBlocks();
        STORAGE_BLOCKS = COMPATIBILITY_ADAPTER.getStorageBlocks();
        INTERACTABLE_BLOCKS = COMPATIBILITY_ADAPTER.getInteractableBlocks();
        ORES = COMPATIBILITY_ADAPTER.getOres();
        TRANSFORMATION_ENTITY_TYPES = COMPATIBILITY_ADAPTER.getTransformationEntityTypes();
    }
}
