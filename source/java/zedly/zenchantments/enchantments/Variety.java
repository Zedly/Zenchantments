package zedly.zenchantments.enchantments;

import com.google.common.collect.ImmutableSet;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;

import java.util.Set;

import static org.bukkit.Material.AIR;
import static zedly.zenchantments.MaterialList.*;

public final class Variety extends Zenchantment {
    public static final String KEY = "variety";

    private static final String                             NAME        = "Variety";
    private static final String                             DESCRIPTION = "Drops random types of wood or leaves";
    private static final Set<Class<? extends Zenchantment>> CONFLICTING = ImmutableSet.of(Fire.class);
    private static final Hand                               HAND_USE    = Hand.LEFT;

    private final NamespacedKey key;

    public Variety(
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
    public boolean onBlockBreak(final @NotNull BlockBreakEvent event, final int level, final boolean usedHand) {
        final Block block = event.getBlock();
        final Material material = block.getType();

        if (LOGS.contains(material)) {
            block.setType(AIR);
            block.getWorld().dropItemNaturally(
                block.getLocation(),
                new ItemStack(LOGS.getRandom())
            );

            Utilities.damageItemStackRespectUnbreaking(event.getPlayer(), 1, usedHand);
        } else if (LEAVES.contains(material)) {
            block.setType(AIR);
            block.getWorld().dropItemNaturally(
                block.getLocation(),
                new ItemStack(LEAVES.getRandom())
            );
            Utilities.damageItemStackRespectUnbreaking(event.getPlayer(), 1, usedHand);
        }

        return true;
    }
}
