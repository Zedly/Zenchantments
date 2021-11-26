package zedly.zenchantments;

import org.bukkit.ChatColor;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import zedly.zenchantments.configuration.GlobalConfiguration;
import zedly.zenchantments.configuration.WorldConfiguration;
import zedly.zenchantments.configuration.WorldConfigurationProvider;
import zedly.zenchantments.enchantments.*;
import zedly.zenchantments.player.PlayerData;
import zedly.zenchantments.player.PlayerDataProvider;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Objects.requireNonNull;
import static org.bukkit.Material.BOOK;
import static org.bukkit.Material.ENCHANTED_BOOK;

public abstract class Zenchantment implements Keyed, zedly.zenchantments.api.Zenchantment {
    private static final Pattern ZENCHANTMENT_LORE_PATTERN = Pattern.compile(
        "§[a-fA-F0-9]([^§]+?)(?:$| $| (I|II|III|IV|V|VI|VII|VIII|IX|X)$)"
    );

    private final Set<Tool>           enchantable;
    private final int                 maxLevel;
    private final int                 cooldown;
    private final double              power;
    private final float               probability;

    private boolean used;
    private boolean cursed;

    protected Zenchantment(
        final @NotNull Set<Tool> enchantable,
        final int maxLevel,
        final int cooldown,
        final double power,
        final float probability
    ) {
        this.enchantable = enchantable;
        this.maxLevel = maxLevel;
        this.cooldown = cooldown;
        this.power = power;
        this.probability = probability;
    }

    //region Static Methods
    public static void applyForTool(
        final @NotNull Player player,
        final @NotNull PlayerDataProvider playerDataProvider,
        final @NotNull GlobalConfiguration globalConfiguration,
        final @NotNull WorldConfigurationProvider worldConfigurationProvider,
        final @NotNull ItemStack tool,
        final @NotNull BiPredicate<Zenchantment, Integer> action
    ) {
        requireNonNull(player);
        requireNonNull(playerDataProvider);
        requireNonNull(worldConfigurationProvider);
        requireNonNull(tool);
        requireNonNull(action);

        final Map<Zenchantment, Integer> zenchantments = getZenchantmentsOnItemStack(
            tool,
            globalConfiguration,
            worldConfigurationProvider.getConfigurationForWorld(player.getWorld())
        );

        for (final Map.Entry<Zenchantment, Integer> entry : zenchantments.entrySet()) {
            final Zenchantment zenchantment = entry.getKey();
            final Integer level = entry.getValue(); // Use Integer to prevent unboxing and then re-boxing.
            final PlayerData playerData = playerDataProvider.getDataForPlayer(player);

            if (!zenchantment.used && Utilities.playerCanUseZenchantment(player, playerData, zenchantment.getKey())) {
                try {
                    zenchantment.used = true;
                    if (action.test(zenchantment, level)) {
                        playerData.setCooldown(zenchantment.getKey(), zenchantment.cooldown);
                    }
                } catch (Exception ex) {
                    // This is absolutely terrible.
                    // TODO: Fix this monstrosity.
                    ex.printStackTrace();
                }

                zenchantment.used = false;
            }
        }
    }

    @NotNull
    public static Map<Zenchantment, Integer> getZenchantmentsOnItemStack(
        final @NotNull ItemStack itemStack,
        final @NotNull GlobalConfiguration globalConfiguration,
        final @NotNull WorldConfiguration worldConfiguration,
        final @NotNull List<String> outExtraLore
    ) {
        return getZenchantmentsOnItemStack(itemStack, false, globalConfiguration, worldConfiguration, outExtraLore);
    }

    @NotNull
    public static Map<Zenchantment, Integer> getZenchantmentsOnItemStack(
        final @NotNull ItemStack itemStack,
        final boolean acceptBooks,
        final @NotNull GlobalConfiguration globalConfiguration,
        final @NotNull WorldConfiguration worldConfiguration
    ) {
        return getZenchantmentsOnItemStack(itemStack, acceptBooks, globalConfiguration, worldConfiguration, null);
    }

    @NotNull
    public static Map<Zenchantment, Integer> getZenchantmentsOnItemStack(
        final @NotNull ItemStack itemStack,
        final @NotNull GlobalConfiguration globalConfiguration,
        final @NotNull WorldConfiguration worldConfiguration
    ) {
        return getZenchantmentsOnItemStack(itemStack, false, globalConfiguration, worldConfiguration, null);
    }

    @NotNull
    public static Map<Zenchantment, Integer> getZenchantmentsOnItemStack(
        final @NotNull ItemStack itemStack,
        final boolean acceptBooks,
        final @NotNull GlobalConfiguration globalConfiguration,
        final @NotNull WorldConfiguration worldConfiguration,
        final @Nullable List<String> outExtraLore
    ) {
        final Map<Zenchantment, Integer> map = new LinkedHashMap<>();

        if ((!acceptBooks && itemStack.getType() == Material.ENCHANTED_BOOK)
            || !itemStack.hasItemMeta()
            || !requireNonNull(itemStack.getItemMeta()).hasLore()
        ) {
            return Collections.emptyMap();
        }

        for (String raw : requireNonNull(itemStack.getItemMeta().getLore())) {
            final Map.Entry<Zenchantment, Integer> zenchantment = getZenchantmentFromString(raw, worldConfiguration);
            if (zenchantment != null) {
                map.put(zenchantment.getKey(), zenchantment.getValue());
            } else {
                if (outExtraLore != null) {
                    outExtraLore.add(raw);
                }
            }
        }

        final Map<Zenchantment, Integer> finalMap = new LinkedHashMap<>();

        // What does this part even do exactly?
        // Can it be removed?
        for (final String key : new String[] { Lumber.KEY, Shred.KEY, Mow.KEY, Pierce.KEY, Extraction.KEY, Plough.KEY }) {
            Zenchantment zenchantment = null;

            for (final Zenchantment configured : globalConfiguration.getConfiguredZenchantments()) {
                if (configured.getKey().getKey().equals(key)) {
                    zenchantment = configured;
                    break;
                }
            }

            if (map.containsKey(zenchantment)) {
                finalMap.put(zenchantment, map.get(zenchantment));
                map.remove(zenchantment);
            }
        }

        finalMap.putAll(map);

        return finalMap;
    }

    @Nullable
    private static Map.Entry<Zenchantment, Integer> getZenchantmentFromString(
        final @NotNull String raw,
        final @NotNull WorldConfiguration config
    ) {
        final Matcher matcher = ZENCHANTMENT_LORE_PATTERN.matcher(raw);

        if (!matcher.find()) {
            return null;
        }

        final String enchantmentName = ChatColor.stripColor(matcher.group(1));
        final Zenchantment zenchantment = config.getZenchantmentFromName(enchantmentName);
        if (zenchantment == null) {
            return null;
        }

        final int level = matcher.group(2) == null || matcher.group(2).equals("") ? 1 : Utilities.convertNumeralToInt(matcher.group(2));

        return new AbstractMap.SimpleEntry<>(zenchantment, level);
    }

    public static boolean isDescription(final @NotNull String string) {
        requireNonNull(string);

        for (final Map.Entry<String, Boolean> entry : Utilities.fromInvisibleString(string).entrySet()) {
            if (entry.getValue()) {
                continue;
            }

            final String[] values = entry.getKey().split("\\.");
            if (values.length == 3 && values[0].equals("ze") && values[1].equals("desc")) {
                return true;
            }
        }
        return false;
    }

    public static void setZenchantmentForItemStack(
        final @Nullable ItemStack stack,
        final @Nullable Zenchantment zenchantment,
        final int level,
        final @NotNull WorldConfiguration worldConfiguration
    ) {
        requireNonNull(worldConfiguration);

        if (stack == null) {
            return;
        }

        final ItemMeta meta = requireNonNull(stack.getItemMeta());
        final List<String> lore = new LinkedList<>();
        final List<String> normalLore = new LinkedList<>();

        boolean isZenchantment = false;

        if (meta.hasLore()) {
            for (final String line : requireNonNull(meta.getLore())) {
                Map.Entry<Zenchantment, Integer> zenchantmentEntry = getZenchantmentFromString(line, worldConfiguration);
                if (zenchantmentEntry == null && !Zenchantment.isDescription(line)) {
                    normalLore.add(line);
                } else if (zenchantmentEntry != null && zenchantmentEntry.getKey() != zenchantment) {
                    isZenchantment = true;
                    lore.add(zenchantmentEntry.getKey().getShown(zenchantmentEntry.getValue(), worldConfiguration));
                    lore.addAll(zenchantmentEntry.getKey().getDescription(worldConfiguration));
                }
            }
        }

        if (zenchantment != null && level > 0 && level <= zenchantment.maxLevel) {
            lore.add(zenchantment.getShown(level, worldConfiguration));
            lore.addAll(zenchantment.getDescription(worldConfiguration));
            isZenchantment = true;
        }

        lore.addAll(normalLore);
        meta.setLore(lore);
        stack.setItemMeta(meta);

        if (isZenchantment && stack.getType() == BOOK) {
            stack.setType(ENCHANTED_BOOK);
        }

        updateEnchantmentGlowForItemStack(stack, isZenchantment, worldConfiguration);
    }

    public static void updateEnchantmentGlowForItemStack(
        final @NotNull ItemStack stack,
        final boolean zenchantment,
        final @NotNull WorldConfiguration worldConfiguration
    ) {
        if (!worldConfiguration.isZenchantmentGlowEnabled()) {
            return;
        }

        final ItemMeta itemMeta = requireNonNull(stack.getItemMeta());

        EnchantmentStorageMeta bookMeta = null;

        final boolean book = stack.getType() == BOOK || stack.getType() == ENCHANTED_BOOK;

        boolean containsNormal = false;
        boolean containsHidden = false;
        int durabilityLevel = 0;
        Map<Enchantment, Integer> enchantments;

        if (stack.getType() == ENCHANTED_BOOK) {
            bookMeta = (EnchantmentStorageMeta) stack.getItemMeta();
            enchantments = bookMeta.getStoredEnchants();
        } else {
            enchantments = itemMeta.getEnchants();
        }

        for (Map.Entry<Enchantment, Integer> set : enchantments.entrySet()) {
            if (!(set.getKey().equals(Enchantment.DURABILITY) && (durabilityLevel = set.getValue()) == 0)) {
                containsNormal = true;
            } else {
                containsHidden = true;
            }
        }

        if (containsNormal || (!zenchantment && containsHidden)) {
            if (stack.getType() == ENCHANTED_BOOK) {
                if (durabilityLevel == 0) {
                    bookMeta.removeStoredEnchant(Enchantment.DURABILITY);
                }
                bookMeta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
            } else {
                if (durabilityLevel == 0) {
                    itemMeta.removeEnchant(Enchantment.DURABILITY);
                }
                itemMeta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
        } else if (zenchantment) {
            if (stack.getType() == BOOK) {
                stack.setType(ENCHANTED_BOOK);
                bookMeta = (EnchantmentStorageMeta) stack.getItemMeta();
                bookMeta.addStoredEnchant(Enchantment.DURABILITY, 0, true);
                bookMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            } else {
                itemMeta.addEnchant(Enchantment.DURABILITY, 0, true);
                itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
        }

        stack.setItemMeta(book ? bookMeta : itemMeta);
    }
    //endregion

    @Override
    @NotNull
    public final Set<Tool> getEnchantable() {
        return this.enchantable;
    }

    @Override
    public final int getMaxLevel() {
        return this.maxLevel;
    }

    @Override
    public final int getCooldown() {
        return this.cooldown;
    }

    @Override
    public final double getPower() {
        return this.power;
    }

    @Override
    public final float getProbability() {
        return this.probability;
    }

    //region Enchantment Events
    public boolean onBlockBreak(final @NotNull BlockBreakEvent event, final int level, final boolean usedHand) {
        return false;
    }

    public boolean onBlockPlace(final @NotNull BlockPlaceEvent event, final int level, final boolean usedHand) {
        return false;
    }

    public boolean onBlockInteract(final @NotNull PlayerInteractEvent event, final int level, final boolean usedHand) {
        return false;
    }

    public boolean onBlockInteractInteractable(final @NotNull PlayerInteractEvent event, final int level, final boolean usedHand) {
        return false;
    }

    public boolean onEntityInteract(final @NotNull PlayerInteractEntityEvent event, final int level, final boolean usedHand) {
        return false;
    }

    public boolean onEntityKill(final @NotNull EntityDeathEvent event, final int level, final boolean usedHand) {
        return false;
    }

    public boolean onEntityHit(final @NotNull EntityDamageByEntityEvent event, final int level, final boolean usedHand) {
        return false;
    }

    public boolean onBeingHit(final @NotNull EntityDamageByEntityEvent event, final int level, final boolean usedHand) {
        return false;
    }

    public boolean onEntityDamage(final @NotNull EntityDamageEvent event, final int level, final boolean usedHand) {
        return false;
    }

    public boolean onPlayerFish(final @NotNull PlayerFishEvent event, final int level, final boolean usedHand) {
        return false;
    }

    public boolean onHungerChange(final @NotNull FoodLevelChangeEvent event, final int level, final boolean usedHand) {
        return false;
    }

    public boolean onShear(final @NotNull PlayerShearEntityEvent event, final int level, final boolean usedHand) {
        return false;
    }

    public boolean onEntityShootBow(final @NotNull EntityShootBowEvent event, final int level, final boolean usedHand) {
        return false;
    }

    public boolean onPotionSplash(final @NotNull PotionSplashEvent event, final int level, final boolean usedHand) {
        return false;
    }

    public boolean onProjectileLaunch(final @NotNull ProjectileLaunchEvent event, final int level, final boolean usedHand) {
        return false;
    }

    public boolean onPlayerDeath(final @NotNull PlayerDeathEvent event, final int level, final boolean usedHand) {
        return false;
    }

    public boolean onCombust(final @NotNull EntityCombustByEntityEvent event, final int level, final boolean usedHand) {
        return false;
    }

    public boolean onScan(final @NotNull Player player, final int level, final boolean usedHand) {
        return false;
    }

    public boolean onScanHands(final @NotNull Player player, final int level, final boolean usedHand) {
        return false;
    }

    public boolean onFastScan(final @NotNull Player player, final int level, final boolean usedHand) {
        return false;
    }

    public boolean onFastScanHands(final @NotNull Player player, final int level, final boolean usedHand) {
        return false;
    }
    //endregion

    public final boolean isValidMaterial(final @NotNull Material material) {
        for (final Tool tool : this.enchantable) {
            if (tool.contains(material)) {
                return true;
            }
        }

        return false;
    }

    public final boolean isValidMaterial(final @NotNull ItemStack itemStack) {
        return this.isValidMaterial(itemStack.getType());
    }

    @NotNull
    public final String getShown(final int level, final @NotNull WorldConfiguration worldConfiguration) {
        return (this.cursed ? worldConfiguration.getCurseColor() : worldConfiguration.getEnchantmentColor())
            + this.getName()
            + (this.maxLevel == 1 ? " " : " " + Utilities.convertIntToNumeral(level));
    }

    @NotNull
    public final List<String> getDescription(final @NotNull WorldConfiguration worldConfiguration) {
        if (!worldConfiguration.isDescriptionLoreEnabled()) {
            return Collections.emptyList();
        }

        final List<String> description = new LinkedList<>();
        final String start = Utilities.makeStringInvisible("ze.desc." + this.getKey())
            + worldConfiguration.getDescriptionColor()
            + ChatColor.ITALIC
            + " ";

        StringBuilder builder = new StringBuilder();
        int i = 0;

        for (char c : this.getDescription().toCharArray()) {
            if (i < 30) {
                i++;
                builder.append(c);
            } else {
                if (c == ' ') {
                    description.add(start + builder.toString());
                    builder = new StringBuilder(" ");
                    i = 1;
                } else {
                    builder.append(c);
                }
            }
        }

        if (i != 0) {
            description.add(start + builder.toString());
        }

        return description;
    }

    public final void setForItemStack(final @Nullable ItemStack stack, final int level, final @NotNull WorldConfiguration worldConfiguration) {
        setZenchantmentForItemStack(stack, this, level, worldConfiguration);
    }

    @FunctionalInterface
    public interface Constructor<T extends Zenchantment> {
        @NotNull
        @Contract(value = "_, _, _, _, _ -> new", pure = true)
        T construct(
            final @NotNull Set<Tool> enchantable,
            final int maxLevel,
            final int cooldown,
            final double power,
            final float probability
        );
    }
}
