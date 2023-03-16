package zedly.zenchantments.enchantments;

import com.google.common.collect.ImmutableSet;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;
import zedly.zenchantments.arrows.LevelArrow;
import zedly.zenchantments.arrows.ZenchantedArrow;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public final class Level extends Zenchantment {
    public static final String KEY = "level";

    private static final Set<Class<? extends Zenchantment>> CONFLICTING = ImmutableSet.of();

    public Level(
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
    public boolean onEntityKill(final @NotNull EntityDeathEvent event, final int level, final EquipmentSlot slot) {
        if (ThreadLocalRandom.current().nextBoolean()) {
            event.setDroppedExp((int) (event.getDroppedExp() * (1.3 + (level * this.getPower() * .5))));
            return true;
        }

        return false;
    }

    @Override
    public boolean onBlockBreak(final @NotNull BlockBreakEvent event, final int level, final EquipmentSlot slot) {
        if (ThreadLocalRandom.current().nextBoolean()) {
            event.setExpToDrop((int) (event.getExpToDrop() * (1.3 + (level * this.getPower() * .5))));
            return true;
        }

        return false;
    }

    @Override
    public boolean onEntityShootBow(final @NotNull EntityShootBowEvent event, final int level, final EquipmentSlot slot) {
        if (ThreadLocalRandom.current().nextBoolean()) {
            final LevelArrow arrow = new LevelArrow((AbstractArrow) event.getProjectile(), level, this.getPower());
            ZenchantedArrow.addZenchantedArrowToArrowEntity((AbstractArrow) event.getProjectile(), arrow, (Player) event.getEntity());
            return true;
        }

        return false;
    }
}
