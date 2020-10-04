package zedly.zenchantments.api.configuration;

import org.bukkit.World;

public interface WorldConfigurationProvider {
    WorldConfiguration getConfigurationForWorld(World world);

    void resetConfigurationForWorld(World world);
}