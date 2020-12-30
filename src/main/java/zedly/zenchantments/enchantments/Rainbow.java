package zedly.zenchantments.enchantments;

import com.google.common.collect.ImmutableSet;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Sheep;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;

import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import static org.bukkit.Material.AIR;
import static org.bukkit.block.BlockFace.DOWN;
import static zedly.zenchantments.MaterialList.LARGE_FLOWERS;
import static zedly.zenchantments.MaterialList.SMALL_FLOWERS;

public final class Rainbow extends Zenchantment {
    public static final String KEY = "rainbow";

    private static final String                             NAME        = "Rainbow";
    private static final String                             DESCRIPTION = "Drops random flowers and wool colors when used";
    private static final Set<Class<? extends Zenchantment>> CONFLICTING = ImmutableSet.of();
    private static final Hand                               HAND_USE    = Hand.BOTH;

    private final NamespacedKey key;

    public Rainbow(
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
    public boolean onBlockBreak(@NotNull BlockBreakEvent event, int level, boolean usedHand) {
        Block block = event.getBlock();
        Material blockType = block.getType();

        Material dropMaterial;

        if (SMALL_FLOWERS.contains(blockType)) {
            dropMaterial = SMALL_FLOWERS.getRandom();
        } else if (LARGE_FLOWERS.contains(blockType)) {
            dropMaterial = LARGE_FLOWERS.getRandom();
        } else {
            return false;
        }

        event.setCancelled(true);

        Block relative = block.getRelative(DOWN);
        if (LARGE_FLOWERS.contains(relative.getType())) {
            relative.setType(AIR);
        }

        block.setType(AIR);

        event.getPlayer().getWorld().dropItem(Utilities.getCenter(block), new ItemStack(dropMaterial, 1));

        Utilities.damageItemStack(event.getPlayer(), 1, usedHand);

        return true;
    }

    @Override
    public boolean onShear(@NotNull PlayerShearEntityEvent event, int level, boolean usedHand) {
        Sheep sheep = (Sheep) event.getEntity();
        if (sheep.isSheared()) {
            return true;
        }

        int count = ThreadLocalRandom.current().nextInt(3) + 1;

        Utilities.damageItemStack(event.getPlayer(), 1, usedHand);

        sheep.setSheared(true);

        event.setCancelled(true);

        event.getEntity().getWorld().dropItemNaturally(
            event.getEntity().getLocation(),
            new ItemStack(MaterialList.WOOL.getRandom(), count)
        );

        return true;
    }
}
