package zedly.zenchantments.arrows.enchanted;

import org.bukkit.entity.Arrow;
import org.bukkit.event.entity.EntityDeathEvent;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.arrows.EnchantedArrow;

public class LevelArrow extends EnchantedArrow {

	public LevelArrow(Arrow entity, int level, double power) {
		super(entity, level, power);
	}

	public void onKill(@NotNull EntityDeathEvent event) {
		die(true);
	}
        
        public void onImpact() {
            die(false);
        }
}
