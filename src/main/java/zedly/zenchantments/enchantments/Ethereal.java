package zedly.zenchantments.enchantments;

import com.google.common.collect.ImmutableSet;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;

import java.util.Set;

public final class Ethereal extends Zenchantment {
    public static final String KEY = "ethereal";

    private static final String                             NAME        = "Ethereal";
    private static final String                             DESCRIPTION = "Prevents tools from breaking";
    private static final Set<Class<? extends Zenchantment>> CONFLICTING = ImmutableSet.of();
    private static final Hand                               HAND_USE    = Hand.NONE;

    private final NamespacedKey key;

    public Ethereal(
        @NotNull ZenchantmentsPlugin plugin,
        @NotNull Set<Tool> enchantable,
        int maxLevel,
        int cooldown,
        double power,
        float probability
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
    public boolean onScanHands(@NotNull Player player, int level, boolean usedHand) {
        ItemStack stack = Utilities.getUsedItemStack(player, usedHand);
        int durability = Utilities.getItemStackDamage(stack);

        Utilities.setItemStackDamage(stack, 0);

        if (durability != 0) {
            if (usedHand) {
                player.getInventory().setItemInMainHand(stack);
            } else {
                player.getInventory().setItemInOffHand(stack);
            }
        }

        return durability != 0;
    }

    @Override
    public boolean onScan(@NotNull Player player, int level, boolean usedHand) {
        for (ItemStack stack : player.getInventory().getArmorContents()) {
            // Thanks IntelliJ, but individual items *can* in fact be null.
            //noinspection ConstantConditions
            if (stack == null) {
                continue;
            }

            if (Zenchantment.getEnchants(stack, player.getWorld()).containsKey(this)) {
                Utilities.setItemStackDamage(stack, 0);
            }
        }

        return true;
    }
}