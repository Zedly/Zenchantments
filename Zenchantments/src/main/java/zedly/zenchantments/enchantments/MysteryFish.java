package zedly.zenchantments.enchantments;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Guardian;
import org.bukkit.event.player.PlayerFishEvent;
import zedly.zenchantments.CustomEnchantment;
import zedly.zenchantments.Storage;
import zedly.zenchantments.annotations.EffectTask;
import zedly.zenchantments.enums.Frequency;
import zedly.zenchantments.enums.Hand;
import zedly.zenchantments.enums.Tool;

import java.util.Iterator;

import static org.bukkit.entity.EntityType.SQUID;
import static zedly.zenchantments.enums.Tool.ROD;

public class MysteryFish extends CustomEnchantment {

    public MysteryFish() {
	    super(38);
	    maxLevel = 3;
	    loreName = "Mystery Fish";
	    probability = 0;
	    enchantable = new Tool[]{ROD};
	    conflicting = new Class[]{};
	    description = "Catches water mobs like Squid and Guardians";
	    cooldown = 0;
	    power = 1.0;
	    handUse = Hand.RIGHT;
    }

    @Override
    public boolean onPlayerFish(final PlayerFishEvent evt, int level, boolean usedHand) {
        if(Storage.rnd.nextInt(10) < level * power) {
            if(evt.getCaught() != null) {
                Location location = evt.getCaught().getLocation();
                final Entity ent;
                if(Storage.rnd.nextBoolean()) {
                    ent = evt.getPlayer().getWorld().spawnEntity(location, SQUID);
                } else {
                    Entity g = Storage.COMPATIBILITY_ADAPTER.spawnGuardian(location, Storage.rnd.nextBoolean());
                    Storage.guardianMove.put(g, evt.getPlayer());
                    ent = g;
                }
                evt.getCaught().setPassenger(ent);
            }
        }
        return true;
    }

	// Move Guardians from MysteryFish towards the player
	@EffectTask(Frequency.HIGH)
	public static void guardian() {
		Iterator it = Storage.guardianMove.keySet().iterator();
		while (it.hasNext()) {
			Guardian g = (Guardian) it.next();
			if (g.getLocation().distance(Storage.guardianMove.get(g).getLocation()) > 2 && g.getTicksLived() < 160) {
				g.setVelocity(
						Storage.guardianMove.get(g).getLocation().toVector().subtract(g.getLocation().toVector()));
			} else {
				it.remove();
			}
		}
	}
}
