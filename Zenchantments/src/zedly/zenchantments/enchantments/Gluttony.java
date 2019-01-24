package zedly.zenchantments.enchantments;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import zedly.zenchantments.CustomEnchantment;
import zedly.zenchantments.Utilities;
import zedly.zenchantments.enums.Hand;
import zedly.zenchantments.enums.Tool;

import static org.bukkit.Material.*;
import static zedly.zenchantments.enums.Tool.HELMET;

public class Gluttony extends CustomEnchantment {

	private static final int[]      FOOD_LEVELS = {4, 5, 1, 6, 5, 3, 1, 6, 5, 6, 8, 5, 6, 2, 1, 2, 6, 8, 10, 8};
	private static final double[]   SATURATIONS = {2.4, 6, 1.2, 7.2, 6, 3.6, 0.2, 7.2, 6, 9.6, 12.8, 6, 9.6, 0.4, 0.6,
		1.2, 7.2, 4.8, 12, 12.8};
	private static final Material[] FOOD_ITEMS  = new Material[]{
		APPLE, BAKED_POTATO, BEETROOT, BEETROOT_SOUP, BREAD, CARROT, TROPICAL_FISH, COOKED_CHICKEN, COOKED_COD,
		COOKED_MUTTON, COOKED_PORKCHOP, COOKED_RABBIT, COOKED_SALMON, COOKIE, DRIED_KELP, MELON_SLICE, MUSHROOM_STEW,
		PUMPKIN_PIE, RABBIT_STEW, COOKED_BEEF};

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
		for (int i = 0; i < FOOD_ITEMS.length; i++) {
			if (player.getInventory().containsAtLeast(new ItemStack(FOOD_ITEMS[i]), 1)
				&& player.getFoodLevel() <= 20 - FOOD_LEVELS[i]) {
				Utilities.removeItem(player, FOOD_ITEMS[i], 1);
				player.setFoodLevel(player.getFoodLevel() + FOOD_LEVELS[i]);
				player.setSaturation((float) (player.getSaturation() + SATURATIONS[i]));
				if (FOOD_ITEMS[i] == RABBIT_STEW || FOOD_ITEMS[i] == MUSHROOM_STEW || FOOD_ITEMS[i] == BEETROOT_SOUP) {
					player.getInventory().addItem(new ItemStack(BOWL));
				}
			}
		}
		return true;
	}
}
