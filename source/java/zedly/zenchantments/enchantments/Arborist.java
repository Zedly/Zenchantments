package zedly.zenchantments.enchantments;

import com.google.common.collect.ImmutableSet;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import static org.bukkit.Material.*;
import static zedly.zenchantments.MaterialList.*;

public final class Arborist extends Zenchantment {
    public static final String KEY = "arborist";

    private static final String                             NAME        = "Arborist";
    private static final String                             DESCRIPTION = "Drops more apples, sticks, and saplings when used on leaves";
    private static final Set<Class<? extends Zenchantment>> CONFLICTING = ImmutableSet.of();

    private final NamespacedKey key;

    public Arborist(
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
    public Collection<EquipmentSlot> getApplyToSlots() {
        return Slots.MAIN_HAND;
    }

    @Override
    public boolean onBlockBreak(final @NotNull BlockBreakEvent event, final int level, final EquipmentSlot slot) {
        final Block block = event.getBlock();
        final Material material = block.getType();

        if (!LEAVES.contains(material)) {
            return false;
        }

        // Crudely get the index in the array of materials.
        // TODO: Make this not awful.
        int index = LEAVES.indexOf(material);

        if (!(ThreadLocalRandom.current().nextInt(10) >= (9 - level) / (this.getPower() + 0.001))) {
            return false;
        }

        if (ThreadLocalRandom.current().nextInt(3) % 3 == 0) {
            event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(SAPLINGS.get(index), 1));
        }

        if (ThreadLocalRandom.current().nextInt(3) % 3 == 0) {
            event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(STICK, 1));
        }

        if (ThreadLocalRandom.current().nextInt(3) % 3 == 0) {
            event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(APPLE, 1));
        }

        if (ThreadLocalRandom.current().nextInt(65) == 25) {
            event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(GOLDEN_APPLE, 1));
        }

        return true;
    }
}
