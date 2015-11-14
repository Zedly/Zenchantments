package zedly.zenchantments;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import static org.bukkit.Material.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Guardian;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class Storage {

    public static Zenchantments zenchantments;
    public static String version;
    public static boolean fallBool = false;
    public static final Random rnd = new Random();
    public static final HashMap<UUID, HashSet<CustomEnchantment>> playerSettings = new HashMap<>();
    public static final HashMap<Arrow, Integer> tracer = new HashMap<>();
    public static final HashMap<Player, Double> sneakGlide = new HashMap<>();
    public static final HashMap<Guardian, Player> guardianMove = new HashMap<>();
    public static final HashMap<Player, Long> laserTimes = new HashMap<>();
    public static final ArrayList<Artifact> artifacts = new ArrayList<>();
    public static final HashSet<Block> webs = new HashSet<>();
    public static final HashMap<Entity, CustomArrow> killedEntities = new HashMap<>();
    public static final HashMap<Player, Integer> pierceModes = new HashMap<>();
    public static final HashMap<Player, Integer> forceModes = new HashMap<>();
    public static final HashMap<FallingBlock, Entity> anthMobs = new HashMap<>();
    public static final HashSet<FallingBlock> anthMobs2 = new HashSet<>();
    public static final ArrayList<Entity> anthVortex = new ArrayList<>();
    public static final HashMap<Player, HashSet<String>> duringEvents = new HashMap<>();
    public static final Map<Block, Location> grabLocs = new HashMap<>();
    public static final Map<Block, Location> vortexLocs = new HashMap<>();
    public static final HashMap<Location, Long> fireLocs = new HashMap<>();
    public static final HashMap<Location, Long> waterLocs = new HashMap<>();
    public static final HashSet<Entity> damagingPlayer = new HashSet<>();
    public static final ArrayList<Entity> lightnings = new ArrayList<>();
    public static final HashSet<LivingEntity> derpingEntities = new HashSet<>();
    public static final HashMap<Entity, HashSet<CustomArrow>> advancedProjectiles = new HashMap<>();
    public static final HashSet<Player> speed = new HashSet<>();
    public static final HashMap<Player, Integer> hungerPlayers = new HashMap<>();
    public static final ArrayList<Config> worldConfigs = new ArrayList<>();
    public static final HashMap<Location, Boolean> blackholes = new HashMap<>();
    public static final HashMap<Player, Block> moverBlocks = new HashMap<>();
    public static final HashMap<Player, Integer> moverBlockDecay = new HashMap<>();
    
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
    public static final Material[] chestplates = new Material[]{DIAMOND_CHESTPLATE, IRON_CHESTPLATE, GOLD_CHESTPLATE, CHAINMAIL_CHESTPLATE, LEATHER_CHESTPLATE};
    public static final Material[] leggings = new Material[]{DIAMOND_LEGGINGS, IRON_LEGGINGS, GOLD_LEGGINGS, CHAINMAIL_LEGGINGS, LEATHER_LEGGINGS};
    public static final Material[] boots = new Material[]{DIAMOND_BOOTS, IRON_BOOTS, GOLD_BOOTS, CHAINMAIL_BOOTS, LEATHER_BOOTS};
    public static final Material[] bows = new Material[]{BOW};
    public static final Material[] shears = new Material[]{SHEARS};
    public static final Material[] lighters = new Material[]{FLINT_AND_STEEL};
    public static final Material[] rods = new Material[]{FISHING_ROD};
}
