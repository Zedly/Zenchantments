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

    private static final int[]      FOOD_LEVELS = {10, 8, 8, 8, 6, 6, 6, 6, 6, 6, 5, 5, 4, 3, 2, 2, 1};
    private static final double[]   SATURATIONS =
            {12.0, 12.8, 4.8, 12.8, 6.0, 7.2, 7.2, 9.6, 7.2, 6.0, 9.6, 6.0, 2.4, 3.6, 0.4, 1.2, 1.2};
    private static final Material[] FOOD_ITEMS  = new Material[]{RABBIT_STEW, COOKED_BEEF, PUMPKIN_PIE,
                                                                 GRILLED_PORK, BAKED_POTATO, BEETROOT_SOUP,
                                                                 COOKED_CHICKEN, COOKED_MUTTON,
                                                                 MUSHROOM_SOUP, COOKED_FISH, COOKED_FISH, BREAD,
                                                                 APPLE, CARROT_ITEM, COOKIE,
                                                                 MELON, BEETROOT};

    public Gluttony() {
        super(21);
        maxLevel = 1;
        loreName = "Gluttony";
        probability = 0;
        enchantable = new Tool[]{HELMET};
        conflicting = new Class[]{};
        description = "Automatically eats for the player";
        cooldown = 0;
        power = -1.0;
        handUse = Hand.NONE;
    }

    @Override
    public boolean onScan(Player player, int level, boolean usedHand) {
        int check = 0;
        for(int i = 0; i < FOOD_ITEMS.length; i++) {
            if(FOOD_ITEMS[i] == COOKED_FISH) {
                check = (check + 1) % 2;
            }
            if(player.getInventory().containsAtLeast(new ItemStack(FOOD_ITEMS[i], 1, (short) check), 1)
               && player.getFoodLevel() <= 20 - FOOD_LEVELS[i]) {
                Utilities.removeItem(player, FOOD_ITEMS[i], (short) check, 1);
                player.setFoodLevel(player.getFoodLevel() + FOOD_LEVELS[i]);
                player.setSaturation((float) (player.getSaturation() + SATURATIONS[i]));
                if(FOOD_ITEMS[i] == RABBIT_STEW || FOOD_ITEMS[i] == MUSHROOM_SOUP) {
                    player.getInventory().addItem(new ItemStack(BOWL));
                }
            }
        }
        return true;
    }
}
