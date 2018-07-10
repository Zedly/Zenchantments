package zedly.zenchantments.enchantments;

import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityShootBowEvent;
import zedly.zenchantments.CustomEnchantment;
import zedly.zenchantments.EnchantArrow;
import zedly.zenchantments.Tool;
import zedly.zenchantments.Utilities;

import static zedly.zenchantments.Tool.BOW_;

public class Tracer extends CustomEnchantment {

    public Tracer() {
        maxLevel = 4;
        loreName = "Tracer";
        probability = 0;
        enchantable = new Tool[]{BOW_};
        conflicting = new Class[]{};
        description = "Guides the arrow to targets and then attacks";
        cooldown = 0;
        power = 1.0;
        handUse = 2;
    }

    public int getEnchantmentId() {
        return 63;
    }

    @Override
    public boolean onEntityShootBow(EntityShootBowEvent evt, int level, boolean usedHand) {
        EnchantArrow.ArrowEnchantTracer arrow =
                new EnchantArrow.ArrowEnchantTracer((Projectile) evt.getProjectile(), level, power);
        Utilities.putArrow(evt.getProjectile(), arrow, (Player) evt.getEntity());
        return true;
    }

}
