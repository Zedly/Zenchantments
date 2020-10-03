package zedly.zenchantments.api;

import org.bukkit.World;
import zedly.zenchantments.api.player.PlayerDataProvider;

import java.util.Set;

/**
 * Represents the main Zenchantments plugin class and entry-point into the API.
 */
public interface Zenchantments {
    /**
     * Gets the {@link PlayerDataProvider} of this {@link Zenchantments} instance.
     *
     * @return The {@link PlayerDataProvider} of this {@link Zenchantments} instance.
     */
    PlayerDataProvider getPlayerDataProvider();

    /**
     * Gets the {@link Zenchantment Zenchantments} enabled for the given {@link World}.
     *
     * @param world The {@link World} to get the enabled {@link Zenchantment Zenchantments} for.
     *
     * @return A {@link Set} of the enabled {@link Zenchantment Zenchantments}.
     */
    Set<Zenchantment> getZenchantmentsForWorld(World world);
}