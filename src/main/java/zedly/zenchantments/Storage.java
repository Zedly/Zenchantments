package zedly.zenchantments;

import java.util.*;
import org.bukkit.*;
import static org.bukkit.Material.COAL_ORE;
import static org.bukkit.Material.DIAMOND_ORE;
import static org.bukkit.Material.EMERALD_ORE;
import static org.bukkit.Material.GLOWING_REDSTONE_ORE;
import static org.bukkit.Material.GLOWSTONE;
import static org.bukkit.Material.GOLD_ORE;
import static org.bukkit.Material.IRON_ORE;
import static org.bukkit.Material.LAPIS_ORE;
import static org.bukkit.Material.QUARTZ_ORE;
import static org.bukkit.Material.REDSTONE_ORE;
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

    // Webs from the Web Arrow elemental arrow
    public static final Set<Block> webs = new HashSet<>();//

    // Entities an advanced arrow has damaged or killed
    public static final Map<Entity, AdvancedArrow> killedEntities = new HashMap<>();//

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
    public static final Integer[] badItems = new Integer[]{0, 8, 9, 10, 11, 26, 34, 36, 43, 51, 55, 59, 63, 64, 68, 71,
        74, 75, 83, 90, 92, 93, 94, 104, 105, 115, 117, 118, 119, 124, 125, 127, 132, 140, 141, 142, 144, 149, 150, 176,
        177, 178, 181, 193, 194, 195, 196, 197};

    // Block IDs that are not to be used in events
    public static final Integer[] badBlocks = {6, 7, 23, 25, 29, 31, 32, 33, 37, 38, 39, 40, 49, 52, 54, 58, 61, 62, 63, 65, 68,
        69, 77, 81, 83, 84, 90, 106, 107, 111, 119, 120, 130, 131, 143, 145, 146, 151, 158, 166, 166, 175, 178,
        183, 184, 185, 186, 187, 209, 255, 323, 324, 330, 355, 397, 427, 428, 429, 430, 431};

    // The plugin Logo to be used in chat commands
    public static final String logo = ChatColor.BLUE + "[" + ChatColor.DARK_AQUA + "Zenchantments"
            + ChatColor.BLUE + "] " + ChatColor.AQUA;
    
    public static final Material ores[] = new Material[]{COAL_ORE, REDSTONE_ORE, DIAMOND_ORE, GOLD_ORE,
                    IRON_ORE, LAPIS_ORE, GLOWSTONE, QUARTZ_ORE, EMERALD_ORE, GLOWING_REDSTONE_ORE};
    

}
