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

    @Override
    public void onEnable() {
        //Register Events, Start Processes, Run Methods
        this.saveDefaultConfig();
        Storage.zenchantments = this;
        Storage.version = Bukkit.getServer().getPluginManager().getPlugin(this.getName()).getDescription().getVersion();
        getServer().getPluginManager().registerEvents(new AnvilMerge(), this);
        getServer().getPluginManager().registerEvents(new ArrowWatcher(), this);
        getServer().getPluginManager().registerEvents(new EnchantmentWatcher(), this);
        getServer().getPluginManager().registerEvents(new Watcher(), this);
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new HFEffects(), 1, 1);
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new MFEffects(), 1, 5);
        //Load Arrow Recipes
        System.out.println("Loading " + Storage.ArrowTypes.size() + " Arrow classes...");
        ItemStack is = new ItemStack(ARROW);
        ItemMeta meta = is.getItemMeta();
        //Remove Arrows
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
        for (Class cl : Storage.ArrowTypes) {
            try {
                Arrow ar = (Arrow) cl.newInstance();
                if (ar.getRecipe(is) != null) {
                    ArrayList<String> lore = new ArrayList<>();
                    lore.add(ChatColor.AQUA + ar.getName());
                    meta.setLore(lore);
                    is.setItemMeta(meta);
                    getServer().addRecipe(ar.getRecipe(is));
                }
                if (ar.getName() != null) {
                    Storage.projectileTable.put(ar.getName(), ar.getClass());
                    Storage.arrowClass.put(ar.getName(), ar);
                }
            } catch (InstantiationException | IllegalAccessException ex) {
            }
        }
        //Check if Config is Up To Date
        if (!zenchantments.getConfig().getList("ZenchantmentsConfigVersion").get(0).equals("1.1.0")) {
            File file = new File("plugins/Zenchantments/config.yml");
            try {
                Files.delete(file.toPath());
            } catch (IOException ex) {
            }
            this.saveDefaultConfig();
        }
        //Load Enchantment Probablility Variable
        try {
            Storage.enchantRarity = (Double.parseDouble(zenchantments.getConfig().getList("probability").get(0).toString()) / 100);
        } catch (NumberFormatException e) {
            Storage.enchantRarity = .25f;
        }
        //Load Max Enchantments Variable
        try {
            Storage.max_enchants_per_item = (Integer.parseInt(zenchantments.getConfig().getList("max_enchants_per_tool").get(0).toString()));
        } catch (NumberFormatException e) {
            Storage.max_enchants_per_item = 4;
        }
        //Load Laser Dispenser Variable
        try {
            Storage.laser_in_dispensers = (Boolean.parseBoolean(zenchantments.getConfig().getList("laser_in_dispensers").get(0).toString()));
        } catch (NumberFormatException e) {
            Storage.laser_in_dispensers = true;
        }
        //Load Reset Player Speeds Variable
        try {
            Storage.reset_speed_on_login = (Boolean.parseBoolean(zenchantments.getConfig().getList("reset_speed_on_login").get(0).toString()));
        } catch (NumberFormatException e) {
            Storage.reset_speed_on_login = true;
        }
        //Load Force & Rainbow Slam Variable
        try {
            Storage.force_rainbow_slam_players = (Boolean.parseBoolean(zenchantments.getConfig().getList("force_rainbow_slam_players").get(0).toString()));
        } catch (NumberFormatException e) {
            Storage.force_rainbow_slam_players = true;
        }
        //Load Fuse Block Explode Variable
        try {
            Storage.fuse_blockbreak = (Boolean.parseBoolean(zenchantments.getConfig().getList("fuse_blockbreak").get(0).toString()));
        } catch (NumberFormatException e) {
            Storage.fuse_blockbreak = true;
        }
        //Load Item Drop Shred Variable
        switch (zenchantments.getConfig().getList("item_drop_shred").get(0).toString()) {
            case "all":
                Storage.item_drop_shred = 0;
                break;
            case "block":
                Storage.item_drop_shred = 1;
                break;
            case "none":
                Storage.item_drop_shred = 2;
                break;
            default:
                Storage.item_drop_shred = 0;
        }
        //Laser PVP Variable
        try {
            Storage.laser_pvp = (Boolean.parseBoolean(zenchantments.getConfig().getList("laser_pvp").get(0).toString()));
        } catch (NumberFormatException e) {
            Storage.laser_pvp = true;
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

        PlayerConfig.loadConfigs();
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
