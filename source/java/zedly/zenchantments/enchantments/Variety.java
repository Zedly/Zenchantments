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

import static org.bukkit.Material.AIR;
import static zedly.zenchantments.MaterialList.*;

public final class Variety extends Zenchantment {
    public static final String KEY = "variety";

    private static final Set<Class<? extends Zenchantment>> CONFLICTING = ImmutableSet.of(Fire.class);

    public Variety(
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
        return Slots.MAIN_HAND;
    }

    @Override
    public boolean onBlockBreak(final @NotNull BlockBreakEvent event, final int level, final EquipmentSlot slot) {
        final Block block = event.getBlock();
        final Material material = block.getType();

        if (LOGS.contains(material)) {
            block.setType(AIR);
            block.getWorld().dropItemNaturally(
                block.getLocation(),
                new ItemStack(LOGS.getRandom())
            );

            Utilities.damageItemStackRespectUnbreaking(event.getPlayer(), 1, slot);
        } else if (LEAVES.contains(material)) {
            block.setType(AIR);
            block.getWorld().dropItemNaturally(
                block.getLocation(),
                new ItemStack(LEAVES.getRandom())
            );
            Utilities.damageItemStackRespectUnbreaking(event.getPlayer(), 1, slot);
        }

        return true;
    }
}
