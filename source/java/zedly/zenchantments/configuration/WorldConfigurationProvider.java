package zedly.zenchantments.configuration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.ZenchantmentsPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
//import org.bukkit.craftbukkit.libs.org.apache.commons.io.IOUtils;
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

    private WorldConfiguration loadDefaultConfigurationForWorld(final @NotNull World world) {
        WorldConfiguration newConfiguration = GlobalConfiguration.getDefaultWorldConfiguration();
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
            return loadDefaultConfigurationForWorld(world);
        }
    }

    @NotNull
    private WorldConfiguration loadConfiguration(final @NotNull String worldName) throws IOException, InvalidConfigurationException {
        // Create default config for this world if it doesn't exist
        InputStream stream = Zenchantments.class.getResourceAsStream("/config.yml");
        Path path = Path.of(ZenchantmentsPlugin.getInstance().getDataFolder().getAbsolutePath(), worldName + ".yml");
        File file = path.toFile();
        if (!file.exists()) {
            Files.copy(stream, path, StandardCopyOption.REPLACE_EXISTING);
        }

        // Load the config for this config
        YamlConfiguration yamlConfig = new YamlConfiguration();
        yamlConfig.load(file);

        return WorldConfiguration.fromYamlConfiguration(yamlConfig);
    }
}
