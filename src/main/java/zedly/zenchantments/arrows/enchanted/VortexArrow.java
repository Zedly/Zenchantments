package zedly.zenchantments.arrows.enchanted;

import org.bukkit.entity.Arrow;
import org.bukkit.event.entity.EntityDeathEvent;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.arrows.EnchantedArrow;

public class VortexArrow extends EnchantedArrow {

    public VortexArrow(Arrow entity) {
        super(entity);
    }

    public void onKill(final @NotNull EntityDeathEvent event) {
        this.die();
    }
}