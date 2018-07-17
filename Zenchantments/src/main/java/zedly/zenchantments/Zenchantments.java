package zedly.zenchantments;
//For Bukkit & Spigot 1.12.X

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import zedly.zenchantments.annotations.EffectTask;
import zedly.zenchantments.enchantments.Meador;
import zedly.zenchantments.enchantments.Speed;
import zedly.zenchantments.enchantments.Weight;
import zedly.zenchantments.enums.Frequency;

import java.io.File;
import java.util.Map;
import java.util.TreeMap;

import static org.bukkit.Material.STATIONARY_LAVA;
import static org.bukkit.Material.STATIONARY_WATER;

public class Zenchantments extends JavaPlugin {

    // Creates a directory for the plugin and then loads configs
    public void loadConfigs() {
        File file = new File("plugins/Zenchantments/");
        file.mkdir();
        Config.loadConfigs();
    }

	@EffectTask(Frequency.MEDIUM)
	public static void speedPlayers() {
		speedPlayers(false);
	}

	// Sets player fly and walk speed to default after certain enchantments are removed
	private static void speedPlayers(boolean checkAll) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			Config world = Config.get(player.getWorld());
			boolean check = false;
			for (ItemStack stk : player.getInventory().getArmorContents()) {
				Map<CustomEnchantment, Integer> map = world.getEnchants(stk);
				Class[] enchs = new Class[]{Weight.class, Speed.class, Meador.class};
				for (CustomEnchantment ench : map.keySet()) {
					if (ArrayUtils.contains(enchs, ench.getClass())) {
						check = true;
					}
				}
			}
			if (player.hasMetadata("ze.speed") && (!check || checkAll)) {
				player.removeMetadata("ze.speed", Storage.zenchantments);
				player.setFlySpeed(.1f);
				player.setWalkSpeed(.2f);
				break;
			}
		}
	}

    // Sets blocks to their natural states at shutdown
    public void onDisable() {
	    speedPlayers(true);
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

	// Loads configs and starts tasks
	public void onEnable() {
		Storage.zenchantments = this;
		Storage.version = Bukkit.getServer().getPluginManager().getPlugin(this.getName()).getDescription()
		                        .getVersion();
		loadConfigs();

		getServer().getPluginManager().registerEvents(new AnvilMerge(), this);
		getServer().getPluginManager().registerEvents(new WatcherArrow(), this);
		getServer().getPluginManager().registerEvents(WatcherEnchant.instance(), this);
		getServer().getPluginManager().registerEvents(new Watcher(), this);
		for (Frequency f : Frequency.values()) {
			getServer().getScheduler().scheduleSyncRepeatingTask(this, new TaskRunner(f), 1, f.period);
		}
	}
}
