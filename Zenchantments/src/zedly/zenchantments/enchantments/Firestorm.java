package zedly.zenchantments.enchantments;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;
import zedly.zenchantments.CustomEnchantment;
import zedly.zenchantments.arrows.EnchantedArrow;
import zedly.zenchantments.arrows.enchanted.FirestormArrow;
import zedly.zenchantments.enums.Hand;
import zedly.zenchantments.enums.Tool;

import static zedly.zenchantments.enums.Tool.BOW;

public class Firestorm extends CustomEnchantment {

    @Override
    public Builder<Firestorm> defaults() {
        return new Builder<>(Firestorm::new, 14)
            .maxLevel(3)
            .loreName("Firestorm")
            .probability(0)
            .enchantable(new Tool[]{BOW})
            .conflicting(new Class[]{Blizzard.class})
            .description("Spawns a firestorm where the arrow strikes burning nearby entities")
            .cooldown(0)
            .power(1.0)
            .handUse(Hand.RIGHT);
    }

    @Override
    public boolean onEntityShootBow(EntityShootBowEvent evt, int level, boolean usedHand) {
        FirestormArrow arrow = new FirestormArrow((Arrow) evt.getProjectile(), level, power);
        EnchantedArrow.putArrow((Arrow) evt.getProjectile(), arrow, (Player) evt.getEntity());
        return true;
    }

}
