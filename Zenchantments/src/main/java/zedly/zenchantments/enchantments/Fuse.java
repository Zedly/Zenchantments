package zedly.zenchantments.enchantments;

import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityShootBowEvent;
import zedly.zenchantments.CustomEnchantment;
import zedly.zenchantments.EnchantArrow;
import zedly.zenchantments.enums.*;
import zedly.zenchantments.Utilities;

import static zedly.zenchantments.enums.Tool.BOW;

public class Fuse extends CustomEnchantment {

    public Fuse() {
        maxLevel = 1;
        loreName = "Fuse";
        probability = 0;
        enchantable = new Tool[]{BOW};
        conflicting = new Class[]{};
        description = "Instantly ignites anything explosive";
        cooldown = 0;
        power = -1.0;
        handUse = Hand.RIGHT;
        id = 18;
    }

    @Override
    public boolean onEntityShootBow(EntityShootBowEvent evt, int level, boolean usedHand) {
        EnchantArrow.ArrowEnchantFuse arrow = new EnchantArrow.ArrowEnchantFuse((Projectile) evt.getProjectile());
        Utilities.putArrow(evt.getProjectile(), arrow, (Player) evt.getEntity());
        return true;
    }

}
