package zedly.zenchantments.enchantments;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import zedly.zenchantments.CustomEnchantment;
import zedly.zenchantments.Utilities;
import zedly.zenchantments.enums.Hand;
import zedly.zenchantments.enums.Tool;

import java.util.Map;

import static zedly.zenchantments.enums.Tool.ALL;

public class Ethereal extends CustomEnchantment {

	public static final int ID = 70;

	@Override
	public Builder<Ethereal> defaults() {
		return new Builder<>(Ethereal::new, ID)
			.maxLevel(1)
			.loreName("Ethereal")
			.probability(0)
			.enchantable(new Tool[]{ALL})
			.conflicting(new Class[]{})
			.description("Prevents tools from breaking")
			.cooldown(0)
			.power(-1.0)
			.handUse(Hand.NONE);
	}

	@Override
	public boolean onScanHands(Player player, int level, boolean usedHand) {
		ItemStack stk = Utilities.usedStack(player, usedHand);
		int dura = stk.getDurability();
		stk.setDurability((short) 0);
		if (dura != 0) {
			if (usedHand) {
				player.getInventory().setItemInMainHand(stk);
			} else {
				player.getInventory().setItemInOffHand(stk);
			}
		}
		return dura != 0;
	}

	@Override
	public boolean onScan(Player player, int level, boolean usedHand) {
		for (ItemStack s : player.getInventory().getArmorContents()) {
			if (s != null) {
				Map<CustomEnchantment, Integer> map = CustomEnchantment.getEnchants(s, player.getWorld());
				if (map.containsKey(zedly.zenchantments.enchantments.Ethereal.this)) {
					s.setDurability((short) 0);
				}
			}
		}
		return true;
	}
}
