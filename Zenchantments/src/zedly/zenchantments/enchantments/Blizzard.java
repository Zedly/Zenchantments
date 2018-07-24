package zedly.zenchantments.enchantments;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;
import zedly.zenchantments.CustomEnchantment;
import zedly.zenchantments.arrows.EnchantedArrow;
import zedly.zenchantments.arrows.enchanted.BlizzardArrow;
import zedly.zenchantments.enums.Hand;
import zedly.zenchantments.enums.Tool;

import static zedly.zenchantments.enums.Tool.BOW;

public class Blizzard extends CustomEnchantment {

    public static final int ID = 6;

    @Override
    public Builder<Blizzard> defaults() {
        return new Builder<>(Blizzard::new, ID)
            .maxLevel(3)
            .loreName("Blizzard")
            .probability(0)
            .enchantable(new Tool[]{BOW})
            .conflicting(new Class[]{Firestorm.class})
            .description("Spawns a blizzard where the arrow strikes freezing nearby entities")
            .cooldown(0)
            .power(1.0)
            .handUse(Hand.RIGHT);
    }

    @Override
    public boolean onEntityShootBow(EntityShootBowEvent evt, int level, boolean usedHand) {
        BlizzardArrow arrow = new BlizzardArrow((Arrow) evt.getProjectile(), level, power);
        EnchantedArrow.putArrow((Arrow) evt.getProjectile(), arrow, (Player) evt.getEntity());
        return true;
    }
}
