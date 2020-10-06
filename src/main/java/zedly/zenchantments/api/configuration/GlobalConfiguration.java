package zedly.zenchantments.api.configuration;

import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.api.Zenchantments;

/**
 * The global {@link Zenchantments} configuration, as set in the {@code config.yml} file.
 * <p>
 * Values here apply across all {@link World Worlds}, unlike {@link WorldConfiguration}, which specifies values specific
 * to a {@link World}.
 */
public interface GlobalConfiguration {
    /**
     * Gets the default {@link WorldConfiguration} that will be used for a {@link World} if it doesn't have a
     * configuration set up.
     *
     * @return The default {@link WorldConfiguration}.
     */
    @NotNull
    WorldConfiguration getDefaultWorldConfiguration();
}