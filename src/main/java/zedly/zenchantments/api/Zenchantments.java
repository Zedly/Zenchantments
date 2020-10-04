package zedly.zenchantments.api;

import zedly.zenchantments.api.configuration.GlobalConfiguration;
import zedly.zenchantments.api.configuration.WorldConfigurationProvider;
import zedly.zenchantments.api.player.PlayerDataProvider;

/**
 * Represents the main Zenchantments plugin class and entry-point into the API.
 */
public interface Zenchantments {
    /**
     * Gets the {@link GlobalConfiguration} of this {@link Zenchantments} instance.
     *
     * @return The {@link GlobalConfiguration} of this {@link Zenchantments} instance.
     */
    GlobalConfiguration getGlobalConfiguration();

    /**
     * Gets the {@link WorldConfigurationProvider} of this {@link Zenchantments} instance.
     *
     * @return The {@link WorldConfigurationProvider} of this {@link Zenchantments} instance.
     */
    WorldConfigurationProvider getWorldConfigurationProvider();

    /**
     * Gets the {@link PlayerDataProvider} of this {@link Zenchantments} instance.
     *
     * @return The {@link PlayerDataProvider} of this {@link Zenchantments} instance.
     */
    PlayerDataProvider getPlayerDataProvider();
}