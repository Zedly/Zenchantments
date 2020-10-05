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

public class Gluttony extends Zenchantment {
    public static final String KEY = "gluttony";

    private static final String                             NAME        = "Gluttony";
    private static final String                             DESCRIPTION = "Automatically eats for the player";
    private static final Set<Class<? extends Zenchantment>> CONFLICTING = ImmutableSet.of();
    private static final Hand                               HAND_USE    = Hand.NONE;

    private final NamespacedKey key;

    public Gluttony(
        @NotNull ZenchantmentsPlugin plugin,
        @NotNull Set<Tool> enchantable,
        int maxLevel,
        int cooldown,
        double power,
        float probability
    ) {
        super(plugin, enchantable, maxLevel, cooldown, power, probability);
        this.key = new NamespacedKey(plugin, Gluttony.KEY);
    }

    @Override
    @NotNull
    public NamespacedKey getKey() {
        return this.key;
    }

    @Override
    @NotNull
    public String getName() {
        return Gluttony.NAME;
    }

    @Override
    @NotNull
    public String getDescription() {
        return Gluttony.DESCRIPTION;
    }

    @Override
    @NotNull
    public Set<Class<? extends Zenchantment>> getConflicting() {
        return Gluttony.CONFLICTING;
    }

    @Override
    @NotNull
    public Hand getHandUse() {
        return Gluttony.HAND_USE;
    }

    @Override
    public boolean onScan(@NotNull Player player, int level, boolean usedHand) {
        for (int i = 0; i < Storage.COMPATIBILITY_ADAPTER.GluttonyFoodItems().length; i++) {
            Material foodMaterial = Storage.COMPATIBILITY_ADAPTER.GluttonyFoodItems()[i];
            int foodLevel = Storage.COMPATIBILITY_ADAPTER.GluttonyFoodLevels()[i];

            if (!player.getInventory().containsAtLeast(new ItemStack(foodMaterial), 1)
                || player.getFoodLevel() > 20 - foodLevel
            ) {
                continue;
            }

            Utilities.removeItem(player, foodMaterial, 1);

            player.setFoodLevel(player.getFoodLevel() + foodLevel);
            player.setSaturation((float) (player.getSaturation() + Storage.COMPATIBILITY_ADAPTER.GluttonySaturations()[i]));

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