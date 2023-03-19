package zedly.zenchantments.enchantments;

import org.bukkit.block.Block;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;
import zedly.zenchantments.arrows.VortexArrow;
import zedly.zenchantments.arrows.ZenchantedArrow;

import java.util.*;

import static java.util.Objects.requireNonNull;

@AZenchantment(runInSlots = Slots.HANDS, conflicting = {})
public final class Vortex extends Zenchantment {
    public static final Map<Block, Player> VORTEX_LOCATIONS = new HashMap<>();

    @Override
    public ZenchantmentPriority getPriority() {
        return ZenchantmentPriority.LATE;
    }

    @Override
    public boolean onEntityKill(final @NotNull EntityDeathEvent event, final int level, final EquipmentSlot slot) {
        final Block deathBlock = event.getEntity().getLocation().getBlock();
        final Player killer = event.getEntity().getKiller();

        VORTEX_LOCATIONS.put(deathBlock, killer);

        final int experience = event.getDroppedExp();
        event.setDroppedExp(0);

        WorldInteractionUtil.collectExp(requireNonNull(killer), experience);
        ZenchantmentsPlugin.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(
            ZenchantmentsPlugin.getInstance(),
            () -> VORTEX_LOCATIONS.remove(deathBlock),
            3
        );

        return true;
    }

    @Override
    public boolean onEntityShootBow(final @NotNull EntityShootBowEvent event, final int level, final EquipmentSlot slot) {
        final VortexArrow arrow = new VortexArrow((AbstractArrow) event.getProjectile());
        ZenchantedArrow.addZenchantedArrowToArrowEntity((AbstractArrow) event.getProjectile(), arrow, (Player) event.getEntity());
        return true;
    }
}
