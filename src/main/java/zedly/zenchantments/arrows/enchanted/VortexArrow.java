package zedly.zenchantments.arrows.enchanted;

import org.bukkit.entity.Arrow;
import org.bukkit.event.entity.EntityDeathEvent;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.ZenchantmentsPlugin;
import zedly.zenchantments.arrows.EnchantedArrow;

public class VortexArrow extends EnchantedArrow {
    public VortexArrow(@NotNull ZenchantmentsPlugin plugin, @NotNull Arrow entity) {
        super(plugin, entity);
    }

    @Override
    public void onKill(final @NotNull EntityDeathEvent event) {
        this.die();
    }
}