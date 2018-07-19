package zedly.zenchantments.enchantments;

import zedly.zenchantments.CustomEnchantment;
import zedly.zenchantments.enums.Hand;
import zedly.zenchantments.enums.Tool;

import static zedly.zenchantments.enums.Tool.ALL;

public class Unrepairable extends CustomEnchantment {
    @Override
    public Builder<Unrepairable> defaults() {
        return new Builder<>(Unrepairable::new, 73)
            .maxLevel(1)
            .loreName("Unrepairable")
            .probability(0)
            .enchantable(new Tool[]{ALL})
            .conflicting(new Class[]{})
            .description("Prevents an item from being repaired")
            .cooldown(0)
            .power(-1.0)
            .handUse(Hand.NONE);
    }
}
