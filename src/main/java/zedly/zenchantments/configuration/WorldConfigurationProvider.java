package zedly.zenchantments.configuration;

import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.ZenchantmentsPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WorldConfigurationProvider implements zedly.zenchantments.api.configuration.WorldConfigurationProvider {
    private final Map<UUID, WorldConfiguration> configMap = new HashMap<>();
    private final ZenchantmentsPlugin           plugin;

    public WorldConfigurationProvider(final @NotNull ZenchantmentsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    @NotNull
    public WorldConfiguration getConfigurationForWorld(final @NotNull World world) {
        return this.configMap.computeIfAbsent(world.getUID(), this::loadConfiguration);
    }

    @Override
    @NotNull
    public WorldConfiguration loadConfigurationForWorld(final @NotNull World world) {
        WorldConfiguration newConfiguration = this.loadConfiguration(world.getUID());
        this.configMap.put(world.getUID(), newConfiguration);
        return newConfiguration;
    }

    @Override
    public void resetConfigurationForWorld(final @NotNull World world) {
        // TODO: Load default config, set as configuration for given world.
    }

    public void loadWorldConfigurations() {
        this.plugin.getServer().getWorlds().forEach(this::loadConfigurationForWorld);
    }

    @NotNull
    private WorldConfiguration loadConfiguration(final @NotNull UUID worldId) {
        return null; // TODO: Load configuration from disk.
    }
}
