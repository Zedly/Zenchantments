package zedly.zenchantments;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import static org.bukkit.Material.*;
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
        Storage.version = Bukkit.getServer().getPluginManager().getPlugin("Zenchantments").getDescription().getVersion();
        getServer().getPluginManager().registerEvents(new Actions(), this);
        getServer().getPluginManager().registerEvents(new EnchantmentWatcher(), this);
        getServer().getPluginManager().registerEvents(new AnvilMerge(), this);
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new ContinuousEffects(), 1, 5);
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new FastEffects(), 1, 1);
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new HFEffects(), 2, 2);
        getServer().getPluginManager().registerEvents(new ArrowWatcher(), this);
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
        if (!zenchantments.getConfig().getList("ZenchantmentsConfigVersion").get(0).equals(Storage.version)) {
        }
        //Load Individual Enchantment Configs
        for (int x = 0; x < zenchantments.getConfig().getList("enchantments").size(); x++) {
            String str = "" + zenchantments.getConfig().getList("enchantments").get(x);
            float number = 1;
            String enchant = str.subSequence(0, str.lastIndexOf(" ")).toString().replace(" ", "").toLowerCase();
            try {
                number = Float.parseFloat(str.subSequence(str.lastIndexOf(" ") + 1, str.length()).toString());
            } catch (NumberFormatException e) {
            }
            Storage.enchantLevels.put(enchant, number);
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
        //Load Enchantment Classes
        for (Class cl : Storage.Enchantments) {
            try {
                Enchantment ench = (Enchantment) cl.newInstance();
                if (Storage.enchantLevels.containsKey(ench.loreName.replace(" ", "").toLowerCase())) {
                    Storage.enchantClass.put(ench.loreName.toLowerCase().replace(" ", ""), ench);
                }
                Storage.enchantClassU.put(ench.loreName.toLowerCase().replace(" ", ""), ench);
            } catch (InstantiationException | IllegalAccessException ex) {
            }
        }
        //Apply Enchantment Configs
        for (Entry ent : Storage.enchantLevels.entrySet()) {
            String key = (String) ent.getKey();
            Float value = (Float) ent.getValue();
            if (Storage.enchantClass.containsKey(key)) {
                Storage.enchantClass.get(key).chance = value;
            }
            if (value == -1) {
                Storage.enchantClassU.remove(key);
                Storage.enchantClass.remove(key);
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
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandlabel, String[] args) {
        return Commands.onCommand(sender, command, commandlabel, args);
    }
}
