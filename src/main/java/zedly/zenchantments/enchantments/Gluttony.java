package zedly.zenchantments.enchantments;

import com.google.common.collect.ImmutableSet;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;

import java.util.Set;

import static org.bukkit.Material.*;

public final class Gluttony extends Zenchantment {
    public static final String KEY = "gluttony";

    private static final String                             NAME        = "Gluttony";
    private static final String                             DESCRIPTION = "Automatically eats for the player";
    private static final Set<Class<? extends Zenchantment>> CONFLICTING = ImmutableSet.of();
    private static final Hand                               HAND_USE    = Hand.NONE;

    private static final int[] GLUTTONY_FOOD_LEVELS = { 4, 5, 1, 6, 5, 3, 1, 6, 5, 6, 8, 5, 6, 2, 1, 2, 6, 8, 10, 8 };

    private static final float[] GLUTTONY_SATURATIONS = {
        2.4f,
        6,
        1.2f,
        7.2f,
        6,
        3.6f,
        0.2f,
        7.2f,
        6,
        9.6f,
        12.8f,
        6,
        9.6f,
        0.4f,
        0.6f,
        1.2f,
        7.2f,
        4.8f,
        12,
        12.8f
    };

    private static final Material[] GLUTTONY_FOOD_ITEMS = {
        APPLE,
        BAKED_POTATO,
        BEETROOT,
        BEETROOT_SOUP,
        BREAD, CARROT,
        TROPICAL_FISH,
        COOKED_CHICKEN,
        COOKED_COD,
        COOKED_MUTTON,
        COOKED_PORKCHOP,
        COOKED_RABBIT,
        COOKED_SALMON,
        COOKIE,
        DRIED_KELP,
        MELON_SLICE,
        MUSHROOM_STEW,
        PUMPKIN_PIE,
        RABBIT_STEW,
        COOKED_BEEF
    };

    private final NamespacedKey key;

    public Gluttony(
        final @NotNull ZenchantmentsPlugin plugin,
        final @NotNull Set<Tool> enchantable,
        final int maxLevel,
        final int cooldown,
        final double power,
        final float probability
    ) {
        super(plugin, enchantable, maxLevel, cooldown, power, probability);
        this.key = new NamespacedKey(plugin, KEY);
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
    public boolean onScan(@NotNull Player player, int level, boolean usedHand) {
        for (int i = 0; i < GLUTTONY_FOOD_ITEMS.length; i++) {
            Material foodMaterial = GLUTTONY_FOOD_ITEMS[i];
            int foodLevel = GLUTTONY_FOOD_LEVELS[i];

            if (!player.getInventory().containsAtLeast(new ItemStack(foodMaterial), 1)
                || player.getFoodLevel() > 20 - foodLevel
            ) {
                continue;
            }

            Utilities.removeMaterialsFromPlayer(player, foodMaterial, 1);

            player.setFoodLevel(player.getFoodLevel() + foodLevel);
            player.setSaturation(player.getSaturation() + GLUTTONY_SATURATIONS[i]);

            if (foodMaterial == RABBIT_STEW
                || foodMaterial == MUSHROOM_STEW
                || foodMaterial == BEETROOT_SOUP
            ) {
                player.getInventory().addItem(new ItemStack(BOWL));
            }
        }

        return true;
    }
}
