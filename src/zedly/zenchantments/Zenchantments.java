package zedly.zenchantments;
//For Bukkit & Spigot 1.8.X

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import java.io.File;
import java.util.Iterator;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import static org.bukkit.Material.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class Zenchantments extends JavaPlugin {

    private WorldGuardPlugin getWorldGuard() {
        Plugin plugin = getServer().getPluginManager().getPlugin("WorldGuard");
        if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
            return null;
        }
        return (WorldGuardPlugin) plugin;
    }

    public void loadConfigs() {
        Config.loadConfigs();
        Artifact.loadConfig();
        PlayerConfig.saveConfigs();
        PlayerConfig.loadConfigs();
    }

    @Override
    public void onEnable() {
        File file = new File("plugins/Zenchantments/");
        file.mkdir();
        Storage.zenchantments = this;
        loadConfigs();
        Storage.version = Bukkit.getServer().getPluginManager().getPlugin(this.getName()).getDescription().getVersion();
        getServer().getPluginManager().registerEvents(new AnvilMerge(), this);
        getServer().getPluginManager().registerEvents(new ArrowWatcher(), this);
        getServer().getPluginManager().registerEvents(new EnchantmentWatcher(), this);
        getServer().getPluginManager().registerEvents(new Watcher(), this);
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new HFEffects(), 1, 1);
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new MFEffects(), 1, 5);
        Storage.worldGuard = getWorldGuard();
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
