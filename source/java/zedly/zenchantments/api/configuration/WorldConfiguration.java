package zedly.zenchantments.api.configuration;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.Zenchantment;

import java.util.Set;

/**
 * Configuration values that are specific to a {@link World}, as defined in the corresponding {@link World World's}
 * configuration file.
 */
public interface WorldConfiguration {
    @NotNull
    Set<Zenchantment> getZenchantments();

    double getZenchantmentRarity();

    int getMaxZenchantments();

    int getShredDropType();

    boolean isExplosionBlockBreakEnabled();

    boolean isDescriptionLoreEnabled();

    boolean isZenchantmentGlowEnabled();

    @NotNull
    ChatColor getDescriptionColor();

    @NotNull
    ChatColor getEnchantmentColor();

    @NotNull
    ChatColor getCurseColor();
}
