package zedly.zenchantments.api.configuration;

import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

public interface WorldConfigurationProvider {
    @NotNull
    WorldConfiguration getConfigurationForWorld(@NotNull World world);

    @NotNull
    WorldConfiguration loadConfigurationForWorld(@NotNull World world);

    void resetConfigurationForWorld(@NotNull World world);
}