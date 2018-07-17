package zedly.zenchantments.enchantments;

import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityShootBowEvent;
import zedly.zenchantments.CustomEnchantment;
import zedly.zenchantments.EnchantArrow;
import zedly.zenchantments.enums.*;
import zedly.zenchantments.Utilities;

import static zedly.zenchantments.enums.Tool.BOW_;

public class Fireworks extends CustomEnchantment {

    public Fireworks() {
        maxLevel = 4;
        loreName = "Fireworks";
        probability = 0;
        enchantable = new Tool[]{BOW_};
        conflicting = new Class[]{};
        description = "Shoots arrows that burst into fireworks upon impact";
        cooldown = 0;
        power = -1.0;
        handUse = Hand.RIGHT;
        id = 15;
    }

    @Override
    public boolean onEntityShootBow(EntityShootBowEvent evt, int level, boolean usedHand) {
        EnchantArrow.ArrowEnchantFirework arrow =
                new EnchantArrow.ArrowEnchantFirework((Projectile) evt.getProjectile(), level);
        Utilities.putArrow(evt.getProjectile(), arrow, (Player) evt.getEntity());
        return true;
    }

}
