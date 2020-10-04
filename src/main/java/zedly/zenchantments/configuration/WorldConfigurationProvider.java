package zedly.zenchantments.configuration;

import org.bukkit.World;
import zedly.zenchantments.ZenchantmentsPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WorldConfigurationProvider implements zedly.zenchantments.api.configuration.WorldConfigurationProvider {
    private final Map<UUID, WorldConfiguration> configMap = new HashMap<>();
    private final ZenchantmentsPlugin           plugin;

    public WorldConfigurationProvider(ZenchantmentsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public WorldConfiguration getConfigurationForWorld(World world) {
        return this.configMap.computeIfAbsent(world.getUID(), this::loadConfiguration);
    }

    @Override
    public WorldConfiguration loadConfigurationForWorld(World world) {
        WorldConfiguration newConfiguration = this.loadConfiguration(world.getUID());
        this.configMap.put(world.getUID(), newConfiguration);
        return newConfiguration;
    }

    @Override
    public void resetConfigurationForWorld(World world) {
        // TODO: Load default config, set as configuration for given world.
    }

    public void loadWorldConfigurations() {
        this.plugin.getServer().getWorlds().forEach(this::loadConfigurationForWorld);
    }

    private WorldConfiguration loadConfiguration(UUID worldId) {
        return null; // TODO: Load configuration from disk.
    }
}