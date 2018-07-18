package zedly.zenchantments.enchantments;

import org.bukkit.entity.Player;
import zedly.zenchantments.CustomEnchantment;
import zedly.zenchantments.enums.Hand;
import zedly.zenchantments.enums.Tool;

import static org.bukkit.Material.SLIME_BLOCK;
import static zedly.zenchantments.enums.Tool.BOOTS;

public class Bounce extends CustomEnchantment {

    public Bounce() {
        super(7);
        maxLevel = 5;
        loreName = "Bounce";
        probability = 0;
        enchantable = new Tool[]{BOOTS};
        conflicting = new Class[]{};
        description = "Preserves momentum when on slime blocks";
        cooldown = 0;
        power = 1.0;
        handUse = Hand.NONE;
    }

    @Override
    public boolean onFastScan(Player player, int level, boolean usedHand) {
        if(player.getVelocity().getY() < 0 &&
           (player.getLocation().getBlock().getRelative(0, -1, 0).getType() == SLIME_BLOCK
            || player.getLocation().getBlock().getType() == SLIME_BLOCK
            || (player.getLocation().getBlock().getRelative(0, -2, 0).getType() == SLIME_BLOCK) &&
               (level * power) > 2.0)) {
            if(!player.isSneaking()) {
                player.setVelocity(player.getVelocity().setY(.56 * level * power));
                return true;
            }
            player.setFallDistance(0);
        }
        return false;
    }
}
