package zedly.zenchantments.enchantments;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import zedly.zenchantments.Zenchantment;
import zedly.zenchantments.Storage;
import zedly.zenchantments.Utilities;
import zedly.zenchantments.Hand;
import zedly.zenchantments.Tool;

import static org.bukkit.event.block.Action.RIGHT_CLICK_AIR;
import static org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK;
import static zedly.zenchantments.Tool.SWORD;

public class Gust extends Zenchantment {

	public static final int ID = 25;

	@Override
	public Builder<Gust> defaults() {
		return new Builder<>(Gust::new, ID)
			.maxLevel(1)
			.loreName("Gust")
			.probability(0)
			.enchantable(new Tool[]{SWORD})
			.conflicting(new Class[]{Force.class, RainbowSlam.class})
			.description("Pushes the user through the air at the cost of their health")
			.cooldown(0)
			.power(1.0)
			.handUse(Hand.RIGHT);
	}

	@Override
	public boolean onBlockInteract(final PlayerInteractEvent event, int level, final boolean usedHand) {
		final Player player = event.getPlayer();
		if (event.getAction().equals(RIGHT_CLICK_AIR) || event.getAction().equals(RIGHT_CLICK_BLOCK)) {
			if (player.getHealth() > 2 && (event.getClickedBlock() == null ||
				event.getClickedBlock().getLocation().distance(player.getLocation()) > 2)) {
				final Block blk = player.getTargetBlock(null, 10);
				player.setVelocity(blk.getLocation().toVector().subtract(player.getLocation().toVector())
				                      .multiply(.25 * power));
				player.setFallDistance(-40);
				ADAPTER.damagePlayer(player, 3, EntityDamageEvent.DamageCause.MAGIC);
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Storage.zenchantments, () -> {
					Utilities.damageTool(event.getPlayer(), 1, usedHand);
				}, 1);
				return true;
			}
		}
		return false;
	}
}
