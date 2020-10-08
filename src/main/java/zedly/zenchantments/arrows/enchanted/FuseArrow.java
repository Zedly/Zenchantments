package zedly.zenchantments.arrows.enchanted;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.*;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.Config;
import zedly.zenchantments.Storage;
import zedly.zenchantments.Utilities;
import zedly.zenchantments.arrows.EnchantedArrow;

import static org.bukkit.Material.AIR;
import static org.bukkit.Material.TNT;

public class FuseArrow extends EnchantedArrow {

	public FuseArrow(Arrow entity) {
		super(entity);
	}

	public void onImpact() {
		Location loc = arrow.getLocation();
		for (int i = 1; i < 5; i++) {
			Vector vec = arrow.getVelocity().multiply(.25 * i);
			Location hitLoc = new Location(loc.getWorld(), loc.getX() + vec.getX(), loc.getY() + vec.getY(),
				loc.getZ() + vec.getZ());
			if (hitLoc.getBlock().getType().equals(TNT)) {
				BlockBreakEvent event = new BlockBreakEvent(hitLoc.getBlock(), (Player) arrow.getShooter());
				Bukkit.getServer().getPluginManager().callEvent(event);
				if (!event.isCancelled()) {
					hitLoc.getBlock().setType(AIR);
					hitLoc.getWorld().spawnEntity(hitLoc, EntityType.PRIMED_TNT);
					die();
				}
				return;
			}
		}
		die();
	}

	public boolean onImpact(@NotNull EntityDamageByEntityEvent event) {
		Location l = event.getEntity().getLocation();
		if (Storage.COMPATIBILITY_ADAPTER.attackEntity((LivingEntity) event.getEntity(), (Player) arrow.getShooter(),
			0)) {
			if (event.getEntity().getType().equals(EntityType.CREEPER)) {
				Creeper c = (Creeper) event.getEntity();
				Storage.COMPATIBILITY_ADAPTER.explodeCreeper(c, Config.get(event.getDamager().getWorld()).explosionBlockBreak());
			} else if (event.getEntity().getType().equals(EntityType.MUSHROOM_COW)) {
				MushroomCow c = (MushroomCow) event.getEntity();
				if (c.isAdult()) {
					Utilities.display(l, Particle.EXPLOSION_LARGE, 1, 1f, 0, 0, 0);
					event.getEntity().remove();
					l.getWorld().spawnEntity(l, EntityType.COW);
					l.getWorld().dropItemNaturally(l, new ItemStack(Material.RED_MUSHROOM, 5));
				}
			}
		}
		die();
		return true;
	}
}
