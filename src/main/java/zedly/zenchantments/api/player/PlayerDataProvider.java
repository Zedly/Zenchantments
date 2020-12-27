package zedly.zenchantments.api.player;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * A provider and general manager of {@link PlayerData} instances for various {@link Player Players}.
 */
public interface PlayerDataProvider {
    /**
     * Gets the {@link PlayerData} for the specified {@link Player}.
     * <p>
     * If there is no {@link PlayerData} associated with the specified {@link Player}, a new instance will be created
     * and linked to them.
     *
     * @param player
     *     The {@link Player} to get the {@link PlayerData} for.
     *
     * @return The {@link PlayerData} for the specified {@link Player}.
     */
    @NotNull
    PlayerData getDataForPlayer(@NotNull Player player);

    /**
     * Resets the {@link PlayerData} for the specified {@link Player}.
     *
     * @param player
     *     The {@link Player} to reset the {@link PlayerData} for.
     */
    void resetDataForPlayer(@NotNull Player player);
}