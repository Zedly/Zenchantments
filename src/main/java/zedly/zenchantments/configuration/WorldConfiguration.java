package zedly.zenchantments.configuration;

import org.bukkit.ChatColor;
import zedly.zenchantments.Zenchantment;

import java.util.*;

public class WorldConfiguration implements zedly.zenchantments.api.configuration.WorldConfiguration {
    private final Set<Zenchantment>          worldEnchants;
    private final Map<String, Zenchantment>  nameToEnch;
    private final Map<Integer, Zenchantment> idToEnch;
    private final double                     enchantRarity;
    private final int                        maxEnchants;
    private final int                        shredDrops;
    private final boolean                    explosionBlockBreak;
    private final boolean                    descriptionLore;
    private final ChatColor                  descriptionColor;
    private final boolean                    enchantGlow;
    private final ChatColor                  enchantmentColor;
    private final ChatColor                  curseColor;

    public WorldConfiguration(
        Set<Zenchantment> worldEnchants,
        double enchantRarity,
        int maxEnchants,
        int shredDrops,
        boolean explosionBlockBreak,
        boolean descriptionLore,
        ChatColor descriptionColor,
        ChatColor enchantmentColor,
        ChatColor curseColor,
        boolean enchantGlow
    ) {
        this.worldEnchants = worldEnchants;
        this.enchantRarity = enchantRarity;
        this.maxEnchants = maxEnchants;
        this.shredDrops = shredDrops;
        this.explosionBlockBreak = explosionBlockBreak;
        this.descriptionLore = descriptionLore;
        this.descriptionColor = descriptionColor;

        this.nameToEnch = new HashMap<>();
        for (Zenchantment ench : this.worldEnchants) {
            nameToEnch.put(ChatColor.stripColor(ench.getName().toLowerCase().replace(" ", "")), ench);
        }

        this.idToEnch = new HashMap<>();
        for (Zenchantment ench : this.worldEnchants) {
            idToEnch.put(ench.getId(), ench);
        }

        this.enchantGlow = enchantGlow;
        this.enchantmentColor = enchantmentColor;
        this.curseColor = curseColor;
    }

    public Set<Zenchantment> getEnchants() {
        return worldEnchants;
    }

    public double getEnchantRarity() {
        return enchantRarity;
    }

    public int getMaxEnchants() {
        return maxEnchants;
    }

    public int getShredDrops() {
        return shredDrops;
    }

    public boolean explosionBlockBreak() {
        return explosionBlockBreak;
    }

    public boolean descriptionLore() {
        return descriptionLore;
    }

    public ChatColor getDescriptionColor() {
        return descriptionColor;
    }

    public boolean enchantGlow() {
        return enchantGlow;
    }

    public ChatColor getEnchantmentColor() {
        return enchantmentColor;
    }

    public ChatColor getCurseColor() {
        return curseColor;
    }

    public Zenchantment enchantFromString(String enchName) {
        return nameToEnch.get(ChatColor.stripColor(enchName.toLowerCase()));
    }

    public List<String> getEnchantNames() {
        return new ArrayList<>(nameToEnch.keySet());
    }

    public Set<Map.Entry<String, Zenchantment>> getSimpleMappings() {
        return nameToEnch.entrySet();
    }
}