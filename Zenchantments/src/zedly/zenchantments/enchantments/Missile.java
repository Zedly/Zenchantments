package zedly.zenchantments.enchantments;

import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;
import zedly.zenchantments.CustomEnchantment;
import zedly.zenchantments.Utilities;
import zedly.zenchantments.arrows.EnchantedArrow;
import zedly.zenchantments.arrows.admin.MissileArrow;
import zedly.zenchantments.enums.Hand;
import zedly.zenchantments.enums.Tool;

import static zedly.zenchantments.enums.Tool.BOW;

public class Missile extends CustomEnchantment {

    @Override
    public Builder<Missile> defaults() {
        return new Builder<>(Missile::new, 71)
            .maxLevel(1)
            .loreName("Missile")
            .probability(0)
            .enchantable(new Tool[]{BOW})
            .conflicting(new Class[]{})
            .description("Shoots a missile from the bow")
            .cooldown(0)
            .power(-1.0)
            .handUse(Hand.RIGHT);
    }

    @Override
    public boolean onEntityShootBow(EntityShootBowEvent evt, int level, boolean usedHand) {
        MissileArrow arrow = new MissileArrow((Arrow) evt.getProjectile());
        EnchantedArrow.putArrow((Arrow) evt.getProjectile(), arrow, (Player) evt.getEntity());
        evt.setCancelled(true);
        Utilities.damageTool((Player) evt.getEntity(), 1, usedHand);
        Utilities.removeItem(((Player) evt.getEntity()), Material.ARROW, 1);
        return true;
    }
}
