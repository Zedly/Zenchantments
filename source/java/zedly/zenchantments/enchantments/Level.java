package zedly.zenchantments.enchantments;

import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;
import zedly.zenchantments.arrows.BlizzardArrow;
import zedly.zenchantments.arrows.LevelArrow;
import zedly.zenchantments.arrows.ZenchantedArrow;

import java.util.concurrent.ThreadLocalRandom;

@AZenchantment(runInSlots = Slots.HANDS, conflicting = {})
public final class Level extends Zenchantment {
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

    @Override
    public boolean onProjectileLaunch(final @NotNull ProjectileLaunchEvent event, final int level, final EquipmentSlot slot) {
        if(event.getEntity().getType() != EntityType.TRIDENT) {
            return false;
        }
        final LevelArrow arrow = new LevelArrow(event.getEntity(), level, getPower());
        ZenchantedArrow.addZenchantedArrowToArrowEntity(event.getEntity(), arrow, (Player) event.getEntity().getShooter());
        return true;
    }
}
