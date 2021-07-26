package zedly.zenchantments.api.configuration;

import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

/**
 * A provider and general manager of {@link WorldConfiguration} instances for various {@link World Worlds}.
 */
public interface WorldConfigurationProvider {
    /**
     * Gets the {@link WorldConfiguration} for the specified {@link World}.
     * <p>
     * If the configuration for the specified {@link World} has not yet been loaded, it will be loaded using {@link
     * WorldConfigurationProvider#loadConfigurationForWorld}.
     *
     * @param world
     *     The {@link World} to get the {@link WorldConfiguration} for.
     *
     * @return The {@link WorldConfiguration} for the specified {@link World}.
     */
    @NotNull
    WorldConfiguration getConfigurationForWorld(@NotNull World world);

    /**
     * Loads the {@link WorldConfiguration} for the specified {@link World} from the disk.
     * <p>
     * If there is no configuration set up for the specified {@link World}, the values returned by {@link
     * GlobalConfiguration#getDefaultWorldConfiguration} will be used.
     *
     * @param world
     *     The {@link World} to load the {@link WorldConfiguration} for.
     *
     * @return The loaded {@link WorldConfiguration}, or the values returned by {@link
     * GlobalConfiguration#getDefaultWorldConfiguration} if no configuration has been set up for the specified
     * {@link World}.
     */
    @NotNull
    WorldConfiguration loadConfigurationForWorld(@NotNull World world);

    /**
     * Resets the {@link WorldConfiguration} for the specified {@link World} back to the values returned by
     * {@link GlobalConfiguration#getDefaultWorldConfiguration}.
     *
     * @param world
     *     The {@link World} to reset the {@link WorldConfiguration} for.
     */
    void resetConfigurationForWorld(@NotNull World world);
}