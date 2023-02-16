package zedly.zenchantments.enchantments;

import com.google.common.collect.ImmutableSet;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
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
    public boolean onScanHands(final @NotNull Player player, final int level, final EquipmentSlot slot) {
        final ItemStack stack = player.getInventory().getItem(slot);
        final int durability = Utilities.getItemStackDamage(stack);

        Utilities.setItemStackDamage(stack, 0);

        if (durability != 0) {
            player.getInventory().setItem(slot, stack);
        }

        return durability != 0;
    }

    @Override
    public boolean onScan(final @NotNull Player player, final int level, final EquipmentSlot slot) {
        for (final ItemStack stack : player.getInventory().getArmorContents()) {
            if (stack == null) {
                continue;
            }

            if (
                Zenchantment.getZenchantmentsOnItemStack(
                    stack,
                    ZenchantmentsPlugin.getInstance().getWorldConfigurationProvider().getConfigurationForWorld(player.getWorld())
                ).containsKey(this)
            ) {
                Utilities.setItemStackDamage(stack, 0);
            }
        }

        return true;
    }
}
