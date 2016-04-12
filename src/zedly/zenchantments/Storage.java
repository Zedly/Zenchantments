package zedly.zenchantments;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import java.util.*;
import org.bukkit.*;
import static org.bukkit.Material.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;

public class Storage {

    public static Zenchantments zenchantments;
    public static String version;
    public static boolean fallBool = false;
    public static final Random rnd = new Random();
    public static final Map<Arrow, Integer> tracer = new HashMap<>();
    public static final Map<Player, Double> sneakGlide = new HashMap<>();
    public static final Map<Guardian, Player> guardianMove = new HashMap<>();
    public static final Map<Player, Long> laserTimes = new HashMap<>();
    public static final List<Artifact> artifacts = new ArrayList<>();
    public static final Set<Block> webs = new HashSet<>();
    public static final Map<Entity, AdvancedArrow> killedEntities = new HashMap<>();
    public static final Map<Player, Integer> pierceModes = new HashMap<>();
    public static final Map<Player, Integer> forceModes = new HashMap<>();
    public static final Map<FallingBlock, Entity> idleBlocks = new HashMap<>();
    public static final Map<FallingBlock, Double> attackBlocks = new HashMap<>();
    public static final List<Entity> anthVortex = new ArrayList<>();
    public static final Map<Player, Set<String>> duringEvents = new HashMap<>();
    public static final Map<Block, Location> grabLocs = new HashMap<>();
    public static final Map<Block, Location> vortexLocs = new HashMap<>();
    public static final Map<Location, Long> fireLocs = new HashMap<>();
    public static final Map<Location, Long> waterLocs = new HashMap<>();
    public static final Set<Entity> damagingPlayer = new HashSet<>();
    public static final List<Entity> lightnings = new ArrayList<>();
    public static final Set<LivingEntity> derpingEntities = new HashSet<>();
    public static final Map<Entity, Set<AdvancedArrow>> advancedProjectiles = new HashMap<>();
    public static final Set<Player> speed = new HashSet<>();
    public static final Map<Player, Integer> hungerPlayers = new HashMap<>();
    public static final Map<Location, Boolean> blackholes = new HashMap<>();
    public static final Map<Player, Block> haulBlocks = new HashMap<>();
    public static final Map<Player, Integer> haulBlockDelay = new HashMap<>();

    public static WorldGuardPlugin worldGuard;
    //Predefined 
    public static final Integer[] badIds = new Integer[]{0, 8, 9, 10, 11, 26, 34, 36, 43, 51, 55, 59, 63, 64, 68, 71, 74, 75, 83, 90, 92, 93, 94, 104, 105, 115, 117, 118, 119, 124, 125, 127, 132, 140, 141, 142, 144, 149, 150, 176, 177, 178, 181, 193, 194, 195, 196, 197};
    public static final String logo = ChatColor.BLUE + "[" + ChatColor.DARK_AQUA + "Zenchantments" + ChatColor.BLUE + "] " + ChatColor.AQUA;
    public static final Material[] swords = new Material[]{WOOD_SWORD, STONE_SWORD, GOLD_SWORD, IRON_SWORD, DIAMOND_SWORD};
    public static final Material[] picks = new Material[]{WOOD_PICKAXE, STONE_PICKAXE, GOLD_PICKAXE, IRON_PICKAXE, DIAMOND_PICKAXE};
    public static final Material[] spades = new Material[]{WOOD_SPADE, STONE_SPADE, GOLD_SPADE, IRON_SPADE, DIAMOND_SPADE};
    public static final Material[] axes = new Material[]{WOOD_AXE, STONE_AXE, GOLD_AXE, IRON_AXE, DIAMOND_AXE};
    public static final Material[] hoes = new Material[]{WOOD_HOE, STONE_HOE, GOLD_HOE, IRON_HOE, DIAMOND_HOE};
    public static final Material[] helmets = new Material[]{DIAMOND_HELMET, IRON_HELMET, GOLD_HELMET, CHAINMAIL_HELMET, LEATHER_HELMET};
    public static final Material[] chestplates = new Material[]{DIAMOND_CHESTPLATE, IRON_CHESTPLATE, GOLD_CHESTPLATE, CHAINMAIL_CHESTPLATE, LEATHER_CHESTPLATE, ELYTRA};
    public static final Material[] leggings = new Material[]{DIAMOND_LEGGINGS, IRON_LEGGINGS, GOLD_LEGGINGS, CHAINMAIL_LEGGINGS, LEATHER_LEGGINGS};
    public static final Material[] boots = new Material[]{DIAMOND_BOOTS, IRON_BOOTS, GOLD_BOOTS, CHAINMAIL_BOOTS, LEATHER_BOOTS};
    public static final Material[] bows = new Material[]{BOW};
    public static final Material[] shears = new Material[]{SHEARS};
    public static final Material[] lighters = new Material[]{FLINT_AND_STEEL};
    public static final Material[] rods = new Material[]{FISHING_ROD};
}
