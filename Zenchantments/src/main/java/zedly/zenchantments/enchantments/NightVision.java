package zedly.zenchantments.enchantments;

import org.bukkit.entity.Player;
import zedly.zenchantments.CustomEnchantment;
import zedly.zenchantments.Tool;
import zedly.zenchantments.Utilities;

import static org.bukkit.potion.PotionEffectType.NIGHT_VISION;
import static zedly.zenchantments.Tool.HELMET;

public class NightVision extends CustomEnchantment {

    public NightVision() {
        maxLevel = 1;
        loreName = "Night Vision";
        probability = 0;
        enchantable = new Tool[]{HELMET};
        conflicting = new Class[]{};
        description = "Lets the player see in the darkness";
        cooldown = 0;
        power = -1.0;
        handUse = 0;
    }

    public int getEnchantmentId() {
        return 40;
    }

    @Override
    public boolean onScan(Player player, int level, boolean usedHand) {
        Utilities.addPotion(player, NIGHT_VISION, 610, 5);
        return true;
    }
}
