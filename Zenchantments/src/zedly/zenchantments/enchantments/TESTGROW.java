package zedly.zenchantments.enchantments;

import org.bukkit.event.player.PlayerInteractEvent;
import zedly.zenchantments.CustomEnchantment;
import zedly.zenchantments.Storage;
import zedly.zenchantments.enums.Hand;
import zedly.zenchantments.enums.Tool;

import static zedly.zenchantments.enums.Tool.SHOVEL;

public class TESTGROW extends CustomEnchantment {

    public static final int ID = 999;

    private static int increase(int old, int add) {
        if(old < add) {
            return ++old;
        } else {
            return 0;
        }
    }

    @Override
    public Builder<TESTGROW> defaults() {
        return new Builder<>(TESTGROW::new, ID)
            .maxLevel(1)
            .loreName("TESTGROW")
            .probability(0)
            .enchantable(new Tool[]{SHOVEL})
            .conflicting(new Class[]{})
            .description("TESTGROW")
            .cooldown(0)
            .power(-1.0)
            .handUse(Hand.RIGHT);
    }

    @Override
    public boolean onBlockInteract(PlayerInteractEvent evt, int level, boolean usedHand) {
        if(evt.getClickedBlock() == null) {
            return false;
        }
        Storage.COMPATIBILITY_ADAPTER.grow(evt.getClickedBlock(), evt.getPlayer());
        return false;
    }

}
