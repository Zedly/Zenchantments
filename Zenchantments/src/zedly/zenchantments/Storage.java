package zedly.zenchantments;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import zedly.zenchantments.compatibility.CompatibilityAdapter;
import zedly.zenchantments.compatibility.NMS_1_13_R1;

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

    // Random object
    public static final Random rnd = new Random();

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
            case "v1_13_R1":
                COMPATIBILITY_ADAPTER = NMS_1_13_1_R1.getInstance();
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
