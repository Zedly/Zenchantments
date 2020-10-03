package zedly.zenchantments;

import org.bukkit.Bukkit;
import org.bukkit.block.BlockFace;
import zedly.zenchantments.compatibility.CompatibilityAdapter;
import zedly.zenchantments.compatibility.NMS_1_16_R1;

import java.util.Random;

@Deprecated
public class Storage {

    // Instance of the Zenchantments plugin to be used by the rest of the classes
    public static ZenchantmentsPlugin zenchantments;

    // Absolute path to the plugin jar
    public static String pluginPath;

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

    static {
        String versionString = Bukkit.getServer().getClass().getPackage().getName();
        String nmsVersionString = versionString.substring(versionString.lastIndexOf('.') + 1);
        System.out.println("Zenchantments: Detected NMS version \"" + nmsVersionString + "\"");
        switch (nmsVersionString) {
            case "v1_16_R2":
                COMPATIBILITY_ADAPTER = NMS_1_16_R1.getInstance();
                break;
            default:
                System.out.println(
                    "No compatible adapter available, falling back to Bukkit. Not everything will work!");
                COMPATIBILITY_ADAPTER = zedly.zenchantments.compatibility.CompatibilityAdapter.getInstance();
                break;
        }
    }
}
