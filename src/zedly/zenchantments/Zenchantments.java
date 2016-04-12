package zedly.zenchantments;
//For Bukkit & Spigot 1.9.X

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import java.io.*;
import java.util.Map;
import java.util.TreeMap;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.*;
import static org.bukkit.Material.*;
import org.bukkit.block.Block;
import org.bukkit.command.*;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class Zenchantments extends JavaPlugin {

    // Tries to find an instance of WorldGuard running on the server
    private WorldGuardPlugin getWorldGuard() {
        Plugin plugin = getServer().getPluginManager().getPlugin("WorldGuard");
        if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
            return null;
        }
        return (WorldGuardPlugin) plugin;
    }

    // Creates a directory for the plugin and then loads configs
    public void loadConfigs() {
        File file = new File("plugins/Zenchantments/");
        file.mkdir();
        Config.loadConfigs();
        Artifact.loadConfig();
    }

    // Loads configs and starts tasks
    public void onEnable() {
        Storage.version = Bukkit.getServer().getPluginManager().getPlugin(this.getName()).getDescription().getVersion();
        loadConfigs();
        Storage.zenchantments = this;
        Storage.worldGuard = getWorldGuard();

        getServer().getPluginManager().registerEvents(new AnvilMerge(), this);
        getServer().getPluginManager().registerEvents(new WatcherArrow(), this);
        getServer().getPluginManager().registerEvents(new WatcherEnchant(), this);
        getServer().getPluginManager().registerEvents(new Watcher(), this);
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new HFEffects(), 1, 1);
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new MFEffects(), 1, 5);
    }

    // Sets blocks to their natural states at shutdown
    public void onDisable() {
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
        for (Block b : Storage.webs) {
            b.setType(AIR);
        }
    }

    // Sends commands over to the CommandProcessor for it to handle
    public boolean onCommand(CommandSender sender, Command command, String commandlabel, String[] args) {
        return CommandProcessor.onCommand(sender, command, commandlabel, args);
    }

    public boolean hasEnchantment(ItemStack stack) {
        boolean has = false;
        for (Config c : Config.CONFIGS) {
            if (!c.getEnchants(stack).isEmpty()) {
                has = true;
            }
        }
        return has;
    }

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

    public boolean isCompatible(String enchantmentName, ItemStack stack) {
        boolean is = false;
        for (Config c : Config.CONFIGS) {
            Map<String, CustomEnchantment> ench = c.getEnchants();
            if (ench.containsKey(enchantmentName.toLowerCase())) {
                is = ArrayUtils.contains(ench.get(enchantmentName.toLowerCase()).enchantable, stack.getType());
                if (is){
                    return true;
                }
            }
        }
        return is;
    }

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
