package zedly.zenchantments;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import static org.bukkit.Material.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Guardian;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class Storage {

    public static Zenchantments zenchantments;
    public static String version;
    public static HashSet<Player> speed = new HashSet<>();
    public static HashMap<FallingBlock, Entity> anthMobs = new HashMap<>();
    public static HashSet<FallingBlock> anthMobs2 = new HashSet<>();
    public static ArrayList<Entity> anthVortex = new ArrayList<>();
    public static HashMap<Player, HashSet<String>> duringEvents= new HashMap<>();
    public static final Map<Block, Location> grabbedBlocks = new HashMap<>();
    public static final Map<Block, Location> vortexLocs = new HashMap<>();
    public static HashMap<Location, Long> fireLocs = new HashMap<>();
    public static HashMap<Location, Long> waterLocs = new HashMap<>();
    public static final HashMap<Entity, HashSet<Arrow>> advancedProjectiles;
    public static final HashMap<String, Class> projectileTable;
    public static final ArrayList<Entity> lightnings;
    public static final HashSet<LivingEntity> derpingEntities;
    public static final Random rnd;
    public static ArrayList<Class> ArrowTypes = new ArrayList<Class>(Arrays.asList(Arrow.class.getClasses()));
    public static LinkedHashMap<String, Enchantment> enchantClasses = new LinkedHashMap<>();
    public static LinkedHashMap<String, Enchantment> allEnchantClasses = new LinkedHashMap<>();
    public static LinkedHashMap<String, Enchantment> originalEnchantClasses = new LinkedHashMap<>();
    public static LinkedHashMap<Enchantment, String> originalEnchantClassesReverse = new LinkedHashMap<>();
    public static Map<String, Arrow> arrowClass = new HashMap<>();
    public static final HashSet<Block> webs;
    public static final HashMap<Entity, Arrow> killedEntities = new HashMap<>();
    public static final HashMap<Player, Integer> pierceModes = new HashMap<>();
    public static final HashMap<Player, Integer> forceModes = new HashMap<>();
    public static final String logo = ChatColor.BLUE + "[" + ChatColor.DARK_AQUA + "Zenchantments" + ChatColor.BLUE + "] " + ChatColor.AQUA;
    public static HashMap<Player, Location> jumpPLayers = new HashMap<>();
    public static HashMap<org.bukkit.entity.Arrow, Integer> tracer = new HashMap<>();
    public static HashMap<Player, Double> sneakGlide = new HashMap<>();
    public static boolean fallBool = false;
    public static HashMap<Guardian, Player> guardianMove = new HashMap<>();
    public static HashMap<Player, Long> laserTimes = new HashMap<>();
    
    //config variables
    public static double enchantRarity;
    public static int max_enchants_per_item;
    public static HashMap<UUID, HashSet<Enchantment>> playerSettings= new HashMap<>();
    public static boolean laser_in_dispensers;
    public static boolean reset_speed_on_login;
    public static boolean force_rainbow_slam_players;
    public static int item_drop_shred;
    public static boolean laser_pvp;
    public static boolean fuse_blockbreak;

    public static final Material[] swords = new Material[]{WOOD_SWORD, STONE_SWORD, GOLD_SWORD, IRON_SWORD, DIAMOND_SWORD};
    public static final Material[] picks = new Material[]{WOOD_PICKAXE, STONE_PICKAXE, GOLD_PICKAXE, IRON_PICKAXE, DIAMOND_PICKAXE};
    public static final Material[] spades = new Material[]{WOOD_SPADE, STONE_SPADE, GOLD_SPADE, IRON_SPADE, DIAMOND_SPADE};
    public static final Material[] axes = new Material[]{WOOD_AXE, STONE_AXE, GOLD_AXE, IRON_AXE, DIAMOND_AXE};
    public static final Material[] hoes = new Material[]{WOOD_HOE, STONE_HOE, GOLD_HOE, IRON_HOE, DIAMOND_HOE};
    public static final Material[] helmets = new Material[]{DIAMOND_HELMET, IRON_HELMET, GOLD_HELMET, CHAINMAIL_HELMET, LEATHER_HELMET};
    public static final Material[] chestplates = new Material[]{DIAMOND_CHESTPLATE, IRON_CHESTPLATE, GOLD_CHESTPLATE, CHAINMAIL_CHESTPLATE, LEATHER_CHESTPLATE};
    public static final Material[] leggings = new Material[]{DIAMOND_LEGGINGS, IRON_LEGGINGS, GOLD_LEGGINGS, CHAINMAIL_LEGGINGS, LEATHER_LEGGINGS};
    public static final Material[] boots = new Material[]{DIAMOND_BOOTS, IRON_BOOTS, GOLD_BOOTS, CHAINMAIL_BOOTS, LEATHER_BOOTS};
    public static final Material[] bows = new Material[]{BOW};
    public static final Material[] shears = new Material[]{SHEARS};
    public static final Material[] lighters = new Material[]{FLINT_AND_STEEL};
    public static final Material[] rods = new Material[]{FISHING_ROD};
    
    static {
        lightnings = new ArrayList<>();
        advancedProjectiles = new HashMap<>();
        rnd = new Random();
        projectileTable = new HashMap<>();
        webs = new HashSet<>();
        derpingEntities = new HashSet<>();
    }
}
