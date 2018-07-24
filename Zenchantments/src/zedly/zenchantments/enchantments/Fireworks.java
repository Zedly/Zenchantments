package zedly.zenchantments.enchantments;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;
import zedly.zenchantments.CustomEnchantment;
import zedly.zenchantments.arrows.EnchantedArrow;
import zedly.zenchantments.arrows.enchanted.FireworkArrow;
import zedly.zenchantments.enums.Hand;
import zedly.zenchantments.enums.Tool;

import static zedly.zenchantments.enums.Tool.BOW;

public class Fireworks extends CustomEnchantment {

    public static final int ID = 15;

    @Override
    public Builder<Fireworks> defaults() {
        return new Builder<>(Fireworks::new, ID)
            .maxLevel(4)
            .loreName("Fireworks")
            .probability(0)
            .enchantable(new Tool[]{BOW})
            .conflicting(new Class[]{})
            .description("Shoots arrows that burst into fireworks upon impact")
            .cooldown(0)
            .power(-1.0)
            .handUse(Hand.RIGHT);
    }

    @Override
    public boolean onEntityShootBow(EntityShootBowEvent evt, int level, boolean usedHand) {
        FireworkArrow arrow = new FireworkArrow((Arrow) evt.getProjectile(), level);
        EnchantedArrow.putArrow((Arrow) evt.getProjectile(), arrow, (Player) evt.getEntity());
        return true;
    }

}
