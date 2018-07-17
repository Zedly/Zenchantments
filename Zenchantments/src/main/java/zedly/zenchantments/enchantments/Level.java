package zedly.zenchantments.enchantments;

import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import zedly.zenchantments.*;
import zedly.zenchantments.enums.Tool;

import static zedly.zenchantments.enums.Tool.*;

public class Level extends CustomEnchantment {

    public Level() {
        maxLevel = 3;
        loreName = "Level";
        probability = 0;
        enchantable = new Tool[]{PICKAXE, SWORD, BOW_};
        conflicting = new Class[]{};
        description = "Drops more XP when killing mobs or mining ores";
        cooldown = 0;
        power = 1.0;
        handUse = 3;
    }

    public int getEnchantmentId() {
        return 32;
    }

    @Override
    public boolean onEntityKill(EntityDeathEvent evt, int level, boolean usedHand) {
        if(Storage.rnd.nextBoolean()) {
            evt.setDroppedExp((int) (evt.getDroppedExp() * (1.3 + (level * power * .5))));
            return true;
        }
        return false;
    }

    @Override
    public boolean onBlockBreak(BlockBreakEvent evt, int level, boolean usedHand) {
        if(Storage.rnd.nextBoolean()) {
            evt.setExpToDrop((int) (evt.getExpToDrop() * (1.3 + (level * power * .5))));
            return true;
        }
        return false;
    }

    @Override
    public boolean onEntityShootBow(EntityShootBowEvent evt, int level, boolean usedHand) {
        if(Storage.rnd.nextBoolean()) {
            EnchantArrow.ArrowEnchantLevel arrow =
                    new EnchantArrow.ArrowEnchantLevel((Projectile) evt.getProjectile(), level, power);
            Utilities.putArrow(evt.getProjectile(), arrow, (Player) evt.getEntity());
            return true;
        }
        return false;
    }

}
