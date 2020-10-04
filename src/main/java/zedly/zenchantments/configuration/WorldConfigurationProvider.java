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
        WorldConfiguration config = this.configMap.get(world.getUID());

        if (config == null) {
            config = null; // TODO: Replace with actual WorldConfiguration constructor.
            this.configMap.put(world.getUID(), config);
        }

        return config;
    }

    @Override
    public void resetConfigurationForWorld(World world) {

    }
}