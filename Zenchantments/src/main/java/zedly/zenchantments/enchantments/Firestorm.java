package zedly.zenchantments.enchantments;

import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityShootBowEvent;
import zedly.zenchantments.CustomEnchantment;
import zedly.zenchantments.EnchantArrow;
import zedly.zenchantments.Tool;
import zedly.zenchantments.Utilities;

import static zedly.zenchantments.Tool.BOW_;

public class Firestorm extends CustomEnchantment {

    public Firestorm() {
        maxLevel = 3;
        loreName = "Firestorm";
        probability = 0;
        enchantable = new Tool[]{BOW_};
        conflicting = new Class[]{Blizzard.class};
        description = "Spawns a firestorm where the arrow strikes burning nearby entities";
        cooldown = 0;
        power = 1.0;
        handUse = 2;
    }

    public int getEnchantmentId() {
        return 14;
    }

    @Override
    public boolean onEntityShootBow(EntityShootBowEvent evt, int level, boolean usedHand) {
        EnchantArrow.ArrowEnchantFirestorm arrow =
                new EnchantArrow.ArrowEnchantFirestorm((Projectile) evt.getProjectile(), level, power);
        Utilities.putArrow(evt.getProjectile(), arrow, (Player) evt.getEntity());
        return true;
    }

}
