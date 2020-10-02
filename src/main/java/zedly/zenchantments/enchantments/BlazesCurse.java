package zedly.zenchantments.enchantments;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import zedly.zenchantments.Zenchantment;
import zedly.zenchantments.Storage;
import zedly.zenchantments.Hand;
import zedly.zenchantments.Tool;

import static org.bukkit.Material.*;
import static org.bukkit.block.BlockFace.DOWN;
import static zedly.zenchantments.Tool.CHESTPLATE;

public class BlazesCurse extends Zenchantment {

	private static final float   submergeDamage = 1.5f;
	private static final float   rainDamage     = .5f;
	public static final  int     ID             = 5;

	@Override
	public Builder<BlazesCurse> defaults() {
		return new Builder<>(BlazesCurse::new, ID)
			.maxLevel(1)
			.loreName("Blaze's Curse")
			.probability(0)
			.enchantable(new Tool[]{CHESTPLATE})
			.conflicting(new Class[]{})
			.description("Causes the player to be unharmed in lava and fire, but damages them in water and rain")
			.cooldown(0)
			.power(-1.0)
			.handUse(Hand.NONE);
	}

	@Override
	public boolean onEntityDamage(EntityDamageEvent event, int level, boolean usedHand) {
		if (event.getCause() == EntityDamageEvent.DamageCause.HOT_FLOOR ||
			event.getCause() == EntityDamageEvent.DamageCause.LAVA ||
			event.getCause() == EntityDamageEvent.DamageCause.FIRE ||
			event.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK) {
			event.setCancelled(true);
			return true;
		}
		return false;
	}

	@Override
	public boolean onBeingHit(EntityDamageByEntityEvent event, int level, boolean usedHand) {
		if (event.getDamager().getType() == EntityType.FIREBALL
			|| event.getDamager().getType() == EntityType.SMALL_FIREBALL) {
			event.setDamage(0);
			return true;
		}
		return false;
	}

	@Override
	public boolean onScan(Player player, int level, boolean usedHand) {
		Material mat = player.getLocation().getBlock().getType();
		if (mat == WATER) {
			ADAPTER.damagePlayer(player, submergeDamage, EntityDamageEvent.DamageCause.DROWNING);
			return true;
		}
		mat = player.getLocation().getBlock().getRelative(DOWN).getType();
		if (mat == ICE || mat == FROSTED_ICE) {
			ADAPTER.damagePlayer(player, rainDamage, EntityDamageEvent.DamageCause.MELTING);
			return true;
		}
		if (player.getWorld().hasStorm()
			&& !Storage.COMPATIBILITY_ADAPTER.DryBiomes().contains(player.getLocation().getBlock().getBiome())) {
			Location check_loc = player.getLocation();
			while (check_loc.getBlockY() < 256) {
				if (!Storage.COMPATIBILITY_ADAPTER.Airs().contains(check_loc.getBlock().getType())) {
					break;
				}
				check_loc.setY(check_loc.getBlockY() + 1);
			}
			if (check_loc.getBlockY() == 256) {
				ADAPTER.damagePlayer(player, rainDamage, EntityDamageEvent.DamageCause.CUSTOM);
			}
		}
		player.setFireTicks(0);
		return true;
	}
}
