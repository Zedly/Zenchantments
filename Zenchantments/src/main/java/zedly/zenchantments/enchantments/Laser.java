package zedly.zenchantments.enchantments;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import zedly.zenchantments.CustomEnchantment;
import zedly.zenchantments.EnchantPlayer;
import zedly.zenchantments.Utilities;
import zedly.zenchantments.enums.Hand;
import zedly.zenchantments.enums.Tool;

import static org.bukkit.event.block.Action.RIGHT_CLICK_AIR;
import static org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK;
import static zedly.zenchantments.enums.Tool.AXE;
import static zedly.zenchantments.enums.Tool.PICKAXE;

public class Laser extends CustomEnchantment {

	// Time at which a later enchantment was fired; this is used to prevent double firing when clicking an entity
	// public static final Map<Player, Long> laserTimes = new HashMap<>();
	public static final int ID = 31;

	@Override
	public Builder<Laser> defaults() {
		return new Builder<>(Laser::new, ID)
			.maxLevel(3)
			.loreName("Laser")
			.probability(0)
			.enchantable(new Tool[]{PICKAXE, AXE})
			.conflicting(new Class[]{})
			.description("Breaks blocks and damages mobs using a powerful beam of light")
			.cooldown(0)
			.power(1.0)
			.handUse(Hand.RIGHT);
	}

	public void shoot(Player player, int level, boolean usedHand) {
		EnchantPlayer.matchPlayer(player).setCooldown(Lumber.ID, 5); // Avoid recursing into Lumber enchant
		Block blk = player.getTargetBlock(null, 6
			+ (int) Math.round(level * power * 3));
		Location playLoc = player.getLocation();
		Location target = Utilities.getCenter(blk.getLocation());
		target.setY(target.getY() + .5);
		playLoc.setY(playLoc.getY() + 1.1);
		double d = target.distance(playLoc);
		for (int i = 0; i < (int) d * 5; i++) {
			Location tempLoc = target.clone();
			tempLoc.setX(playLoc.getX() + (i * ((target.getX() - playLoc.getX()) / (d * 5))));
			tempLoc.setY(playLoc.getY() + (i * ((target.getY() - playLoc.getY()) / (d * 5))));
			tempLoc.setZ(playLoc.getZ() + (i * ((target.getZ() - playLoc.getZ()) / (d * 5))));

			player.getWorld().spawnParticle(Particle.REDSTONE, tempLoc, 1, new Particle.DustOptions(Color.RED, 0.5f));

			for (Entity ent : Bukkit.getWorld(playLoc.getWorld().getName()).getNearbyEntities(tempLoc, .3, .3, .3)) {
				if (ent instanceof LivingEntity && ent != player) {
					LivingEntity e = (LivingEntity) ent;
					ADAPTER.attackEntity(e, player, 1 + (level + power * 2));
				}
			}
		}
		if (ADAPTER.isBlockSafeToBreak(blk) && !ADAPTER.LaserBlackListBlocks().contains(blk.getType())) {
			ADAPTER.breakBlockNMS(blk, player);
		}
	}

	@Override
	public boolean onEntityInteract(PlayerInteractEntityEvent evt, int level, boolean usedHand) {
		if (usedHand && !evt.getPlayer().isSneaking()) {
			shoot(evt.getPlayer(), level, usedHand);
			return true;
		}
		return false;
	}

	@Override
	public boolean onBlockInteract(final PlayerInteractEvent evt, int level, boolean usedHand) {
		if (usedHand && !evt.getPlayer().isSneaking() &&
			(evt.getAction() == RIGHT_CLICK_AIR || evt.getAction() == RIGHT_CLICK_BLOCK)) {
			shoot(evt.getPlayer(), level, usedHand);
			return true;
		}
		return false;
	}
}
