package zedly.zenchantments.configuration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.ZenchantmentsPlugin;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.IOUtils;
import zedly.zenchantments.Tool;
import zedly.zenchantments.Zenchantment;
import zedly.zenchantments.Zenchantment.Constructor;
import zedly.zenchantments.ZenchantmentFactory;
import zedly.zenchantments.api.Zenchantments;

public class WorldConfigurationProvider implements zedly.zenchantments.api.configuration.WorldConfigurationProvider {

    private static final Map<UUID, WorldConfiguration> configMap = new HashMap<>();
    private static final WorldConfigurationProvider instance = new WorldConfigurationProvider();

    public static WorldConfigurationProvider getInstance() {
        return instance;
    }

    @Override
    @NotNull
    public WorldConfiguration getConfigurationForWorld(final @NotNull World world) {
        if (configMap.containsKey(world.getUID())) {
            return configMap.get(world.getUID());
        } else {
            return configMap.put(world.getUID(), tryLoadConfigurationForWorld(world));
        }
    }

    @Override
    @NotNull
    public WorldConfiguration loadConfigurationForWorld(final @NotNull World world) throws IOException, InvalidConfigurationException {
        WorldConfiguration newConfiguration = this.loadConfiguration(world.getName());
        this.configMap.put(world.getUID(), newConfiguration);
        return newConfiguration;
    }

    @Override
    public void resetConfigurationForWorld(final @NotNull World world) {
        // TODO: Load default config, set as configuration for given world.
    }

    public void loadWorldConfigurations() {
        Bukkit.getServer().getWorlds().forEach(this::tryLoadConfigurationForWorld);
    }

    private WorldConfiguration tryLoadConfigurationForWorld(final @NotNull World world) {
        try {
            return loadConfigurationForWorld(world);
        } catch (IOException | InvalidConfigurationException e) {
            System.err.println("Zenchantments was unable to load the configuration for world: " + world.getName() + ".\n" +
                "Please check the configuration for this world. Falling back to default configuration!");
            e.printStackTrace();
            return ZenchantmentsPlugin.getInstance().getGlobalConfiguration().getDefaultWorldConfiguration();
        }
    }

    @NotNull
    private WorldConfiguration loadConfiguration(final @NotNull String worldName) throws IOException, InvalidConfigurationException {
        // Create default config for this world if it doesn't exist
        InputStream stream = Zenchantments.class.getResourceAsStream("/config.yml");
        File file = new File(ZenchantmentsPlugin.getInstance().getDataFolder(), worldName + ".yml");
        if (!file.exists()) {
            String raw = IOUtils.toString(stream, "UTF-8");
            byte[] b = raw.getBytes();
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(b, 0, b.length);
            fos.flush();
        }

        // Load the config for this config
        YamlConfiguration yamlConfig = new YamlConfiguration();
        yamlConfig.load(file);

        return WorldConfiguration.fromYamlConfiguration(yamlConfig);
    }
}
