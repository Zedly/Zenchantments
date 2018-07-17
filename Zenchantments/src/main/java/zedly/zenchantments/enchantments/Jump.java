package zedly.zenchantments.enchantments;

import org.bukkit.entity.Player;
import zedly.zenchantments.CustomEnchantment;
import zedly.zenchantments.enums.*;
import zedly.zenchantments.Utilities;

import static org.bukkit.potion.PotionEffectType.JUMP;
import static zedly.zenchantments.enums.Tool.BOOTS;

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
        handUse = Hand.NONE;
        id = 30;
    }

    @Override
    public boolean onScan(Player player, int level, boolean usedHand) {
        Utilities.addPotion(player, JUMP, 610, (int) Math.round(level * power));
        return true;
    }
}
