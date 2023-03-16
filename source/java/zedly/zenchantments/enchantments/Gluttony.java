package zedly.zenchantments.enchantments;

import com.google.common.collect.ImmutableSet;
import net.minecraft.util.Tuple;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;

import java.util.*;

import static org.bukkit.Material.*;

public final class Gluttony extends Zenchantment {
    public static final String KEY = "gluttony";

    private static final Set<Class<? extends Zenchantment>> CONFLICTING = ImmutableSet.of();

    private static final HashMap<Material, Tuple<Integer, Double>> GLUTTONY_FOODS = new HashMap<>();

    public Gluttony(
        final @NotNull Set<Tool> enchantable,
        final int maxLevel,
        final int cooldown,
        final double probability,
        final float power
    ) {
        super(enchantable, maxLevel, cooldown, probability, power, CONFLICTING, KEY);
    }

    @Override
    public Collection<EquipmentSlot> getApplyToSlots() {
        return Slots.ARMOR;
    }

    @Override
    public boolean onScan(final @NotNull Player player, final int level, final EquipmentSlot slot) {
        final int needFoodLevel = 20 - player.getFoodLevel();
        if (needFoodLevel <= 0) {
            return false;
        }
        final double genericMaxHealth = Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue();
        boolean needToHeal = player.getHealth() < genericMaxHealth;

        Material maxHungerMaterial = AIR;
        int maxFoodLevel = 0;
        Material maxNonExcessMaterial = AIR;
        int maxNonExcessFoodLevel = 0;
        for (ItemStack item : player.getInventory()) {
            if (item == null) {
                continue;
            }
            Material mat = item.getType();
            if (!GLUTTONY_FOODS.containsKey(mat)) {
                continue;
            }

            int foodLevelForMat = GLUTTONY_FOODS.get(mat).a();
            if (foodLevelForMat > maxFoodLevel) {
                maxHungerMaterial = mat;
                maxFoodLevel = foodLevelForMat;
                if (!needToHeal && needFoodLevel < maxFoodLevel) {
                    return false;
                }
            }
            if (foodLevelForMat <= needFoodLevel && foodLevelForMat > maxNonExcessFoodLevel) {
                maxNonExcessMaterial = mat;
                maxNonExcessFoodLevel = foodLevelForMat;
            }
        }
        Material matToEat = needToHeal ? maxNonExcessMaterial : maxHungerMaterial;
        if(matToEat == AIR) {
            return false;
        }

        final int foodLevel = GLUTTONY_FOODS.get(matToEat).a();
        final float saturationlevel = GLUTTONY_FOODS.get(matToEat).b().floatValue();

        Utilities.removeMaterialsFromPlayer(player, matToEat, 1);

        player.setFoodLevel(player.getFoodLevel() + foodLevel);
        player.setSaturation(player.getSaturation() + saturationlevel);

        if (matToEat == RABBIT_STEW
            || matToEat == MUSHROOM_STEW
            || matToEat == BEETROOT_SOUP
        ) {
            player.getInventory().addItem(new ItemStack(BOWL));
        }
        if (matToEat == HONEY_BOTTLE
        ) {
            HashMap<Integer, ItemStack> overflow = player.getInventory().addItem(new ItemStack(GLASS_BOTTLE));
            if (!overflow.isEmpty()) {
                player.getWorld().dropItem(player.getLocation(), overflow.get(0));
            }
        }
        return true;
    }

    static {
        GLUTTONY_FOODS.put(APPLE, new Tuple<>(4, 2.4));
        GLUTTONY_FOODS.put(BAKED_POTATO, new Tuple<>(5, 6.0));
        GLUTTONY_FOODS.put(BEETROOT, new Tuple<>(1, 1.2));
        GLUTTONY_FOODS.put(BEETROOT_SOUP, new Tuple<>(6, 7.2));
        GLUTTONY_FOODS.put(BREAD, new Tuple<>(5, 6.0));
        GLUTTONY_FOODS.put(CARROT, new Tuple<>(3, 3.6));
        GLUTTONY_FOODS.put(COOKED_CHICKEN, new Tuple<>(6, 7.2));
        GLUTTONY_FOODS.put(COOKED_COD, new Tuple<>(5, 6.0));
        GLUTTONY_FOODS.put(COOKED_MUTTON, new Tuple<>(6, 9.6));
        GLUTTONY_FOODS.put(COOKED_PORKCHOP, new Tuple<>(8, 12.8));
        GLUTTONY_FOODS.put(COOKED_RABBIT, new Tuple<>(5, 6.0));
        GLUTTONY_FOODS.put(COOKED_SALMON, new Tuple<>(6, 9.6));
        GLUTTONY_FOODS.put(COOKIE, new Tuple<>(2, 0.4));
        GLUTTONY_FOODS.put(DRIED_KELP, new Tuple<>(1, 0.6));
        GLUTTONY_FOODS.put(GLOW_BERRIES, new Tuple<>(2, 0.4));
        GLUTTONY_FOODS.put(GOLDEN_CARROT, new Tuple<>(6, 14.4));
        GLUTTONY_FOODS.put(HONEY_BOTTLE, new Tuple<>(6, 1.2));
        GLUTTONY_FOODS.put(MELON_SLICE, new Tuple<>(2, 1.2));
        GLUTTONY_FOODS.put(MUSHROOM_STEW, new Tuple<>(6, 7.2));
        GLUTTONY_FOODS.put(POTATO, new Tuple<>(1, 0.6));
        GLUTTONY_FOODS.put(PUMPKIN_PIE, new Tuple<>(8, 4.8));
        GLUTTONY_FOODS.put(RABBIT_STEW, new Tuple<>(10, 12.0));
        GLUTTONY_FOODS.put(COOKED_BEEF, new Tuple<>(8, 12.8));
        GLUTTONY_FOODS.put(SWEET_BERRIES, new Tuple<>(2, 0.4));
        GLUTTONY_FOODS.put(TROPICAL_FISH, new Tuple<>(1, 0.2));
    }
}
