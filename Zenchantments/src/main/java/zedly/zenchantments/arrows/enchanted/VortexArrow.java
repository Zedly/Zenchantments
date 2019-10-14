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
		Vortex.vortexLocs.put(evt.getEntity().getLocation().getBlock(), evt.getEntity().getKiller().getLocation());
		int i = evt.getDroppedExp();
		evt.setDroppedExp(0);
		evt.getEntity().getKiller().giveExp(i);
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Storage.zenchantments, () -> {
			Vortex.vortexLocs.remove(evt.getEntity().getLocation().getBlock());
		}, 3);
		die();
	}
}
