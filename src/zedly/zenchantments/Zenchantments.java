package zedly.zenchantments;
//For Bukkit & Spigot 1.8.X

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import static org.bukkit.Material.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import static zedly.zenchantments.Storage.zenchantments;

public class Zenchantments extends JavaPlugin {

    public static void enable() {
        Storage.zenchantments.saveDefaultConfig();
        Artifact.loadConfig();
        PlayerConfig.loadConfigs();
        Storage.zenchantments.reloadConfig();
        Storage.enchantClasses.clear();
        Storage.allEnchantClasses.clear();
        Storage.arrowLores.clear();
        Storage.projectileTable.clear();
        Storage.arrowClass.clear();
        Storage.originalEnchantClasses.clear();
        Storage.originalEnchantClassesReverse.clear();
        Storage.descriptionColor = ChatColor.GREEN;
        for (int x = zenchantments.getConfig().getList("elemental_arrows").size() - 1; x >= 0; x--) {
            String str = "" + zenchantments.getConfig().getList("elemental_arrows").get(x);
            boolean b;
            try {
                b = Boolean.parseBoolean(str.split("=")[1].replace("}", ""));
            } catch (NumberFormatException e) {
                b = false;
            }
            ArrayList<Class> toRemove = new ArrayList<>();
            for (Class cl : Storage.ArrowTypes) {
                try {
                    Arrow ar = (Arrow) cl.newInstance();
                    if (ar.getName() != null) {
                        if (ar.getName().equals(str.split("=")[0].replace("{", "")) && !b) {
                            toRemove.add(cl);
                        }
                    }
                } catch (InstantiationException | IllegalAccessException ex) {
                }
            }
            Storage.ArrowTypes.removeAll(toRemove);
        }
        //Load Arrows & Recipes
        ItemStack is = new ItemStack(ARROW);
        ItemMeta meta = is.getItemMeta();
        for (Class cl : Storage.ArrowTypes) {
            try {
                Arrow ar = (Arrow) cl.newInstance();
                if (ar.getRecipe(is) != null) {
                    ArrayList<String> lore = new ArrayList<>();
                    lore.add(ChatColor.AQUA + ar.getName());
                    meta.setLore(lore);
                    is.setItemMeta(meta);
                    Bukkit.getServer().addRecipe(ar.getRecipe(is));
                    Storage.arrowLores.add(ar.getRecipe(is).getResult().getItemMeta().getLore().get(0));
                }
                if (ar.getName() != null) {
                    Storage.projectileTable.put(ar.getName(), ar.getClass());
                    Storage.arrowClass.put(ar.getName(), ar);
                }
            } catch (InstantiationException | IllegalAccessException ex) {
            }
        }
        //Load Variables
        
        int rarity = (int) zenchantments.getConfig().get("probability");
        Storage.enchantRarity = ((double) rarity / 100.0);
        Storage.maxEnchants = (int) zenchantments.getConfig().get("max_enchants_per_tool");
        Storage.laserDispenser = (boolean) zenchantments.getConfig().get("laser_in_dispensers");
        Storage.loginSpeedReset = (boolean) zenchantments.getConfig().get("reset_speed_on_login");
        Storage.forceSlamPlayers = (boolean) zenchantments.getConfig().get("force_rainbow_slam_players");
        Storage.laserPVP = (boolean) zenchantments.getConfig().get("laser_pvp");
        Storage.fuseBreak = (boolean) zenchantments.getConfig().get("fuse_blockbreak");
        Storage.missileBreak = (boolean) zenchantments.getConfig().get("missile_blockbreak");
        Storage.apocalypseBreak = (boolean) zenchantments.getConfig().get("apocalypse_blockbreak");
        Storage.descriptions = (boolean) zenchantments.getConfig().get("lore_descriptions");
        ChatColor color = ChatColor.getByChar("" + zenchantments.getConfig().get("description_color"));
        if (color != null) {
            Storage.descriptionColor = color;
        }
        switch ((String) zenchantments.getConfig().get("item_drop_shred")) {
            case "all":
                Storage.shredDrops = 0;
                break;
            case "block":
                Storage.shredDrops = 1;
                break;
            case "none":
                Storage.shredDrops = 2;
                break;
            default:
                Storage.shredDrops = 0;
        }
        //Load Individual Enchantment Configs
        HashMap<String, ArrayList<String>> tempConfigs = new HashMap<>();
        for (int x = 0; x < zenchantments.getConfig().getList("enchantments").size(); x++) {
            String rawConfig = ("" + zenchantments.getConfig().getList("enchantments").get(x)).replace("}", "").replace("{", "");
            String[] p = rawConfig.replace(", ", ",").split("=");
            ArrayList<String> parts = new ArrayList<>();
            parts.add(p[2].split(",")[0]);
            parts.add(p[4].split(",")[0]);
            parts.add(p[5].split(",")[0]);
            for (int i = 0; i < p[3].split(",").length - 1; i++) {
                parts.add(p[3].split(",")[i]);
            }
            tempConfigs.put(rawConfig.subSequence(0, rawConfig.indexOf("=")).toString().replace(" ", "").toLowerCase(), parts);
        }
        //Load Enchantment Classes
        for (Class cl : Enchantment.class.getClasses()) {
            try {
                Enchantment ench = (Enchantment) cl.newInstance();
                if (tempConfigs.containsKey(ench.loreName.toLowerCase().replace(" ", ""))) {
                    ArrayList<String> conf = tempConfigs.get(ench.loreName.toLowerCase().replace(" ", ""));
                    float probability = 1;
                    try {
                        probability = Float.parseFloat(conf.get(0));
                    } catch (NumberFormatException e) {
                    }
                    ench.chance = probability;
                    if (ench.chance != -1) {
                        Storage.originalEnchantClasses.put(ench.loreName, ench);
                        Storage.originalEnchantClassesReverse.put(ench, ench.loreName);
                    }
                    ench.loreName = conf.get(1);
                    int max = 1;
                    try {
                        max = Integer.parseInt(conf.get(2));
                    } catch (NumberFormatException e) {
                    }
                    ench.maxLevel = max;
                    Object[] m = null;
                    for (int i = 3; i < conf.size(); i++) {
                        switch (conf.get(i)) {
                            case "Axe":
                                m = ArrayUtils.addAll(m, Storage.axes);
                                break;
                            case "Shovel":
                                m = ArrayUtils.addAll(m, Storage.spades);
                                break;
                            case "Sword":
                                m = ArrayUtils.addAll(m, Storage.swords);
                                break;
                            case "Pickaxe":
                                m = ArrayUtils.addAll(m, Storage.picks);
                                break;
                            case "Rod":
                                m = ArrayUtils.addAll(m, Storage.rods);
                                break;
                            case "Shears":
                                m = ArrayUtils.addAll(m, Storage.shears);
                                break;
                            case "Bow":
                                m = ArrayUtils.addAll(m, Storage.bows);
                                break;
                            case "Lighter":
                                m = ArrayUtils.addAll(m, Storage.lighters);
                                break;
                            case "Hoe":
                                m = ArrayUtils.addAll(m, Storage.hoes);
                                break;
                            case "Helmet":
                                m = ArrayUtils.addAll(m, Storage.helmets);
                                break;
                            case "Chestplate":
                                m = ArrayUtils.addAll(m, Storage.chestplates);
                                break;
                            case "Leggings":
                                m = ArrayUtils.addAll(m, Storage.leggings);
                                break;
                            case "Boots":
                                m = ArrayUtils.addAll(m, Storage.boots);
                                break;
                            case "All":
                                m = ArrayUtils.addAll(m, Storage.axes);
                                m = ArrayUtils.addAll(m, Storage.spades);
                                m = ArrayUtils.addAll(m, Storage.swords);
                                m = ArrayUtils.addAll(m, Storage.picks);
                                m = ArrayUtils.addAll(m, Storage.rods);
                                m = ArrayUtils.addAll(m, Storage.shears);
                                m = ArrayUtils.addAll(m, Storage.bows);
                                m = ArrayUtils.addAll(m, Storage.lighters);
                                m = ArrayUtils.addAll(m, Storage.hoes);
                                m = ArrayUtils.addAll(m, Storage.helmets);
                                m = ArrayUtils.addAll(m, Storage.chestplates);
                                m = ArrayUtils.addAll(m, Storage.leggings);
                                m = ArrayUtils.addAll(m, Storage.boots);
                                break;
                        }
                    }
                    ench.enchantable = (Material[]) m;
                }
                if (ench.chance != -1) {
                    Storage.allEnchantClasses.put(ench.loreName.toLowerCase().replace(" ", ""), ench);
                }
                if (ench.chance != 0 && ench.chance != -1) {
                    Storage.enchantClasses.put(ench.loreName.toLowerCase().replace(" ", ""), ench);
                }
            } catch (InstantiationException | IllegalAccessException ex) {
            }
        }
        System.out.println("Loaded " + Storage.ArrowTypes.size() + " arrow classes...");
        System.out.println("Loaded " + Storage.enchantClasses.size() + " enchantment classes...");
        System.out.println("Zenchantments successfully reloaded.");
    }

    @Override
    public void onEnable() {
        Storage.zenchantments = this;
        this.saveDefaultConfig();
        //Check versioning
        String version;
        try {
            version = (String) zenchantments.getConfig().getList("ZenchantmentsConfigVersion").get(0);
        } catch (NullPointerException e) {
            version = (String) zenchantments.getConfig().get("ZenchantmentsConfigVersion");
        }
        switch (version) {
            case "1.0.0":
            case "1.0.1":
            case "1.0.2":
                File file = new File("plugins/Zenchantments/config.yml");
                try {
                    Files.delete(file.toPath());
                } catch (IOException ex) {
                }
                this.saveDefaultConfig();
                break;
            case "1.1.0":
                this.getConfig().set("ZenchantmentsConfigVersion", "1.2.0");
                this.getConfig().set("probability", zenchantments.getConfig().getList("probability").get(0));
                this.getConfig().set("max_enchants_per_tool", zenchantments.getConfig().getList("max_enchants_per_tool").get(0));
                this.getConfig().set("laser_in_dispensers", zenchantments.getConfig().getList("laser_in_dispensers").get(0));
                this.getConfig().set("reset_speed_on_login", zenchantments.getConfig().getList("reset_speed_on_login").get(0));
                this.getConfig().set("force_rainbow_slam_players", zenchantments.getConfig().getList("force_rainbow_slam_players").get(0));
                this.getConfig().set("item_drop_shred", zenchantments.getConfig().getList("item_drop_shred").get(0));
                this.getConfig().set("laser_pvp", zenchantments.getConfig().getList("laser_pvp").get(0));
                this.getConfig().set("fuse_blockbreak", zenchantments.getConfig().getList("fuse_blockbreak").get(0));
                this.getConfig().createSection("missile_blockbreak");
                this.getConfig().set("missile_blockbreak", true);
                this.getConfig().createSection("apocalypse_blockbreak");
                this.getConfig().set("apocalypse_blockbreak", true);
                this.getConfig().createSection("lore_descriptions");
                this.getConfig().set("lore_descriptions", false);
                this.getConfig().createSection("description_color");
                this.getConfig().set("description_color", "a");
                this.saveConfig();
                break;
        }
        //register events, start processes, run methods, load configs
        enable();
        Storage.version = Bukkit.getServer().getPluginManager().getPlugin(this.getName()).getDescription().getVersion();
        getServer().getPluginManager().registerEvents(new AnvilMerge(), this);
        getServer().getPluginManager().registerEvents(new ArrowWatcher(), this);
        getServer().getPluginManager().registerEvents(new EnchantmentWatcher(), this);
        getServer().getPluginManager().registerEvents(new Watcher(), this);
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new HFEffects(), 1, 1);
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new MFEffects(), 1, 5);
    }

    @Override
    public void onDisable() {
        PlayerConfig.saveConfigs();
        getServer().getScheduler().cancelTasks(this);
        Iterator iceIT = Storage.waterLocs.keySet().iterator();
        while (iceIT.hasNext()) {
            Location location = (Location) iceIT.next();
            location.getBlock().setType(STATIONARY_WATER);
            iceIT.remove();
        }
        Iterator fireIT = Storage.fireLocs.keySet().iterator();
        while (fireIT.hasNext()) {
            Location location = (Location) fireIT.next();
            location.getBlock().setType(STATIONARY_LAVA);
            fireIT.remove();
        }
        Iterator anthIT = Storage.anthMobs.keySet().iterator();
        while (anthIT.hasNext()) {
            Entity e = (Entity) anthIT.next();
            e.remove();
            anthIT.remove();
        }
        Iterator webIT = Storage.webs.iterator();
        while (webIT.hasNext()) {
            Block b = (Block) webIT.next();
            b.setType(AIR);
            webIT.remove();
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandlabel, String[] args) {
        return CommandProcessor.onCommand(sender, command, commandlabel, args);
    }
}
