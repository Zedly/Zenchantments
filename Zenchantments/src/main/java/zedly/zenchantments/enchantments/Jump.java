package zedly.zenchantments.enchantments;

import org.bukkit.entity.Player;
import zedly.zenchantments.CustomEnchantment;
import zedly.zenchantments.Tool;
import zedly.zenchantments.Utilities;

import static org.bukkit.potion.PotionEffectType.JUMP;
import static zedly.zenchantments.Tool.BOOTS;

public class Jump extends CustomEnchantment {

    public Jump() {
        maxLevel = 4;
        loreName = "Jump";
        probability = 0;
        enchantable = new Tool[]{BOOTS};
        conflicting = new Class[]{};
        description = "Gives the player a jump boost";
        cooldown = 0;
        power = 1.0;
        handUse = 0;
    }

    public int getEnchantmentId() {
        return 30;
    }

    @Override
    public boolean onScan(Player player, int level, boolean usedHand) {
        Utilities.addPotion(player, JUMP, 610, (int) Math.round(level * power));
        return true;
    }
}
