package zedly.zenchantments.api;

import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.api.configuration.GlobalConfiguration;
import zedly.zenchantments.api.configuration.WorldConfigurationProvider;

/**
 * Represents the main Zenchantments plugin class and entry-point into the API.
 */
public interface Zenchantments {
    /**
     * Gets the {@link GlobalConfiguration} of this {@link Zenchantments} instance.
     *
     * @return The {@link GlobalConfiguration} of this {@link Zenchantments} instance.
     */
    @NotNull
    GlobalConfiguration getGlobalConfiguration();

    /**
     * Gets the {@link WorldConfigurationProvider} of this {@link Zenchantments} instance.
     *
     * @return The {@link WorldConfigurationProvider} of this {@link Zenchantments} instance.
     */
    @NotNull
    WorldConfigurationProvider getWorldConfigurationProvider();
}
