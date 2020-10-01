package zedly.zenchantments.enchantments;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Guardian;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerFishEvent;
import zedly.zenchantments.CustomEnchantment;
import zedly.zenchantments.Storage;
import zedly.zenchantments.task.EffectTask;
import zedly.zenchantments.task.Frequency;
import zedly.zenchantments.enums.Hand;
import zedly.zenchantments.enums.Tool;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static org.bukkit.entity.EntityType.SQUID;
import static zedly.zenchantments.enums.Tool.ROD;

public class MysteryFish extends CustomEnchantment {

	// Guardians from the Mystery Fish enchantment and the player they should move towards
	public static final Map<Entity, Player> guardianMove = new HashMap<>();
	public static final int                 ID           = 38;

	@Override
	public Builder<MysteryFish> defaults() {
		return new Builder<>(MysteryFish::new, ID)
			.maxLevel(3)
			.loreName("Mystery Fish")
			.probability(0)
			.enchantable(new Tool[]{ROD})
			.conflicting(new Class[]{})
			.description("Catches water mobs like Squid and Guardians")
			.cooldown(0)
			.power(1.0)
			.handUse(Hand.RIGHT);
	}

	@Override
	public boolean onPlayerFish(final PlayerFishEvent evt, int level, boolean usedHand) {
		if (Storage.rnd.nextInt(10) < level * power) {
			if (evt.getCaught() != null) {
				Location location = evt.getCaught().getLocation();
				final Entity ent;
				if (Storage.rnd.nextBoolean()) {
					ent = evt.getPlayer().getWorld().spawnEntity(location, SQUID);
				} else {
					Entity g = Storage.COMPATIBILITY_ADAPTER.spawnGuardian(location, Storage.rnd.nextBoolean());
					guardianMove.put(g, evt.getPlayer());
					ent = g;
				}
			}
		}
		return true;
	}

	// Move Guardians from MysteryFish towards the player
	@EffectTask(Frequency.HIGH)
	public static void guardian() {
		Iterator it = guardianMove.keySet().iterator();
		while (it.hasNext()) {
			Guardian g = (Guardian) it.next();
			if (g.getLocation().distance(guardianMove.get(g).getLocation()) > 2 && g.getTicksLived() < 160) {
				g.setVelocity(
					guardianMove.get(g).getLocation().toVector().subtract(g.getLocation().toVector()));
			} else {
				it.remove();
			}
		}
	}
}
