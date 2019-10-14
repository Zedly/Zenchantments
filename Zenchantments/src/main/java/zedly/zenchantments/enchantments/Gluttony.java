package zedly.zenchantments.enchantments;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import zedly.zenchantments.CustomEnchantment;
import zedly.zenchantments.Storage;
import zedly.zenchantments.Utilities;
import zedly.zenchantments.enums.Hand;
import zedly.zenchantments.enums.Tool;

import static org.bukkit.Material.*;
import static zedly.zenchantments.enums.Tool.HELMET;

public class Gluttony extends CustomEnchantment {

	public static final int ID = 21;

	@Override
	public Builder<Gluttony> defaults() {
		return new Builder<>(Gluttony::new, ID)
			.maxLevel(1)
			.loreName("Gluttony")
			.probability(0)
			.enchantable(new Tool[]{HELMET})
			.conflicting(new Class[]{})
			.description("Automatically eats for the player")
			.cooldown(0)
			.power(-1.0)
			.handUse(Hand.NONE);
	}

	@Override
	public boolean onScan(Player player, int level, boolean usedHand) {
		for (int i = 0; i < Storage.COMPATIBILITY_ADAPTER.GluttonyFoodItems().length; i++) {
			if (player.getInventory().containsAtLeast(
				new ItemStack(Storage.COMPATIBILITY_ADAPTER.GluttonyFoodItems()[i]), 1)
				&& player.getFoodLevel() <= 20 - Storage.COMPATIBILITY_ADAPTER.GluttonyFoodLevels()[i]) {
				Utilities.removeItem(player, Storage.COMPATIBILITY_ADAPTER.GluttonyFoodItems()[i], 1);
				player.setFoodLevel(player.getFoodLevel() + Storage.COMPATIBILITY_ADAPTER.GluttonyFoodLevels()[i]);
				player.setSaturation(
					(float) (player.getSaturation() + Storage.COMPATIBILITY_ADAPTER.GluttonySaturations()[i]));
				if (Storage.COMPATIBILITY_ADAPTER.GluttonyFoodItems()[i] == RABBIT_STEW
					|| Storage.COMPATIBILITY_ADAPTER.GluttonyFoodItems()[i] == MUSHROOM_STEW
					|| Storage.COMPATIBILITY_ADAPTER.GluttonyFoodItems()[i] == BEETROOT_SOUP) {
					player.getInventory().addItem(new ItemStack(BOWL));
				}
			}
		}
		return true;
	}
}
