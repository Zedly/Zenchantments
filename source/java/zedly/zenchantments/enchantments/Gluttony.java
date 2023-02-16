package zedly.zenchantments.enchantments;

import com.google.common.collect.ImmutableSet;
import org.apache.commons.lang3.tuple.Triple;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

import static org.bukkit.Material.*;

public final class Gluttony extends Zenchantment {
    public static final String KEY = "gluttony";

    private static final String                             NAME        = "Gluttony";
    private static final String                             DESCRIPTION = "Automatically eats for the player";
    private static final Set<Class<? extends Zenchantment>> CONFLICTING = ImmutableSet.of();
    private static final Hand                               HAND_USE    = Hand.NONE;

    private static final LinkedList<Triple<Material, Integer, Double>> GLUTTONY_FOODS = new LinkedList<>();

    private final NamespacedKey key;

    public Gluttony(
        final @NotNull Set<Tool> enchantable,
        final int maxLevel,
        final int cooldown,
        final double probability,
        final float power
    ) {
        super(enchantable, maxLevel, cooldown, probability, power);
        this.key = new NamespacedKey(ZenchantmentsPlugin.getInstance(), KEY);
    }

    @Override
    @NotNull
    public NamespacedKey getKey() {
        return this.key;
    }

    @Override
    @NotNull
    public String getName() {
        return NAME;
    }

    @Override
    @NotNull
    public String getDescription() {
        return DESCRIPTION;
    }

    @Override
    @NotNull
    public Set<Class<? extends Zenchantment>> getConflicting() {
        return CONFLICTING;
    }

    @Override
    @NotNull
    public Hand getHandUse() {
        return HAND_USE;
    }

    @Override
    public boolean onScan(final @NotNull Player player, final int level, final EquipmentSlot slot) {
        for (int i = 0; i < GLUTTONY_FOODS.size(); i++) {
            final Material foodMaterial = GLUTTONY_FOODS.get(i).getLeft();
            final int foodLevel = GLUTTONY_FOODS.get(i).getMiddle();

            if (!player.getInventory().containsAtLeast(new ItemStack(foodMaterial), 1)
                || player.getFoodLevel() > 20 - foodLevel
            ) {
                continue;
            }

            Utilities.removeMaterialsFromPlayer(player, foodMaterial, 1);

            player.setFoodLevel(player.getFoodLevel() + foodLevel);
            player.setSaturation(player.getSaturation() + GLUTTONY_FOODS.get(i).getRight().floatValue());

            if (foodMaterial == RABBIT_STEW
                || foodMaterial == MUSHROOM_STEW
                || foodMaterial == BEETROOT_SOUP
            ) {
                player.getInventory().addItem(new ItemStack(BOWL));
            }
            if (foodMaterial == HONEY_BOTTLE
            ) {
                HashMap<Integer, ItemStack> overflow = player.getInventory().addItem(new ItemStack(GLASS_BOTTLE));
                if(!overflow.isEmpty()) {
                    player.getWorld().dropItem(player.getLocation(), overflow.get(0));
                }
            }
        }
        return true;
    }

    static {
        GLUTTONY_FOODS.add(Triple.of(APPLE, 4, 2.4));
        GLUTTONY_FOODS.add(Triple.of(BAKED_POTATO, 5, 6.0));
        GLUTTONY_FOODS.add(Triple.of(BEETROOT, 1, 1.2));
        GLUTTONY_FOODS.add(Triple.of(BEETROOT_SOUP, 6, 7.2));
        GLUTTONY_FOODS.add(Triple.of(BREAD, 5, 6.0));
        GLUTTONY_FOODS.add(Triple.of(CARROT, 3, 3.6));
        GLUTTONY_FOODS.add(Triple.of(COOKED_CHICKEN, 6, 7.2));
        GLUTTONY_FOODS.add(Triple.of(COOKED_COD, 5, 6.0));
        GLUTTONY_FOODS.add(Triple.of(COOKED_MUTTON, 6, 9.6));
        GLUTTONY_FOODS.add(Triple.of(COOKED_PORKCHOP, 8, 12.8));
        GLUTTONY_FOODS.add(Triple.of(COOKED_RABBIT, 5, 6.0));
        GLUTTONY_FOODS.add(Triple.of(COOKED_SALMON, 6, 9.6));
        GLUTTONY_FOODS.add(Triple.of(COOKIE, 2, 0.4));
        GLUTTONY_FOODS.add(Triple.of(DRIED_KELP, 1, 0.6));
        GLUTTONY_FOODS.add(Triple.of(GLOW_BERRIES, 2, 0.4));
        GLUTTONY_FOODS.add(Triple.of(GOLDEN_CARROT, 6, 14.4));
        GLUTTONY_FOODS.add(Triple.of(HONEY_BOTTLE, 6, 1.2));
        GLUTTONY_FOODS.add(Triple.of(MELON_SLICE, 2, 1.2));
        GLUTTONY_FOODS.add(Triple.of(MUSHROOM_STEW, 6, 7.2));
        GLUTTONY_FOODS.add(Triple.of(POTATO, 1, 0.6));
        GLUTTONY_FOODS.add(Triple.of(PUMPKIN_PIE, 8, 4.8));
        GLUTTONY_FOODS.add(Triple.of(RABBIT_STEW, 10, 12.0));
        GLUTTONY_FOODS.add(Triple.of(COOKED_BEEF, 8, 12.8));
        GLUTTONY_FOODS.add(Triple.of(SWEET_BERRIES, 2, 0.4));
        GLUTTONY_FOODS.add(Triple.of(TROPICAL_FISH, 1, 0.2));
    }
}
