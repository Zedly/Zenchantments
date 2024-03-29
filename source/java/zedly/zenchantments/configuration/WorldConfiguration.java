package zedly.zenchantments.configuration;

import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.Tool;
import zedly.zenchantments.Zenchantment;
import zedly.zenchantments.ZenchantmentClassStorage;
import zedly.zenchantments.ZenchantmentFactory;

import java.util.*;

import static zedly.zenchantments.I18n.translateString;

public class WorldConfiguration implements zedly.zenchantments.api.configuration.WorldConfiguration {
    private final Set<Zenchantment> zenchantments;
    private final Map<String, Zenchantment> nameToEnch;
    private final Map<NamespacedKey, Zenchantment> keyToEnch;
    private final double zenchantmentRarity;
    private final int maxZenchantments;
    private final int shredDrops;
    private final boolean explosionBlockBreakEnabled;
    private final boolean descriptionLoreEnabled;
    private final boolean legacyDescriptionLoreEnabled;
    private final ChatColor descriptionColor;
    private final boolean zenchantmentGlowEnabled;
    private final ChatColor enchantmentColor;
    private final ChatColor curseColor;

    public static WorldConfiguration fromYamlConfiguration(YamlConfiguration yamlConfig) {
        double rarity = (double) (yamlConfig.get("enchant-rarity"));
        double enchantRarity = (rarity / 100.0);
        int maxEnchants = (int) yamlConfig.get("max-enchants");
        boolean explosionBlockBreak = (boolean) yamlConfig.get("explosion-block-break");
        boolean descriptionLore = (boolean) yamlConfig.get("description-lore");
        boolean legacyDescriptionLore = (boolean) yamlConfig.get("fix-legacy-description-lore", true);
        boolean enchantGlow = (boolean) yamlConfig.get("enchantment-glow");
        ChatColor descriptionColor = ChatColor.getByChar("" + yamlConfig.get("description-color"));
        ChatColor enchantColor = ChatColor.getByChar("" + yamlConfig.get("enchantment-color"));
        ChatColor curseColor = ChatColor.getByChar("" + yamlConfig.get("curse-color"));
        int shredDrops;

        descriptionColor = (descriptionColor != null) ? descriptionColor : ChatColor.GREEN;
        enchantColor = enchantColor != null ? enchantColor : ChatColor.GRAY;
        curseColor = curseColor != null ? curseColor : ChatColor.RED;

        switch ((String) yamlConfig.get("shred-drops")) {
            case "all":
                shredDrops = 0;
                break;
            case "block":
                shredDrops = 1;
                break;
            case "none":
                shredDrops = 2;
                break;
            default:
                shredDrops = 0;
        }

        // Transform nested generic data types of YamlConfiguration into fixed types
        Set<Zenchantment> enchantments = new HashSet<>();
        Map<String, LinkedHashMap<String, Object>> configInfo = new HashMap<>();
        for (Map<String, LinkedHashMap<String, Object>> part
            : (List<Map<String, LinkedHashMap<String, Object>>>) yamlConfig.get("enchantments")) {
            for (String name : part.keySet()) {
                configInfo.put(name, part.get(name));
            }
        }

        // Parse configuration into set of Zenchantment objects
        for (Class<? extends Zenchantment> cl : ZenchantmentClassStorage.LIST) {
            try {
                if (configInfo.containsKey(cl.getSimpleName())) {
                    Zenchantment ench = Zenchantment.forFlass(cl);
                    if (ench.checkIfDisabledAndLoadConfig(configInfo.get(cl.getSimpleName()))) {
                        enchantments.add(ench);
                    }
                }
            } catch (Exception ex) {
                throw new WorldConfigurationException("Exception while parsing configuration for enchantment " + cl.getSimpleName(), ex);
            }
        }
        return new WorldConfiguration(enchantments, enchantRarity, maxEnchants, shredDrops, explosionBlockBreak,
            descriptionLore, legacyDescriptionLore, enchantGlow, descriptionColor, enchantColor, curseColor);
    }

    private WorldConfiguration(
        final @NotNull Set<Zenchantment> zenchantments,
        final double zenchantmentRarity,
        final int maxZenchantments,
        final int shredDropsEnabled,
        final boolean explosionBlockBreakEnabled,
        final boolean descriptionLoreEnabled,
        final boolean legacyDescriptionLoreEnabled,
        final boolean zenchantmentGlowEnabled,
        final @NotNull ChatColor descriptionColor,
        final @NotNull ChatColor enchantmentColor,
        final @NotNull ChatColor curseColor
    ) {
        this.zenchantments = zenchantments;
        this.zenchantmentRarity = zenchantmentRarity;
        this.maxZenchantments = maxZenchantments;
        this.shredDrops = shredDropsEnabled;
        this.explosionBlockBreakEnabled = explosionBlockBreakEnabled;
        this.descriptionLoreEnabled = descriptionLoreEnabled;
        this.legacyDescriptionLoreEnabled = legacyDescriptionLoreEnabled;
        this.zenchantmentGlowEnabled = zenchantmentGlowEnabled;
        this.descriptionColor = descriptionColor;
        this.enchantmentColor = enchantmentColor;
        this.curseColor = curseColor;

        this.nameToEnch = new HashMap<>();
        for (Zenchantment ench : this.zenchantments) {
            this.nameToEnch.put(ChatColor.stripColor(ench.getName().toLowerCase()), ench);
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
    public int getShredDropType() {
        return shredDrops;
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
    public boolean isLegacyDescriptionLoreEnabled() {
        return this.legacyDescriptionLoreEnabled;
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

    public Zenchantment getZenchantmentFromNameOrKey(final @NotNull String nameOrKey) {
        Zenchantment zen = nameToEnch.get(ChatColor.stripColor(nameOrKey).toLowerCase());
        if (zen != null) {
            return zen;
        }
        NamespacedKey key = NamespacedKey.fromString("zenchantments:" + ChatColor.stripColor(nameOrKey).toLowerCase());
        return keyToEnch.get(key);
    }

    public List<String> getEnchantNames() {
        return new ArrayList<>(nameToEnch.keySet());
    }

    public Set<Map.Entry<String, Zenchantment>> getSimpleMappings() {
        return nameToEnch.entrySet();
    }

    public static class WorldConfigurationException extends RuntimeException {

        public WorldConfigurationException(String message, Exception cause) {
            super(message, cause);
        }
    }
}
