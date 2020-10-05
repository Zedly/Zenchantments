package zedly.zenchantments.enchantments;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;

import java.util.concurrent.ThreadLocalRandom;

import static org.bukkit.Material.*;

public class Arborist extends Zenchantment {
    private static final String     KEY         = "arborist";
    private static final String     NAME        = "Arborist";
    private static final String     DESCRIPTION = "Drops more apples, sticks, and saplings when used on leaves";
    private static final Class<?>[] CONFLICTING = new Class<?>[0];
    private static final Hand       HAND_USE    = Hand.LEFT;

    private final NamespacedKey key;

    public Arborist(
        @NotNull ZenchantmentsPlugin plugin,
        @NotNull Tool[] enchantable,
        int maxLevel,
        int cooldown,
        double probability,
        float power
    ) {
        super(plugin, enchantable, maxLevel, cooldown, probability, power);
        this.key = new NamespacedKey(plugin, Arborist.KEY);
    }

    @Override
    @NotNull
    public NamespacedKey getKey() {
        return this.key;
    }

    @Override
    @NotNull
    public String getName() {
        return Arborist.NAME;
    }

    @Override
    @NotNull
    public String getDescription() {
        return Arborist.DESCRIPTION;
    }

    @Override
    @NotNull
    public Class<?>[] getConflicting() {
        return Arborist.CONFLICTING;
    }

    @Override
    @NotNull
    public Hand getHandUse() {
        return Arborist.HAND_USE;
    }

    @Override
    public boolean onBlockBreak(@NotNull BlockBreakEvent event, int level, boolean usedHand) {
        Block block = event.getBlock();
        Material material = block.getType();

        if (!Storage.COMPATIBILITY_ADAPTER.Leaves().contains(material)) {
            return false;
        }

        // Crudely get the index in the array of materials.
        // TODO: Make this not awful.
        int index = Math.max(
            Storage.COMPATIBILITY_ADAPTER.Leaves().indexOf(material),
            Storage.COMPATIBILITY_ADAPTER.Leaves().indexOf(material)
        );

        ItemStack stack = new ItemStack(Storage.COMPATIBILITY_ADAPTER.Saplings().get(index), 1);

        if (!(ThreadLocalRandom.current().nextInt(10) >= (9 - level) / (this.getPower() + 0.001))) {
            return false;
        }

        if (ThreadLocalRandom.current().nextInt(3) % 3 == 0) {
            event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), stack);
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