package zedly.zenchantments;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import zedly.zenchantments.compatibility.CompatibilityAdapter;
import zedly.zenchantments.enchantments.*;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.bukkit.Material.BOOK;
import static org.bukkit.Material.ENCHANTED_BOOK;

public abstract class Zenchantment implements zedly.zenchantments.api.Zenchantment {
    private static final Pattern ENCH_LORE_PATTERN = Pattern.compile("§[a-fA-F0-9]([^§]+?)(?:$| $| (I|II|III|IV|V|VI|VII|VIII|IX|X)$)");

    protected static final CompatibilityAdapter ADAPTER = Storage.COMPATIBILITY_ADAPTER;

    protected int        id;
    protected int        maxLevel;
    protected String     loreName;
    protected float      probability;
    protected Tool[]     enchantable;
    protected Class<?>[] conflicting;
    protected String     description;
    protected int        cooldown;
    protected double     power;
    protected Hand       handUse;
    private   boolean    used;
    protected boolean    isCursed;

    public abstract Builder<? extends Zenchantment> defaults();

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public String getLoreName() {
        return this.loreName;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public int getMaxLevel() {
        return this.maxLevel;
    }

    @Override
    public int getCooldown() {
        return this.cooldown;
    }

    @Override
    public double getPower() {
        return this.power;
    }

    @Override
    public float getProbability() {
        return this.probability;
    }

    @Override
    public Tool[] getEnchantable() {
        return this.enchantable;
    }

    @Override
    public Class<?>[] getConflicting() {
        return this.conflicting;
    }

    @Override
    public Hand getHandUse() {
        return handUse;
    }

    //region Enchantment Events
    public boolean onBlockBreak(BlockBreakEvent event, int level, boolean usedHand) {
        return false;
    }

    public boolean onBlockInteract(PlayerInteractEvent event, int level, boolean usedHand) {
        return false;
    }

    public boolean onBlockInteractInteractable(PlayerInteractEvent event, int level, boolean usedHand) {
        return false;
    }

    public boolean onEntityInteract(PlayerInteractEntityEvent event, int level, boolean usedHand) {
        return false;
    }

    public boolean onEntityKill(EntityDeathEvent event, int level, boolean usedHand) {
        return false;
    }

    public boolean onEntityHit(EntityDamageByEntityEvent event, int level, boolean usedHand) {
        return false;
    }

    public boolean onBeingHit(EntityDamageByEntityEvent event, int level, boolean usedHand) {
        return false;
    }

    public boolean onEntityDamage(EntityDamageEvent event, int level, boolean usedHand) {
        return false;
    }

    public boolean onPlayerFish(PlayerFishEvent event, int level, boolean usedHand) {
        return false;
    }

    public boolean onHungerChange(FoodLevelChangeEvent event, int level, boolean usedHand) {
        return false;
    }

    public boolean onShear(PlayerShearEntityEvent event, int level, boolean usedHand) {
        return false;
    }

    public boolean onEntityShootBow(EntityShootBowEvent event, int level, boolean usedHand) {
        return false;
    }

    public boolean onPotionSplash(PotionSplashEvent event, int level, boolean usedHand) {
        return false;
    }

    public boolean onProjectileLaunch(ProjectileLaunchEvent event, int level, boolean usedHand) {
        return false;
    }

    public boolean onPlayerDeath(PlayerDeathEvent event, int level, boolean usedHand) {
        return false;
    }

    public boolean onScan(Player player, int level, boolean usedHand) {
        return false;
    }

    public boolean onScanHands(Player player, int level, boolean usedHand) {
        return false;
    }

    public boolean onCombust(EntityCombustByEntityEvent event, int level, boolean usedHand) {
        return false;
    }

    public boolean onFastScan(Player player, int level, boolean usedHand) {
        return false;
    }

    public boolean onFastScanHands(Player player, int level, boolean usedHand) {
        return false;
    }
    //endregion

    public static void applyForTool(Player player, ItemStack tool, BiPredicate<Zenchantment, Integer> action) {
        Zenchantment.getEnchants(tool, player.getWorld()).forEach((Zenchantment ench, Integer level) -> {
            if (!ench.used && Utilities.canUse(player, ench.id)) {
                try {
                    ench.used = true;
                    if (action.test(ench, level)) {
                        EnchantPlayer.matchPlayer(player).setCooldown(ench.id, ench.cooldown);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                ench.used = false;
            }
        });
    }

    // Updates lore enchantments and descriptions to new format. This will be removed eventually
    public static ItemStack updateToNewFormat(ItemStack itemStack, World world) {
        if (itemStack == null) {
            return null;
        }

        if (!itemStack.hasItemMeta()) {
            return itemStack;
        }

        if (!itemStack.getItemMeta().hasLore()) {
            return itemStack;
        }

        boolean hasEnch = false;
        ItemMeta meta = itemStack.getItemMeta();
        List<String> lore = new ArrayList<>();
        Zenchantment lastEnch = null;

        List<String> tempLore = new LinkedList<>();
        for (String string : meta.getLore()) {
            Zenchantment ench = null;
            int level = 0;

            if (string.startsWith(ChatColor.GRAY.toString())) {
                String stripString = ChatColor.stripColor(string);

                int splitIndex = stripString.lastIndexOf(" ");
                if (splitIndex != -1) {
                    if (stripString.length() > 2) {
                        String enchant;
                        level = Utilities.getNumber(stripString.substring(splitIndex + 1));
                        try {
                            enchant = stripString.substring(0, splitIndex);
                        } catch (Exception e) {
                            enchant = "";
                        }
                        ench = Config.get(world).enchantFromString(enchant);
                    }
                }
            }

            if (ench != null) {
                lastEnch = ench;
                hasEnch = true;
                lore.add(ench.getShown(level, world));
                lore.addAll(tempLore);
                tempLore.clear();
                continue;
            }

            if (lastEnch != null) {
                tempLore.add(string);

                StringBuilder builder = new StringBuilder();

                for (String loreLine : tempLore) {
                    builder.append(ChatColor.stripColor(loreLine));
                }

                if (lastEnch.description.equals(builder.toString())) {
                    lastEnch = null;
                    tempLore.clear();
                }
            } else {
                lore.add(string);
            }
        }

        lore.addAll(tempLore);
        meta.setLore(lore);
        itemStack.setItemMeta(meta);

        if (hasEnch) {
            Zenchantment.setGlow(itemStack, true, world);
        }

        return itemStack;
    }

    // Returns a mapping of custom enchantments and their level on a given tool
    public static Map<Zenchantment, Integer> getEnchants(
        ItemStack itemStack,
        World world,
        List<String> outExtraLore
    ) {
        return Zenchantment.getEnchants(itemStack, false, world, outExtraLore);
    }

    // Returns a mapping of custom enchantments and their level on a given tool
    public static Map<Zenchantment, Integer> getEnchants(
        ItemStack itemStack,
        boolean acceptBooks,
        World world
    ) {
        return Zenchantment.getEnchants(itemStack, acceptBooks, world, null);
    }

    // Returns a mapping of custom enchantments and their level on a given tool
    public static Map<Zenchantment, Integer> getEnchants(ItemStack itemStack, World world) {
        return Zenchantment.getEnchants(itemStack, false, world, null);
    }

    public static Map<Zenchantment, Integer> getEnchants(
        ItemStack itemStack,
        boolean acceptBooks,
        World world,
        List<String> outExtraLore
    ) {
        Map<Zenchantment, Integer> map = new LinkedHashMap<>();
        if (itemStack != null
            && (acceptBooks || itemStack.getType() != Material.ENCHANTED_BOOK)
            && itemStack.hasItemMeta()
            && itemStack.getItemMeta().hasLore()
        ) {
            for (String raw : itemStack.getItemMeta().getLore()) {
                Map.Entry<Zenchantment, Integer> ench = getEnchant(raw, world);
                if (ench != null) {
                    map.put(ench.getKey(), ench.getValue());
                } else {
                    if (outExtraLore != null) {
                        outExtraLore.add(raw);
                    }
                }
            }
        }

        Map<Zenchantment, Integer> finalMap = new LinkedHashMap<>();

        for (int id : new int[] {Lumber.ID, Shred.ID, Mow.ID, Pierce.ID, Extraction.ID, Plough.ID}) {
            Zenchantment zenchantment = null;

            for (Zenchantment ench : Config.allEnchants) {
                if (ench.id == id) {
                    zenchantment = ench;
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

    // Returns the custom enchantment from the lore name
    private static Map.Entry<Zenchantment, Integer> getEnchant(String raw, World world) {
        Matcher m = ENCH_LORE_PATTERN.matcher(raw);

        if (!m.find()) {
            return null;
        }

        String enchantmentName = ChatColor.stripColor(m.group(1));
        int level = m.group(2) == null || m.group(2).equals("") ? 1 : Utilities.getNumber(m.group(2));

        Zenchantment zenchantment = Config.get(world).enchantFromString(enchantmentName);
        if (zenchantment == null) {
            return null;
        }

        return new AbstractMap.SimpleEntry<>(zenchantment, level);
    }

    /**
     * Determines if the material provided is enchantable with this enchantment.
     *
     * @param material The material to test.
     * @return true iff the material can be enchanted with this enchantment.
     */
    // Returns true if the given material (tool) is compatible with the enchantment, otherwise false
    public boolean validMaterial(Material material) {
        for (Tool tool : this.enchantable) {
            if (tool.contains(material)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if the stack of material provided is enchantable with this
     * enchantment.
     *
     * @param itemStack The stack of material to test.
     * @return true iff the stack of material can be enchanted with this
     * enchantment.
     */
    public boolean validMaterial(ItemStack itemStack) {
        return this.validMaterial(itemStack.getType());
    }

    public String getShown(int level, World world) {
        Config config = Config.get(world);
        String levelString = Utilities.getRomanString(level);
        return (this.isCursed ? config.getCurseColor() : config.getEnchantmentColor())
            + this.loreName
            + (this.maxLevel == 1 ? " " : " " + levelString);
    }

    public List<String> getDescription(World world) {
        Config config = Config.get(world);
        List<String> desc = new LinkedList<>();

        if (config.descriptionLore()) {
            String start = Utilities.toInvisibleString("ze.desc." + this.id)
                + config.getDescriptionColor()
                + ChatColor.ITALIC
                + " ";
            StringBuilder builder = new StringBuilder();
            int i = 0;

            for (char c : description.toCharArray()) {
                if (i < 30) {
                    i++;
                    builder.append(c);
                } else {
                    if (c == ' ') {
                        desc.add(start + builder.toString());
                        builder = new StringBuilder(" ");
                        i = 1;
                    } else {
                        builder.append(c);
                    }
                }
            }

            if (i != 0) {
                desc.add(start + builder.toString());
            }
        }

        return desc;
    }

    public static boolean isDescription(String string) {
        for (Map.Entry<String, Boolean> entry : Utilities.fromInvisibleString(string).entrySet()) {
            if (entry.getValue()) {
                continue;
            }

            String[] values = entry.getKey().split("\\.");

            if (values.length == 3 && values[0].equals("ze") && values[1].equals("desc")) {
                return true;
            }
        }
        return false;
    }

    public void setEnchantment(ItemStack stack, int level, World world) {
        Zenchantment.setEnchantment(stack, this, level, world);
    }

    public static void setEnchantment(ItemStack stack, Zenchantment ench, int level, World world) {
        if (stack == null) {
            return;
        }
        ItemMeta meta = stack.getItemMeta();
        List<String> lore = new LinkedList<>();
        List<String> normalLore = new LinkedList<>();
        boolean isCustomEnchantment = false;
        if (meta.hasLore()) {
            for (String loreStr : meta.getLore()) {
                Map.Entry<Zenchantment, Integer> enchEntry = Zenchantment.getEnchant(loreStr, world);
                if (enchEntry == null && !Zenchantment.isDescription(loreStr)) {
                    normalLore.add(loreStr);
                } else if (enchEntry != null && enchEntry.getKey() != ench) {
                    isCustomEnchantment = true;
                    lore.add(enchEntry.getKey().getShown(enchEntry.getValue(), world));
                    lore.addAll(enchEntry.getKey().getDescription(world));
                }
            }
        }

        if (ench != null && level > 0 && level <= ench.maxLevel) {
            lore.add(ench.getShown(level, world));
            lore.addAll(ench.getDescription(world));
            isCustomEnchantment = true;
        }

        lore.addAll(normalLore);
        meta.setLore(lore);
        stack.setItemMeta(meta);

        if (isCustomEnchantment && stack.getType() == BOOK) {
            stack.setType(ENCHANTED_BOOK);
        }

        Zenchantment.setGlow(stack, isCustomEnchantment, world);
    }

    public static void setGlow(ItemStack stack, boolean isCustomEnchantment, World world) {
        if (Config.get(world) == null || !Config.get(world).enchantGlow()) {
            return;
        }

        ItemMeta itemMeta = stack.getItemMeta();
        EnchantmentStorageMeta bookMeta = null;

        boolean isBook = stack.getType() == BOOK || stack.getType() == ENCHANTED_BOOK;

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
        if (containsNormal || (!isCustomEnchantment && containsHidden)) {
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
        } else if (isCustomEnchantment) {
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

        stack.setItemMeta(isBook ? bookMeta : itemMeta);
    }

    protected static final class Builder<T extends Zenchantment> {
        private final T zenchantment;

        public Builder(Supplier<T> supplier, int id) {
            this.zenchantment = supplier.get();
            this.zenchantment.id = id;
        }

        public Builder<T> maxLevel(int maxLevel) {
            this.zenchantment.maxLevel = maxLevel;
            return this;
        }

        public int maxLevel() {
            return this.zenchantment.maxLevel;
        }

        public Builder<T> loreName(String loreName) {
            this.zenchantment.loreName = loreName;
            return this;
        }

        public String loreName() {
            return this.zenchantment.loreName;
        }

        public Builder<T> probability(float probability) {
            this.zenchantment.probability = probability;
            return this;
        }

        public float probability() {
            return this.zenchantment.probability;
        }

        public Builder<T> enchantable(Tool[] enchantable) {
            this.zenchantment.enchantable = enchantable;
            return this;
        }

        public Tool[] enchantable() {
            return this.zenchantment.enchantable;
        }

        public Builder<T> conflicting(Class<?>[] conflicting) {
            zenchantment.conflicting = conflicting;
            return this;
        }

        public Class<?>[] conflicting() {
            return this.zenchantment.conflicting;
        }

        public Builder<T> description(String description) {
            this.zenchantment.description = description;
            return this;
        }

        public String description() {
            return this.zenchantment.description;
        }

        public Builder<T> cooldown(int cooldown) {
            this.zenchantment.cooldown = cooldown;
            return this;
        }

        public int cooldown() {
            return this.zenchantment.cooldown;
        }

        public Builder<T> power(double power) {
            this.zenchantment.power = power;
            return this;
        }

        public double power() {
            return zenchantment.power;
        }

        public Builder<T> handUse(Hand handUse) {
            this.zenchantment.handUse = handUse;
            return this;
        }

        public Hand handUse() {
            return this.zenchantment.handUse;
        }

        public T build() {
            return this.zenchantment;
        }
    }
}