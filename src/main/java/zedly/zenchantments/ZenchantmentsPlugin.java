package zedly.zenchantments;

import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;
import zedly.zenchantments.enchantments.*;
import zedly.zenchantments.task.Frequency;
import zedly.zenchantments.task.TaskRunner;

import java.io.File;

import static org.bukkit.Material.LAVA;
import static org.bukkit.Material.WATER;
import static org.bukkit.potion.PotionEffectType.FAST_DIGGING;

public class ZenchantmentsPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        Storage.zenchantments = this;
        Storage.pluginPath = this.getServer()
            .getPluginManager()
            .getPlugin("Zenchantments")
            .getClass()
            .getProtectionDomain()
            .getCodeSource()
            .getLocation()
            .getPath();
        Storage.version = this.getServer()
            .getPluginManager()
            .getPlugin(this.getName())
            .getDescription()
            .getVersion();

        this.loadConfigs();

        this.getCommand("ench").setTabCompleter(new CommandProcessor.TabCompletion());

        this.getServer().getPluginManager().registerEvents(new AnvilMerge(this), this);
        this.getServer().getPluginManager().registerEvents(new GrindstoneMerge(this), this);
        this.getServer().getPluginManager().registerEvents(WatcherEnchant.instance(), this);
        this.getServer().getPluginManager().registerEvents(new Watcher(), this);

        for (Frequency frequency : Frequency.values()) {
            this.getServer().getScheduler().scheduleSyncRepeatingTask(
                this,
                new TaskRunner(this, frequency),
                1,
                frequency.getPeriod()
            );
        }

        if (this.getConfig().getBoolean("forceUpdateDescriptions", false)) {
            this.getServer().getScheduler().scheduleSyncRepeatingTask(this, this::updateDescriptions, 1, 200);
        }

        int[][] searchFaces = new int[27][3];
        int i = 0;
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    searchFaces[i++] = new int[]{x, y, z};
                }
            }
        }

        Lumber.SEARCH_FACES = searchFaces;
        Spectral.SEARCH_FACES = searchFaces;
        Pierce.SEARCH_FACES = searchFaces;
    }

    @Override
    public void onDisable() {
        for (Location location : FrozenStep.frozenLocs.keySet()) {
            location.getBlock().setType(WATER);
        }

        for (Location location : NetherStep.netherstepLocs.keySet()) {
            location.getBlock().setType(LAVA);
        }

        for (Entity entity : Anthropomorphism.idleBlocks.keySet()) {
            entity.remove();
        }

        for (Player player : this.getServer().getOnlinePlayers()) {
            if (player.hasMetadata("ze.speed")) {
                player.removeMetadata("ze.speed", Storage.zenchantments);
                player.setFlySpeed(0.1f);
                player.setWalkSpeed(0.2f);
            }

            if (player.hasMetadata("ze.haste")) {
                player.removePotionEffect(FAST_DIGGING);
                player.removeMetadata("ze.haste", Storage.zenchantments);
            }
        }
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return CommandProcessor.onCommand(sender, command, label, args);
    }

    public void loadConfigs() {
        if (new File("plugins/Zenchantments/").mkdir()) {
            this.getLogger().info("Created folder for configuration.");
        }

        Config.loadConfigs();
    }

    private void updateDescriptions() {
        for (Player player : this.getServer().getOnlinePlayers()) {
            PlayerInventory inventory = player.getInventory();
            for (ItemStack itemStack : ArrayUtils.addAll(inventory.getArmorContents(), inventory.getContents())) {
                Zenchantment.setEnchantment(itemStack, null, 0, player.getWorld());
                Zenchantment.updateToNewFormat(itemStack, player.getWorld());
            }
        }
    }
}