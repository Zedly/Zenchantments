package zedly.zenchantments;

import java.io.*;
import java.util.*;
import org.apache.commons.io.IOUtils;
import org.bukkit.*;
import static org.bukkit.Material.ARROW;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

// This class manages indivudual world configs, loading them each from the config file. It will start the process
//      to automatically update the config files if they are old
public class Config {

    public static final Set<Config> CONFIGS = new HashSet<>(); // Set of all world configs on the current server

    private final Map<String, CustomEnchantment> enchants;     // Set of active Custom Enchantments 
    private final double enchantRarity;                        // Overall rarity of obtaining enchantments
    private final int maxEnchants;                             // Max number of Custom Enchantments on a tool
    private final int shredDrops;                              // The setting (all, block, none) for shred drops 
    private final boolean explosionBlockBreak;                 // Determines whether enchantment explosions cause world damage
    private final boolean descriptionLore;                     // Determines if description lore appears on tools
    private final ChatColor descriptionColor;                  // The color of the description lore
    private final World world;                                 // The World associated with the config

    // Constructs a new config object
    public Config(Map<String, CustomEnchantment> enchants, double enchantRarity,
            int maxEnchants, int shredDrops, boolean explosionBlockBreak,
            boolean descriptionLore, ChatColor descriptionColor, World world) {
        this.enchants = enchants;
        this.enchantRarity = enchantRarity;
        this.maxEnchants = maxEnchants;
        this.shredDrops = shredDrops;
        this.explosionBlockBreak = explosionBlockBreak;
        this.descriptionLore = descriptionLore;
        this.descriptionColor = descriptionColor;
        this.world = world;
    }

    // Returns a mapping of enchantment names to custom enchantment objects
    public Map<String, CustomEnchantment> getEnchants() {
        return enchants;
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
                YamlConfiguration c = new YamlConfiguration();
                c.load(file);
                int[] version = new int[3];
                try {
                    String[] versionString;
                    try {
                        versionString = c.getString("ZenchantmentsConfigVersion").split("\\.");
                    } catch (NullPointerException ex) {
                        versionString = ((String) c.getList("ZenchantmentsConfigVersion").get(0)).split("\\.");
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
                UpdateConfig.update(c, version);

                //Init variables
                final int shredDrops;
                //Load Arrows & Recipes
                ItemStack is = new ItemStack(ARROW);
                ItemMeta meta = is.getItemMeta();
                c.save(file);
                //Load Variables
                double rarity = (double) (c.get("enchant_rarity"));
                double enchantRarity = ((double) rarity / 100.0);
                int maxEnchants = (int) c.get("max_enchants");
                boolean explosionBlockBreak = (boolean) c.get("explosion_block_break");
                boolean descriptionLore = (boolean) c.get("description_lore");
                ChatColor color = ChatColor.getByChar("" + c.get("description_color"));
                ChatColor descriptionColor = (color != null) ? color : ChatColor.GREEN;
                switch ((String) c.get("shred_drops")) {
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
                Map<String, CustomEnchantment> enchantmentMap = new HashMap<>();
                Map<String, LinkedHashMap<String, Object>> configInfo = new HashMap<>();
                for (Map<String, LinkedHashMap<String, Object>> part
                        : (List<Map<String, LinkedHashMap<String, Object>>>) c.get("enchantments")) {
                    for (String name : part.keySet()) {
                        configInfo.put(name, part.get(name));
                    }
                }
                for (Class cl : CustomEnchantment.class.getClasses()) {
                    try {
                        CustomEnchantment ench = (CustomEnchantment) cl.newInstance();
                        if (configInfo.containsKey(ench.loreName)) {
                            LinkedHashMap<String, Object> data = configInfo.get(ench.loreName);
                            ench.probability = (float) (double) data.get("Probability");
                            ench.loreName = (String) data.get("Name");
                            if (data.containsValue("Max Level")) {
                                ench.maxLevel = (int) data.get("Max Level");
                            }
                            ench.cooldown = (int) data.get("Cooldown");
                            if (data.containsValue("Power")) {
                                ench.power = (double) data.get("Power");
                            }
                            Set<Tool> materials = new HashSet<>();
                            for (String s : ((String) data.get("Tools")).split(", |\\,")) {
                                materials.add(Tool.fromString(s));
                            }
                            materials.toArray(ench.enchantable);
                            if (ench.probability != -1) {
                                enchantmentMap.put(ench.loreName.toLowerCase().replace(" ", ""), ench);
                            }
                        }
                    } catch (InstantiationException | IllegalAccessException | ClassCastException ex) {
                        System.err.println("Error parsing config for enchantment " + cl.getName() + ", skipping");
                    }
                }
                Config config = new Config(enchantmentMap, enchantRarity, maxEnchants, shredDrops,
                        explosionBlockBreak, descriptionLore, descriptionColor, world);
                Config.CONFIGS.add(config);
            } catch (IOException | InvalidConfigurationException ex) {
            }
        }
    }

    // Returns the config object associated with the given world
    public static Config get(World world) {
        for (Config c : CONFIGS) {
            if (c.world.equals(world)) {
                return c;
            }
        }
        loadConfigs();
        for (Config c : CONFIGS) {
            if (c.world.equals(world)) {
                return c;
            }
        }
        return null;
    }

    // Returns a mapping of custom enchantments and their level on a given tool
    public LinkedHashMap<CustomEnchantment, Integer> getEnchants(ItemStack stk) {
        return getEnchants(stk, false);
    }

    public LinkedHashMap<CustomEnchantment, Integer> getEnchants(ItemStack stk, boolean acceptBooks) {
        ItemStack stack;
        Map<CustomEnchantment, Integer> map = new LinkedHashMap<>();
        if (stk != null && (acceptBooks || stk.getType() != Material.ENCHANTED_BOOK)) {
            stack = removeDescriptions(stk.clone(), null);
            if (stack.hasItemMeta()) {
                if (stack.getItemMeta().hasLore()) {
                    List<String> lore = stack.getItemMeta().getLore();
                    for (String rawEnchant : lore) {
                        int index1 = rawEnchant.lastIndexOf(" ");
                        if (index1 == -1) {
                            continue;
                        }
                        if (rawEnchant.length() > 2) {
                            try {
                                Integer level = Utilities.getNumber(rawEnchant.substring(index1 + 1));
                                String enchant = rawEnchant.substring(2, index1);//ERROR
                                if (getEnchants().containsKey(enchant.replace(" ", "").toLowerCase())) {
                                    CustomEnchantment ench = getEnchants().get(enchant.replace(" ", "").toLowerCase());
                                    map.put(ench, level);
                                }
                            } catch (StringIndexOutOfBoundsException e) {
                                System.out.println("Lore causing error: " + rawEnchant);
                            }
                        }

                    }
                }
            }
        }
        LinkedHashMap<CustomEnchantment, Integer> finalmap = new LinkedHashMap<>();
        for (Class c : new Class[]{CustomEnchantment.Lumber.class, CustomEnchantment.Shred.class,
            CustomEnchantment.Mow.class, CustomEnchantment.Pierce.class, CustomEnchantment.Extraction.class,
            CustomEnchantment.Plough.class}) {
            CustomEnchantment e = null;
            for (CustomEnchantment en : getEnchants().values()) {
                if (en.getClass().equals(c)) {
                    e = en;
                }
            }
            if (map.containsKey(e)) {
                finalmap.put(e, map.get(e));
                map.remove(e);
            }
        }
        finalmap.putAll(map);
        return finalmap;
    }

    // Returns the custom enchantment from the lore name 
    private CustomEnchantment getEnchant(String raw) {
        CustomEnchantment e = null;
        if (raw != null && raw.length() > 2) {
            int index1 = raw.lastIndexOf(" ");
            if (index1 < 0) {
                return e;
            }
            try {
                String enchant = raw.substring(2, index1);
                if (getEnchants().containsKey(enchant.replace(" ", "").toLowerCase())) {
                    e = getEnchants().get(enchant.replace(" ", "").toLowerCase());
                }
            } catch (StringIndexOutOfBoundsException ex) {
                System.out.println("Zenchantments error parsing item with lore: " + raw);
            }
        }
        return e;
    }

    // Returns the custom enchantment from the enchantment ID
    private CustomEnchantment getEnchant(int id) {
        for (CustomEnchantment ench : enchants.values()) {
            if (ench.getEnchantmentId() == id) {
                return ench;
            }
        }
        return null;
    }

    // Adds lore descriptions to a given item stack, but will remove a certain lore if the enchant is to be removed
    public ItemStack addDescriptions(ItemStack stk, CustomEnchantment delete) {
        stk = removeDescriptions(stk, delete);
        if (stk != null) {
            if (stk.hasItemMeta()) {
                if (stk.getItemMeta().hasLore()) {
                    ItemMeta meta = stk.getItemMeta();
                    List<String> lore = new ArrayList<>();
                    for (String s : meta.getLore()) {
                        lore.add(s);
                        CustomEnchantment e = getEnchant(s);
                        if (e != null) {
                            String str = e.description;
                            int start = 0;
                            int counter = 0;
                            for (int i = 0; i < str.toCharArray().length; i++) {
                                if (counter > 30) {
                                    if (str.toCharArray()[i - 1] == ' ') {
                                        lore.add(getDescriptionColor() + str.substring(start, i));
                                        counter = 0;
                                        start = i;
                                    }
                                }
                                counter++;
                            }
                            lore.add(getDescriptionColor() + str.substring(start));
                        }
                    }
                    meta.setLore(lore);
                    stk.setItemMeta(meta);
                }
            }
        }
        return stk;
    }

    // Removes the lore description from a given item
    public ItemStack removeDescriptions(ItemStack stk, CustomEnchantment delete) {
        if (stk != null) {
            if (stk.hasItemMeta()) {
                if (stk.getItemMeta().hasLore()) {
                    ItemMeta meta = stk.getItemMeta();
                    List<String> lore = new ArrayList<>();
                    CustomEnchantment current = null;
                    for (String s : meta.getLore()) {
                        CustomEnchantment e = getEnchant(s);
                        if (e != null) {
                            current = e;
                        }
                        if (current == null) {
                            if (delete != null) {
                                if (!delete.description.contains(ChatColor.stripColor(s))) {
                                    lore.add(s);
                                }
                            } else {
                                lore.add(s);
                            }
                        } else if (delete != null) {
                            if (!delete.description.contains(ChatColor.stripColor(s))
                                    && !current.description.contains(ChatColor.stripColor(s))) {
                                lore.add(s);
                            }
                        } else if (!current.description.contains(ChatColor.stripColor(s))) {
                            lore.add(s);
                        }
                    }
                    meta.setLore(lore);
                    stk.setItemMeta(meta);
                    return stk;
                }
            }
        }
        return stk;
    }
}
