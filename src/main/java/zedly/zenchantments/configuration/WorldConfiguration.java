package zedly.zenchantments.configuration;

import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.Zenchantment;

import java.util.*;

public class WorldConfiguration implements zedly.zenchantments.api.configuration.WorldConfiguration {
    private final Set<Zenchantment>                zenchantments;
    private final Map<String, Zenchantment>        nameToEnch;
    private final Map<NamespacedKey, Zenchantment> keyToEnch;
    private final double                           zenchantmentRarity;
    private final int                              maxZenchantments;
    private final int                              shredDropsEnabled;
    private final boolean                          explosionBlockBreakEnabled;
    private final boolean                          descriptionLoreEnabled;
    private final ChatColor                        descriptionColor;
    private final boolean                          zenchantmentGlowEnabled;
    private final ChatColor                        enchantmentColor;
    private final ChatColor                        curseColor;

    public WorldConfiguration(
        final @NotNull Set<Zenchantment> zenchantments,
        final double zenchantmentRarity,
        final int maxZenchantments,
        final int shredDropsEnabled,
        final boolean explosionBlockBreakEnabled,
        final boolean descriptionLoreEnabled,
        final boolean zenchantmentGlowEnabled,
        final @NotNull ChatColor descriptionColor,
        final @NotNull ChatColor enchantmentColor,
        final @NotNull ChatColor curseColor
    ) {
        this.zenchantments = zenchantments;
        this.zenchantmentRarity = zenchantmentRarity;
        this.maxZenchantments = maxZenchantments;
        this.shredDropsEnabled = shredDropsEnabled;
        this.explosionBlockBreakEnabled = explosionBlockBreakEnabled;
        this.descriptionLoreEnabled = descriptionLoreEnabled;
        this.zenchantmentGlowEnabled = zenchantmentGlowEnabled;
        this.descriptionColor = descriptionColor;
        this.enchantmentColor = enchantmentColor;
        this.curseColor = curseColor;

        this.nameToEnch = new HashMap<>();
        for (Zenchantment ench : this.zenchantments) {
            this.nameToEnch.put(ChatColor.stripColor(ench.getName().toLowerCase().replace(" ", "")), ench);
        }

        this.keyToEnch = new HashMap<>();
        for (Zenchantment ench : this.zenchantments) {
            this.keyToEnch.put(ench.getKey(), ench);
        }
    }

    @Override
    @NotNull
    public Set<Zenchantment> getZenchantments() {
        return this.zenchantments;
    }

    @Override
    public double getZenchantmentRarity() {
        return this.zenchantmentRarity;
    }

    @Override
    public int getMaxZenchantments() {
        return this.maxZenchantments;
    }

    @Override
    public int areShredDropsEnabled() {
        return shredDropsEnabled;
    }

    @Override
    public boolean isExplosionBlockBreakEnabled() {
        return this.explosionBlockBreakEnabled;
    }

    @Override
    public boolean isDescriptionLoreEnabled() {
        return this.descriptionLoreEnabled;
    }

    @Override
    public boolean isZenchantmentGlowEnabled() {
        return this.zenchantmentGlowEnabled;
    }

    @Override
    @NotNull
    public ChatColor getDescriptionColor() {
        return this.descriptionColor;
    }

    @Override
    @NotNull
    public ChatColor getEnchantmentColor() {
        return this.enchantmentColor;
    }

    @Override
    @NotNull
    public ChatColor getCurseColor() {
        return this.curseColor;
    }

    public Zenchantment getZenchantmentFromName(final @NotNull String name) {
        return this.nameToEnch.get(ChatColor.stripColor(name).toLowerCase());
    }

    public List<String> getEnchantNames() {
        return new ArrayList<>(nameToEnch.keySet());
    }

    public Set<Map.Entry<String, Zenchantment>> getSimpleMappings() {
        return nameToEnch.entrySet();
    }
}