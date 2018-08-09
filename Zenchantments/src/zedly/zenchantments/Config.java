package zedly.zenchantments;

import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import org.apache.commons.io.IOUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import zedly.zenchantments.enums.Tool;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

// This class manages indivudual world configs, loading them each from the config file. It will start the process
//      to automatically update the config files if they are old
public class Config {

    public static final Map<World, Config> CONFIGS = new HashMap<>(); // Map of all world configs on the current server
    public static final HashSet<CustomEnchantment> allEnchants = new HashSet<>();

    private final Set<CustomEnchantment> worldEnchants;     // Set of active Custom Enchantments
	private final Map<String, CustomEnchantment> nameToEnch;
	private final Map<Integer, CustomEnchantment> idToEnch;
	private final Set<String> enchNames;
    private final double enchantRarity;                        // Overall rarity of obtaining enchantments
    private final int maxEnchants;                             // Max number of Custom Enchantments on a tool
    private final int shredDrops;                              // The setting (all, block, none) for shred drops
    private final boolean explosionBlockBreak;                 // Determines whether enchantment explosions cause world damage
    private final boolean descriptionLore;                     // Determines if description lore appears on tools
    private final ChatColor descriptionColor;                  // The color of the description lore
    private final World world;                                 // The World associated with the config

    // Constructs a new config object
    public Config(Set<CustomEnchantment> worldEnchants, double enchantRarity,
            int maxEnchants, int shredDrops, boolean explosionBlockBreak,
            boolean descriptionLore, ChatColor descriptionColor, World world) {
        this.worldEnchants = worldEnchants;
        this.enchantRarity = enchantRarity;
        this.maxEnchants = maxEnchants;
        this.shredDrops = shredDrops;
        this.explosionBlockBreak = explosionBlockBreak;
        this.descriptionLore = descriptionLore;
        this.descriptionColor = descriptionColor;
        this.world = world;

        this.nameToEnch = new HashMap<>();
        for (CustomEnchantment ench : this.worldEnchants) {
	        nameToEnch.put(ench.getLoreName(), ench);
        }

	    this.idToEnch = new HashMap<>();
	    for (CustomEnchantment ench : this.worldEnchants) {
		    idToEnch.put(ench.getId(), ench);
	    }

	    this.enchNames = new HashSet<>();
	    for (CustomEnchantment ench : this.worldEnchants) {
		    enchNames.add(ench.getLoreName());
	    }

        allEnchants.addAll(worldEnchants);
    }

    // Returns a mapping of enchantment names to custom enchantment objects
    public Set<CustomEnchantment> getEnchants() {
        return worldEnchants;
    }

    // Returns the overall rarity of obtaining an enchantment
    public double getEnchantRarity() {
        return enchantRarity;
    }

    // Returns the max number of enchantments applicable on a tool
    public int getMaxEnchants() {
        return maxEnchants;
    }

    // Returns which block break setting is enabled for shred (0 = all; 1 = blocks; 2 = none)
    public int getShredDrops() {
        return shredDrops;
    }

    // Returns if certain enchantments can break blocks with the explosions they create
    public boolean explosionBlockBreak() {
        return explosionBlockBreak;
    }

    // Returns if description lore appears on tools
    public boolean descriptionLore() {
        return descriptionLore;
    }

    // Returns the color of description lore
    public ChatColor getDescriptionColor() {
        return descriptionColor;
    }

    // Returns the world associated with the config
    public World getWorld() {
        return world;
    }

    public CustomEnchantment enchantFromString(String str) {
    	return nameToEnch.get(str);
    }

    public CustomEnchantment enchantFromID(int id) {
    	return idToEnch.get(id);
    }

    public Set<String> getEnchantNames() {
    	return enchNames;
    }

    // Loads, parses, and auto updates the config file, creating a new config for each map 
    public static void loadConfigs() {
        CONFIGS.clear();
        for (World world : Bukkit.getWorlds()) {
            try {
                ClassLoader classloader = Thread.currentThread().getContextClassLoader();
                InputStream stream = Zenchantments.class.getResourceAsStream("/defaultconfig.yml");
                File file = new File(Storage.zenchantments.getDataFolder(), world.getName() + ".yml");
                if (!file.exists()) {
                    try {
                        String raw = IOUtils.toString(stream, "UTF-8");
                        byte[] b = raw.getBytes();
                        FileOutputStream fos = new FileOutputStream(file);
                        fos.write(b, 0, b.length);
                        fos.flush();
                    } catch (IOException e) {

                    }
                }
                YamlConfiguration yamlConfig = new YamlConfiguration();
                yamlConfig.load(file);
                int[] version = new int[3];
                try {
                    String[] versionString;
                    try {
                        versionString = yamlConfig.getString("ZenchantmentsConfigVersion").split("\\.");
                    } catch (NullPointerException ex) {
                        versionString = ((String) yamlConfig.getList("ZenchantmentsConfigVersion").get(0)).split("\\.");
                    }
                    if (versionString.length == 3) {
                        for (int i = 0; i < 3; i++) {
                            version[i] = Integer.parseInt(versionString[i]);
                        }
                    } else {
                        version = new int[]{0, 0, 0};
                    }
                } catch (Exception ex) {
                    version = new int[]{0, 0, 0};
                }
                UpdateConfig.update(yamlConfig, version);

                //Init variables
                final int shredDrops;
                yamlConfig.save(file);
                //Load Variables
                double rarity = (double) (yamlConfig.get("enchant_rarity"));
                double enchantRarity = (rarity / 100.0);
                int maxEnchants = (int) yamlConfig.get("max_enchants");
                boolean explosionBlockBreak = (boolean) yamlConfig.get("explosion_block_break");
                boolean descriptionLore = (boolean) yamlConfig.get("description_lore");
                ChatColor color = ChatColor.getByChar("" + yamlConfig.get("description_color"));
                ChatColor descriptionColor = (color != null) ? color : ChatColor.GREEN;
                switch ((String) yamlConfig.get("shred_drops")) {
                    case "all":
                        shredDrops = 0;
                        break;
                    case "block":
                        shredDrops = 1;
                        break;
                    case "none":
                        shredDrops = 2;
                        break;
                    default:
                        shredDrops = 0;
                }
                //Load CustomEnchantment Classes
                Set<CustomEnchantment> enchantments = new HashSet<>();
                Map<String, LinkedHashMap<String, Object>> configInfo = new HashMap<>();
                for (Map<String, LinkedHashMap<String, Object>> part
                        : (List<Map<String, LinkedHashMap<String, Object>>>) yamlConfig.get("enchantments")) {
                    for (String name : part.keySet()) {
                        configInfo.put(name, part.get(name));
                    }
                }

                List<Class<? extends CustomEnchantment>> customEnchantments = new ArrayList<>();
                new FastClasspathScanner(CustomEnchantment.class.getPackage().getName())
                    .matchSubclassesOf(CustomEnchantment.class, customEnchantments::add)
                    .scan();
                for (Class<? extends CustomEnchantment> cl : customEnchantments) {
                    try {
                        CustomEnchantment.Builder<? extends CustomEnchantment> ench = cl.newInstance().defaults();
                        if (configInfo.containsKey(ench.loreName())) {
                            LinkedHashMap<String, Object> data = configInfo.get(ench.loreName());
                            ench.probability((float) (double) data.get("Probability"));
                            ench.loreName((String) data.get("Name"));
                            ench.cooldown((int) data.get("Cooldown"));
                            if (data.containsValue("Max Level")) {
	                            Bukkit.broadcastMessage("1");
                                ench.maxLevel((int) data.get("Max Level"));
                            }
                            if (data.containsValue("Power")) {
	                            Bukkit.broadcastMessage("2");
                                ench.power((double) data.get("Power"));
                            }

                            Set<Tool> materials = new HashSet<>();
                            for (String s : ((String) data.get("Tools")).split(", |\\,")) {
                                materials.add(Tool.fromString(s));
                            }
                            ench.enchantable(materials.toArray(new Tool[0]));
                            if (ench.probability() != -1) {
                                enchantments.add(ench.build());
                            }
                        }
                    } catch (IllegalAccessException | ClassCastException | InstantiationException ex) {
                        System.err.println("Error parsing config for enchantment " + cl.getName() + ", skipping");
                    }
                }
                Config config = new Config(enchantments, enchantRarity, maxEnchants, shredDrops,
                        explosionBlockBreak, descriptionLore, descriptionColor, world);
                Config.CONFIGS.put(world, config);
            } catch (IOException | InvalidConfigurationException ex) {
	            System.err.println("Error parsing config for world " + world.getName() + ", skipping");
            }
        }
    }

    // Returns the config object associated with the given world
    public static Config get(World world) {
        return CONFIGS.get(world);
    }

}
