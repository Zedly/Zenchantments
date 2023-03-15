package zedly.zenchantments.arrows;

import org.bukkit.block.Block;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.CompatibilityAdapter;
import zedly.zenchantments.ZenchantmentPriority;
import zedly.zenchantments.ZenchantmentsPlugin;

import static java.util.Objects.requireNonNull;
import static zedly.zenchantments.enchantments.Vortex.VORTEX_LOCATIONS;

public final class VortexArrow extends ZenchantedArrow {
    public VortexArrow(final @NotNull AbstractArrow entity) {
        super(entity);
    }

    public ZenchantmentPriority getPriority() {
        return ZenchantmentPriority.LATE;
    }

    @Override
    public void onKill(final @NotNull EntityDeathEvent event) {
        if(event.getEntity().getKiller() == null) {
            die(true);
            return;
        }

        final Block deathBlock = event.getEntity().getLocation().getBlock();
        final Player killer = event.getEntity().getKiller();

        VORTEX_LOCATIONS.put(deathBlock, killer);

        final int experience = event.getDroppedExp();
        event.setDroppedExp(0);

        CompatibilityAdapter.instance().collectExp(requireNonNull(killer), experience);
        ZenchantmentsPlugin.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(
            ZenchantmentsPlugin.getInstance(),
            () -> VORTEX_LOCATIONS.remove(deathBlock),
            3
        );
        die(true);
    }
}
