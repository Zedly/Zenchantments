package zedly.zenchantments.enchantments;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import zedly.zenchantments.CustomEnchantment;
import zedly.zenchantments.Storage;
import zedly.zenchantments.enums.Hand;
import zedly.zenchantments.enums.Tool;

import static zedly.zenchantments.enums.Tool.LEGGINGS;

public class Saturation extends CustomEnchantment {

    public Saturation() {
        super(50);
        maxLevel = 3;
        loreName = "Saturation";
        probability = 0;
        enchantable = new Tool[]{LEGGINGS};
        conflicting = new Class[]{};
        description = "Uses less of the player's hunger";
        cooldown = 0;
        power = 1.0;
        handUse = Hand.NONE;
    }

    @Override
    public boolean onHungerChange(FoodLevelChangeEvent evt, int level, boolean usedHand) {
        if(evt.getFoodLevel() < ((Player) evt.getEntity()).getFoodLevel() &&
           Storage.rnd.nextInt(10) > 10 - 2 * level * power) {
            evt.setCancelled(true);
        }
        return true;
    }
}
