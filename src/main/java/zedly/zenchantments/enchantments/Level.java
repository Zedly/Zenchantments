package zedly.zenchantments.enchantments;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import zedly.zenchantments.Zenchantment;
import zedly.zenchantments.arrows.EnchantedArrow;
import zedly.zenchantments.Storage;
import zedly.zenchantments.arrows.enchanted.LevelArrow;
import zedly.zenchantments.Hand;
import zedly.zenchantments.Tool;

import static zedly.zenchantments.Tool.*;

public class Level extends Zenchantment {

    public static final int ID = 32;

    @Override
    public Builder<Level> defaults() {
        return new Builder<>(Level::new, ID)
                .maxLevel(3)
                .name("Level")
                .probability(0)
                .enchantable(new Tool[]{PICKAXE, SWORD, BOW})
                .conflicting(new Class[]{})
                .description("Drops more XP when killing mobs or mining ores")
                .cooldown(0)
                .power(1.0)
                .handUse(Hand.BOTH);
    }

    @Override
    public boolean onEntityKill(EntityDeathEvent event, int level, boolean usedHand) {
        if (Storage.rnd.nextBoolean()) {
            event.setDroppedExp((int) (event.getDroppedExp() * (1.3 + (level * power * .5))));
            return true;
        }
        return false;
    }

    @Override
    public boolean onBlockBreak(BlockBreakEvent event, int level, boolean usedHand) {
        if (Storage.rnd.nextBoolean()) {
            event.setExpToDrop((int) (event.getExpToDrop() * (1.3 + (level * power * .5))));
            return true;
        }
        return false;
    }

    @Override
    public boolean onEntityShootBow(EntityShootBowEvent event, int level, boolean usedHand) {
        if (Storage.rnd.nextBoolean()) {
            LevelArrow arrow = new LevelArrow((Arrow) event.getProjectile(), level, power);
            EnchantedArrow.putArrow((Arrow) event.getProjectile(), arrow, (Player) event.getEntity());
            return true;
        }
        return false;
    }

}
