package zedly.zenchantments.arrows.enchanted;

import org.bukkit.Bukkit;
import org.bukkit.entity.Arrow;
import org.bukkit.event.entity.EntityDeathEvent;
import zedly.zenchantments.Storage;
import zedly.zenchantments.arrows.EnchantedArrow;
import zedly.zenchantments.enchantments.Vortex;

public class VortexArrow extends EnchantedArrow {

    public VortexArrow(Arrow entity) {
        super(entity);
    }

    public void onKill(final EntityDeathEvent evt) {
        die();
    }
}
