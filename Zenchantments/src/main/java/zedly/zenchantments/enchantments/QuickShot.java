package zedly.zenchantments.enchantments;

import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityShootBowEvent;
import zedly.zenchantments.CustomEnchantment;
import zedly.zenchantments.EnchantArrow;
import zedly.zenchantments.Tool;
import zedly.zenchantments.Utilities;

import static zedly.zenchantments.Tool.BOW_;

public class QuickShot extends CustomEnchantment {

    public QuickShot() {
        maxLevel = 1;
        loreName = "Quick Shot";
        probability = 0;
        enchantable = new Tool[]{BOW_};
        conflicting = new Class[]{};
        description = "Shoots arrows at full speed, instantly";
        cooldown = 0;
        power = 1.0;
        handUse = 2;
    }

    public int getEnchantmentId() {
        return 46;
    }

    @Override
    public boolean onEntityShootBow(EntityShootBowEvent evt, int level, boolean usedHand) {
        EnchantArrow.ArrowEnchantQuickShot arrow =
                new EnchantArrow.ArrowEnchantQuickShot((Projectile) evt.getProjectile());
        Utilities.putArrow(evt.getProjectile(), arrow, (Player) evt.getEntity());
        return true;
    }
}
