package zedly.zenchantments.enchantments;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import zedly.zenchantments.Zenchantment;
import zedly.zenchantments.Storage;
import zedly.zenchantments.Hand;
import zedly.zenchantments.Tool;

import static org.bukkit.Material.AIR;
import static zedly.zenchantments.Tool.CHESTPLATE;

public class Stock extends Zenchantment {

	public static final int ID = 59;

	@Override
	public Builder<Stock> defaults() {
		return new Builder<>(Stock::new, ID)
			.maxLevel(1)
			.name("Stock")
			.probability(0)
			.enchantable(new Tool[]{CHESTPLATE})
			.conflicting(new Class[]{})
			.description("Refills the player's item in hand when they run out")
			.cooldown(-1)
			.power(-1.0)
			.handUse(Hand.NONE);
	}

	@Override
	public boolean onBlockInteract(final PlayerInteractEvent event, int level, boolean usedHand) {
		final ItemStack stk = event.getPlayer().getInventory().getItemInMainHand().clone();
		if (stk == null || stk.getType() == AIR) {
			return false;
		}
		final Player player = event.getPlayer();
		Bukkit.getScheduler().scheduleSyncDelayedTask(Storage.zenchantments, () -> {
			int current = -1;
			ItemStack newHandItem = event.getPlayer().getInventory().getItemInMainHand();
			if (newHandItem != null && newHandItem.getType() != AIR) {
				return;
			}
			for (int i = 0; i < event.getPlayer().getInventory().getContents().length; i++) {
				ItemStack s = player.getInventory().getContents()[i];
				if (s != null && s.getType().equals(stk.getType())) {
					current = i;
					break;
				}
			}
			if (current != -1) {
				event.getPlayer().getInventory()
				   .setItemInMainHand(event.getPlayer().getInventory().getContents()[current]);
				event.getPlayer().getInventory().setItem(current, new ItemStack(AIR));
			}
		}, 1);
		return false;
	}
}
