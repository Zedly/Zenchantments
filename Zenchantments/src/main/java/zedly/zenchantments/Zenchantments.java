package zedly.zenchantments;
//For Bukkit & Spigot 1.10.X-1.11.X

import java.io.*;
import java.util.Map;
import java.util.TreeMap;
import org.bukkit.*;
import static org.bukkit.Material.*;
import org.bukkit.block.Block;
import org.bukkit.command.*;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class Zenchantments extends JavaPlugin {

    // Creates a directory for the plugin and then loads configs
    public void loadConfigs() {
        File file = new File("plugins/Zenchantments/");
        file.mkdir();
        Config.loadConfigs();
    }

    // Loads configs and starts tasks
    public void onEnable() {
        Storage.zenchantments = this;
        Storage.version = Bukkit.getServer().getPluginManager().getPlugin(this.getName()).getDescription().getVersion();
        loadConfigs(); 

        getServer().getPluginManager().registerEvents(new AnvilMerge(), this);
        getServer().getPluginManager().registerEvents(new WatcherArrow(), this);
        getServer().getPluginManager().registerEvents(WatcherEnchant.instance(), this);
        getServer().getPluginManager().registerEvents(new Watcher(), this);
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new HFEffects(), 1, 1);
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new MFEffects(), 1, 5);
    }

    // Sets blocks to their natural states at shutdown
    public void onDisable() {
        MFEffects.speedPlayers(true);
        getServer().getScheduler().cancelTasks(this);
        for (Location l : Storage.waterLocs.keySet()) {
            l.getBlock().setType(STATIONARY_WATER);
        }
        for (Location l : Storage.fireLocs.keySet()) {
            l.getBlock().setType(STATIONARY_LAVA);
        }
        for (Entity e : Storage.idleBlocks.keySet()) {
            e.remove();
        }
    }

    // Sends commands over to the CommandProcessor for it to handle
    public boolean onCommand(CommandSender sender, Command command, String commandlabel, String[] args) {
        return CommandProcessor.onCommand(sender, command, commandlabel, args);
    }

    // Returns true if the given item stack has a custom enchantment
    public boolean hasEnchantment(ItemStack stack) {
        boolean has = false;
        for (Config c : Config.CONFIGS) {
            if (!c.getEnchants(stack).isEmpty()) {
                has = true;
            }
        }
        return has;
    }

    // Returns enchantment names mapped to their level from the given item stack
    public Map<String, Integer> getEnchantments(ItemStack stack) {
        Map<String, Integer> enchantments = new TreeMap<>();
        for (Config c : Config.CONFIGS) {
            Map<CustomEnchantment, Integer> ench = c.getEnchants(stack);
            for (CustomEnchantment e : ench.keySet()) {
                enchantments.put(e.loreName, ench.get(e));
            }
        }
        return enchantments;
    }

    // Returns true if the enchantment (given by the string) can be applied to the given item stack
    public boolean isCompatible(String enchantmentName, ItemStack stack) {
        boolean is = false;
        for (Config c : Config.CONFIGS) {
            Map<String, CustomEnchantment> ench = c.getEnchants();
            if (ench.containsKey(enchantmentName.toLowerCase())) {
                is = ench.get(enchantmentName.toLowerCase()).validMaterial(stack);
                if (is) {
                    return true;
                }
            }
        }
        return is;
    }

    // Adds the enchantments (given by the string) of level 'level' to the given item stack, returning true if the
    //      action was successful
    public boolean addEnchantment(ItemStack stack, String name, int level) {
        for (Config c : Config.CONFIGS) {
            Map<String, CustomEnchantment> ench = c.getEnchants();
            if (ench.containsKey(name.toLowerCase())) {
                CommandProcessor.addEnchantments(c.getWorld(), null, ench.get(name.toLowerCase()), stack, level + "", false);
                return true;
            }
        }
        return false;
    }

    // Removes the enchantment (given by the string) from the given item stack, returning true if the action was
    //      successful
    public boolean removeEnchantment(ItemStack stack, String name) {
        for (Config c : Config.CONFIGS) {
            Map<String, CustomEnchantment> ench = c.getEnchants();
            if (ench.containsKey(name.toLowerCase())) {
                CommandProcessor.addEnchantments(c.getWorld(), null, ench.get(name.toLowerCase()), stack, "0", true);
                return true;
            }
        }
        return false;
    }

}
