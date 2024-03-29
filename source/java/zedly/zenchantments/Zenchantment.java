package zedly.zenchantments;

import org.bukkit.ChatColor;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import zedly.zenchantments.configuration.WorldConfiguration;
import zedly.zenchantments.configuration.WorldConfigurationProvider;
import zedly.zenchantments.event.listener.EnchantmentFunction;
import zedly.zenchantments.player.PlayerData;
import zedly.zenchantments.player.PlayerDataProvider;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Objects.requireNonNull;
import static org.bukkit.Material.BOOK;
import static org.bukkit.Material.ENCHANTED_BOOK;
import static zedly.zenchantments.I18n.translateString;

public abstract class Zenchantment implements Keyed, zedly.zenchantments.api.Zenchantment {
    private static final Pattern ZENCHANTMENT_LORE_PATTERN = Pattern.compile(
        "§[a-fA-F0-9]([^§]+?)(?:$| $| (I|II|III|IV|V|VI|VII|VIII|IX|X)$)"
    );

    private Set<Class<? extends Zenchantment>> conflicting;
    private Set<Tool> enchantable;
    private String name;
    private String description;
    private Slots applyToSlots;
    private int maxLevel;
    private int cooldown;
    private double power;
    private float probability;
    private NamespacedKey key;

    private boolean recursionLock;
    private boolean cursed = false;

    /*
    protected Zenchantment(
        final @NotNull Set<Tool> enchantable,
        final int maxLevel,
        final int cooldown,
        final double power,
        final float probability,
        final Set<Class<? extends Zenchantment>> conflicting,
        final Collection<EquipmentSlot> applyToSlots
    ) {
        this.enchantable = enchantable;
        this.name = translateString("zenchantment." + getI18nKey() + ".name");
        this.description = translateString("zenchantment." + getI18nKey() + ".description");
        this.maxLevel = maxLevel;
        this.cooldown = cooldown;
        this.power = power;
        this.probability = probability;
        this.conflicting = conflicting;
        this.key = new NamespacedKey(ZenchantmentsPlugin.getInstance(), getI18nKey());
        this.applyToSlots = applyToSlots;
    }
    */

    //region Static Methods
    public static void applyForTool(
        final @NotNull Player player,
        final @Nullable EquipmentSlot slot,
        final @NotNull EnchantmentFunction action
    ) {
        requireNonNull(player);
        requireNonNull(action);
        ItemStack tool = player.getInventory().getItem(slot);
        if (tool == null) {
            return;
        }

        final Map<Zenchantment, Integer> zenchantments = getZenchantmentsOnItemStack(
            tool,
            WorldConfigurationProvider.getInstance().getConfigurationForWorld(player.getWorld())
        );

        for (final Map.Entry<Zenchantment, Integer> entry : zenchantments.entrySet()) {
            final Zenchantment zenchantment = entry.getKey();

            if (!zenchantment.getApplyToSlots().contains(slot)) {
                continue;
            }

            final Integer level = entry.getValue(); // Use Integer to prevent unboxing and then re-boxing.
            final PlayerData playerData = PlayerDataProvider.getDataForPlayer(player);

            if (!zenchantment.recursionLock && Utilities.playerCanUseZenchantment(player, playerData, zenchantment.getKey())) {
                zenchantment.recursionLock = true;
                if (action.run(zenchantment, level, slot)) {
                    playerData.setCooldown(zenchantment.getKey(), zenchantment.cooldown);
                }
                // Fixed it
                zenchantment.recursionLock = false;
            }
        }
    }

    @NotNull
    public static Map<Zenchantment, Integer> getZenchantmentsOnItemStack(
        final @Nullable ItemStack itemStack,
        final boolean acceptBooks,
        final @NotNull WorldConfiguration worldConfiguration
    ) {
        return getZenchantmentsOnItemStack(itemStack, acceptBooks, worldConfiguration, null);
    }

    @NotNull
    public static Map<Zenchantment, Integer> getZenchantmentsOnItemStack(
        final @Nullable ItemStack itemStack,
        final @NotNull WorldConfiguration worldConfiguration
    ) {
        return getZenchantmentsOnItemStack(itemStack, false, worldConfiguration, null);
    }

    @NotNull
    public static Map<Zenchantment, Integer> getZenchantmentsOnItemStack(
        final @Nullable ItemStack itemStack,
        final boolean acceptBooks,
        final @NotNull WorldConfiguration worldConfiguration,
        final @Nullable List<String> outExtraLore
    ) {
        if (itemStack == null) {
            return Collections.emptyMap();
        }

        final Map<Zenchantment, Integer> earlyMap = new LinkedHashMap<>();
        final Map<Zenchantment, Integer> map = new LinkedHashMap<>();
        final Map<Zenchantment, Integer> lateMap = new LinkedHashMap<>();

        if ((!acceptBooks && itemStack.getType() == Material.ENCHANTED_BOOK)
            || !itemStack.hasItemMeta()
            || !requireNonNull(itemStack.getItemMeta()).hasLore()
        ) {
            return Collections.emptyMap();
        }

        for (String raw : requireNonNull(itemStack.getItemMeta().getLore())) {
            final Map.Entry<Zenchantment, Integer> zenchantment = getZenchantmentFromString(raw, worldConfiguration);
            if (zenchantment != null) {
                switch (zenchantment.getKey().getPriority()) {
                    case EARLY:
                        earlyMap.put(zenchantment.getKey(), zenchantment.getValue());
                        break;
                    case NORMAL:
                        map.put(zenchantment.getKey(), zenchantment.getValue());
                        break;
                    case LATE:
                        lateMap.put(zenchantment.getKey(), zenchantment.getValue());
                        break;
                }

            } else if (outExtraLore != null) {
                if (!isDescription(raw, worldConfiguration)) {
                    outExtraLore.add(raw);
                }
            }
        }

        earlyMap.putAll(map);
        earlyMap.putAll(lateMap);
        return earlyMap;
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

    public static boolean isDescription(final @NotNull String string, WorldConfiguration config) {
        requireNonNull(string);

        for (Zenchantment zen : config.getZenchantments()) {
            // Match description with only active color codes
            for (String descriptionLine : zen.getDescription(config)) {
                if (string.equals(descriptionLine)) {
                    return true;
                }
            }
            if (config.isDescriptionLoreEnabled())
                // Match old description with corrupted invisible color codes
                for (String descriptionLine : zen.getOldDescription(config)) {
                    if (string.equals(WorldInteractionUtil.reproduceCorruptedInvisibleSequence(descriptionLine))) {
                        return true;
                    }
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
                if (zenchantmentEntry == null && !Zenchantment.isDescription(line, worldConfiguration)) {
                    normalLore.add(line);
                } else if (zenchantmentEntry != null && zenchantmentEntry.getKey() != zenchantment) {
                    isZenchantment = true;
                    lore.add(zenchantmentEntry.getKey().getMainEnchantmentString(zenchantmentEntry.getValue(), worldConfiguration));
                    lore.addAll(zenchantmentEntry.getKey().getDescription(worldConfiguration));
                }
            }
        }

        if (zenchantment != null && level > 0 && level <= zenchantment.maxLevel) {
            lore.add(zenchantment.getMainEnchantmentString(level, worldConfiguration));
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

    public static String keyForClass(Class<? extends Zenchantment> enchClazz) {
        return enchClazz.getSimpleName().toLowerCase(Locale.ROOT);
    }

    public static <T extends Zenchantment> T forFlass(Class<T> clazz) throws NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        T ench = clazz.getConstructor().newInstance();
        return ench;
    }

    //endregion

    public boolean checkIfDisabledAndLoadConfig(LinkedHashMap<String, Object> data) {
        if (probability == -1) {
            return false;
        }

        AZenchantment az = this.getClass().getAnnotation(AZenchantment.class);
        this.name = translateString("zenchantment." + getI18nKey() + ".name");
        this.description = translateString("zenchantment." + getI18nKey() + ".description");
        this.key = new NamespacedKey(ZenchantmentsPlugin.getInstance(), getI18nKey());
        this.applyToSlots = az.runInSlots();
        this.conflicting = Set.of(az.conflicting());
        this.probability = (float) (double) data.getOrDefault("probability", 0.0);
        this.cooldown = (int) data.get("cooldown");
        this.maxLevel = (int) data.get("max-level");
        this.power = (double) data.get("power");
        this.enchantable = new HashSet<>();
        for (String s : ((String) data.get("tools")).split("\\W*,\\W*")) { // comma surrounded by arbitrary whitespaces
            this.enchantable.add(Tool.fromString(s));
        }
        return true;
    }

    public final String getName() {
        return name;
    }

    public final String getDescription() {
        return description;
    }

    public final NamespacedKey getKey() {
        return key;
    }

    public final String getI18nKey() {
        return keyForClass(this.getClass());
    }

    public final Set<Class<? extends Zenchantment>> getConflicting() {
        return conflicting;
    }

    public final Slots getApplyToSlots() {
        return applyToSlots;
    }

    @NotNull
    public final Set<Tool> getEnchantable() {
        return this.enchantable;
    }

    public final int getMaxLevel() {
        return this.maxLevel;
    }

    public final int getCooldown() {
        return this.cooldown;
    }

    public final double getPower() {
        return this.power;
    }

    public final float getProbability() {
        return this.probability;
    }

    public ZenchantmentPriority getPriority() {
        return ZenchantmentPriority.NORMAL;
    }

    //region Enchantment Events
    public boolean onBlockBreak(final @NotNull BlockBreakEvent event, final int level, final EquipmentSlot slot) {
        return false;
    }

    public boolean onBlockPlace(final @NotNull BlockPlaceEvent event, final int level, final EquipmentSlot slot) {
        return false;
    }

    public boolean onBlockPlaceOtherHand(final @NotNull BlockPlaceEvent event, final int level, final EquipmentSlot slot) {
        return false;
    }

    public boolean onBlockInteract(final @NotNull PlayerInteractEvent event, final int level, final EquipmentSlot slot) {
        return false;
    }

    public boolean onBlockInteractInteractable(final @NotNull PlayerInteractEvent event, final int level, final EquipmentSlot slot) {
        return false;
    }

    public boolean onEntityInteract(final @NotNull PlayerInteractEntityEvent event, final int level, final EquipmentSlot slot) {
        return false;
    }

    public boolean onEntityKill(final @NotNull EntityDeathEvent event, final int level, final EquipmentSlot slot) {
        return false;
    }

    public boolean onEntityHit(final @NotNull EntityDamageByEntityEvent event, final int level, final EquipmentSlot slot) {
        return false;
    }

    public boolean onBeingHit(final @NotNull EntityDamageByEntityEvent event, final int level, final EquipmentSlot slot) {
        return false;
    }

    public boolean onEntityDamage(final @NotNull EntityDamageEvent event, final int level, final EquipmentSlot slot) {
        return false;
    }

    public boolean onPlayerFish(final @NotNull PlayerFishEvent event, final int level, final EquipmentSlot slot) {
        return false;
    }

    public boolean onHungerChange(final @NotNull FoodLevelChangeEvent event, final int level, final EquipmentSlot slot) {
        return false;
    }

    public boolean onShear(final @NotNull PlayerShearEntityEvent event, final int level, final EquipmentSlot slot) {
        return false;
    }

    public boolean onEntityShootBow(final @NotNull EntityShootBowEvent event, final int level, final EquipmentSlot slot) {
        return false;
    }

    public boolean onHitByProjectile(final @NotNull ProjectileHitEvent event, final int level, final EquipmentSlot slot) {
        return false;
    }

    public boolean onPotionSplash(final @NotNull PotionSplashEvent event, final int level, final EquipmentSlot slot) {
        return false;
    }

    public boolean onProjectileLaunch(final @NotNull ProjectileLaunchEvent event, final int level, final EquipmentSlot slot) {
        return false;
    }

    public boolean onPlayerDeath(final @NotNull PlayerDeathEvent event, final int level, final EquipmentSlot slot) {
        return false;
    }

    public boolean onCombust(final @NotNull EntityCombustByEntityEvent event, final int level, final EquipmentSlot slot) {
        return false;
    }

    public boolean onScan(final @NotNull Player player, final int level, final EquipmentSlot slot) {
        return false;
    }

    public boolean onFastScan(final @NotNull Player player, final int level, final EquipmentSlot slot) {
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
    public final String getMainEnchantmentString(final int level, final @NotNull WorldConfiguration worldConfiguration) {
        return (this.cursed ? worldConfiguration.getCurseColor() : worldConfiguration.getEnchantmentColor())
            + translateString("zenchantment." + getI18nKey() + ".name")
            + (this.maxLevel == 1 ? " " : " " + Utilities.convertIntToNumeral(level));
    }

    @NotNull
    public final List<String> getDescription(final @NotNull WorldConfiguration worldConfiguration) {
        if (!worldConfiguration.isDescriptionLoreEnabled()) {
            return Collections.emptyList();
        }

        final List<String> description = new LinkedList<>();
        final String start = ""
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

    @NotNull
    public final List<String> getOldDescription(final @NotNull WorldConfiguration worldConfiguration) {
        if (!worldConfiguration.isDescriptionLoreEnabled()) {
            return Collections.emptyList();
        }

        final List<String> description = new LinkedList<>();
        final String start = Utilities.makeStringInvisible("ze.desc." + this.getI18nKey())
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
