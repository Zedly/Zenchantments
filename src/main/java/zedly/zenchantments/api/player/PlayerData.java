package zedly.zenchantments.api.player;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.api.Zenchantment;

/**
 * Stores data about a {@link Player Player's} ability to interact with the plugin.
 */
public interface PlayerData {
    /**
     * Enables the {@link Zenchantment} with the specified {@link NamespacedKey} for the {@link Player} this {@link
     * PlayerData} is linked to.
     *
     * @param zenchantmentKey
     *     The {@link NamespacedKey} of the {@link Zenchantment} to enable.
     */
    void enableZenchantment(@NotNull NamespacedKey zenchantmentKey);

    /**
     * Enables all {@link Zenchantment Zenchantments} for the {@link Player} this {@link PlayerData} is linked to.
     */
    void enableAllZenchantments();

    /**
     * Disables the {@link Zenchantment} with the specified {@link NamespacedKey} for the {@link Player} this {@link
     * PlayerData} is linked to.
     *
     * @param zenchantmentKey
     *     The {@link NamespacedKey} of the {@link Zenchantment} to disable.
     */
    void disableZenchantment(@NotNull NamespacedKey zenchantmentKey);

    /**
     * Disables all {@link Zenchantment Zenchantments} for the {@link Player} this {@link PlayerData} is linked to.
     */
    void disableAllZenchantments();

    /**
     * Gets the cooldown for the {@link Zenchantment} with the specified {@link NamespacedKey} for the {@link Player}
     * this {@link PlayerData} is linked to.
     *
     * @param zenchantmentKey
     *     The {@link NamespacedKey} of the {@link Zenchantment} to get the cooldown for.
     *
     * @return An {@code int} representing the cooldown in ticks for the {@link Zenchantment}.
     */
    int getCooldownForZenchantment(@NotNull NamespacedKey zenchantmentKey);
}