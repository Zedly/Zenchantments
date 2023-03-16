package zedly.zenchantments.enchantments;

import com.google.common.collect.ImmutableSet;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Sheep;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import static org.bukkit.Material.AIR;
import static org.bukkit.block.BlockFace.DOWN;
import static zedly.zenchantments.MaterialList.LARGE_FLOWERS;
import static zedly.zenchantments.MaterialList.SMALL_FLOWERS;

public final class Rainbow extends Zenchantment {
    public static final String KEY = "rainbow";

    private static final Set<Class<? extends Zenchantment>> CONFLICTING = ImmutableSet.of();

    public Rainbow(
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
        return Slots.HANDS;
    }

    @Override
    public boolean onBlockBreak(final @NotNull BlockBreakEvent event, final int level, final EquipmentSlot slot) {
        final Block block = event.getBlock();
        final Material blockType = block.getType();

        final Material dropMaterial;

        if (SMALL_FLOWERS.contains(blockType)) {
            dropMaterial = SMALL_FLOWERS.getRandom();
        } else if (LARGE_FLOWERS.contains(blockType)) {
            dropMaterial = LARGE_FLOWERS.getRandom();
        } else {
            return false;
        }

        event.setCancelled(true);

        final Block relative = block.getRelative(DOWN);
        if (LARGE_FLOWERS.contains(relative.getType())) {
            relative.setType(AIR);
        }

        block.setType(AIR);

        event.getPlayer().getWorld().dropItem(Utilities.getCenter(block), new ItemStack(dropMaterial, 1));
        Utilities.damageItemStackRespectUnbreaking(event.getPlayer(), 1, slot);
        return true;
    }

    @Override
    public boolean onShear(final @NotNull PlayerShearEntityEvent event, final int level, final EquipmentSlot slot) {
        final Sheep sheep = (Sheep) event.getEntity();
        if (sheep.isSheared()) {
            return true;
        }

        final int count = ThreadLocalRandom.current().nextInt(3) + 1;

        Utilities.damageItemStackRespectUnbreaking(event.getPlayer(), 1, slot);

        sheep.setSheared(true);
        event.setCancelled(true);

        event.getEntity().getWorld().dropItemNaturally(
            event.getEntity().getLocation(),
            new ItemStack(MaterialList.WOOL.getRandom(), count)
        );

        return true;
    }
}
