package zedly.zenchantments.enchantments;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;
import zedly.zenchantments.CustomEnchantment;
import zedly.zenchantments.arrows.EnchantedArrow;
import zedly.zenchantments.arrows.enchanted.QuickArrow;
import zedly.zenchantments.enums.Hand;
import zedly.zenchantments.enums.Tool;

import static zedly.zenchantments.enums.Tool.BOW;

public class QuickShot extends CustomEnchantment {

    @Override
    public Builder<QuickShot> defaults() {
        return new Builder<>(QuickShot::new, 46)
            .maxLevel(1)
            .loreName("Quick Shot")
            .probability(0)
            .enchantable(new Tool[]{BOW})
            .conflicting(new Class[]{})
            .description("Shoots arrows at full speed, instantly")
            .cooldown(0)
            .power(1.0)
            .handUse(Hand.RIGHT);
    }

    @Override
    public boolean onEntityShootBow(EntityShootBowEvent evt, int level, boolean usedHand) {
        QuickArrow arrow = new QuickArrow((Arrow) evt.getProjectile());
        EnchantedArrow.putArrow((Arrow) evt.getProjectile(), arrow, (Player) evt.getEntity());
        return true;
    }
}
