package zedly.zenchantments.enchantments;

import org.bukkit.entity.Entity;
import org.bukkit.entity.MushroomCow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import zedly.zenchantments.Zenchantment;
import zedly.zenchantments.Hand;
import zedly.zenchantments.Tool;

import static org.bukkit.event.block.Action.RIGHT_CLICK_AIR;
import static org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK;
import static zedly.zenchantments.Tool.SHEAR;

public class Mow extends Zenchantment {

	public static final int ID = 37;

	@Override
	public Builder<Mow> defaults() {
		return new Builder<>(Mow::new, ID)
			.maxLevel(3)
			.name("Mow")
			.probability(0)
			.enchantable(new Tool[]{SHEAR})
			.conflicting(new Class[]{})
			.description("Shears all nearby sheep")
			.cooldown(0)
			.power(1.0)
			.handUse(Hand.RIGHT);
	}

	private boolean shear(PlayerEvent evt, int level, boolean usedHand) {
		boolean shearedEntity = false;
		int radius = (int) Math.round(level * power + 2);
		Player player = evt.getPlayer();
		for (Entity ent : evt.getPlayer().getNearbyEntities(radius, radius, radius)) {
			if (ent instanceof Sheep) {
				Sheep sheep = (Sheep) ent;
				if (sheep.isAdult()) {
					ADAPTER.shearEntityNMS(sheep, player, usedHand);
					shearedEntity = true;
				}
			} else if (ent instanceof MushroomCow) {
				MushroomCow mCow = (MushroomCow) ent;
				if (mCow.isAdult()) {
					ADAPTER.shearEntityNMS(mCow, player, usedHand);
					shearedEntity = true;
				}
			}
		}
		return shearedEntity;
	}

	@Override
	public boolean onBlockInteract(PlayerInteractEvent event, int level, boolean usedHand) {
		if (event.getAction() == RIGHT_CLICK_AIR || event.getAction() == RIGHT_CLICK_BLOCK) {
			return shear(event, level, usedHand);
		}
		return false;
	}

	@Override
	public boolean onShear(PlayerShearEntityEvent event, int level, boolean usedHand) {
		return shear(event, level, usedHand);
	}
}
