package zedly.zenchantments.enchantments;

import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityShootBowEvent;
import zedly.zenchantments.CustomEnchantment;
import zedly.zenchantments.EnchantArrow;
import zedly.zenchantments.enums.*;
import zedly.zenchantments.Utilities;

import static zedly.zenchantments.enums.Tool.BOW;

public class Blizzard extends CustomEnchantment {

    public Blizzard() {
        maxLevel = 3;
        loreName = "Blizzard";
        probability = 0;
        enchantable = new Tool[]{BOW};
        conflicting = new Class[]{Firestorm.class};
        description = "Spawns a blizzard where the arrow strikes freezing nearby entities";
        cooldown = 0;
        power = 1.0;
        handUse = Hand.RIGHT;
        id = 6;
    }

    @Override
    public boolean onEntityShootBow(EntityShootBowEvent evt, int level, boolean usedHand) {
        EnchantArrow.ArrowEnchantBlizzard arrow =
                new EnchantArrow.ArrowEnchantBlizzard((Projectile) evt.getProjectile(), level, power);
        Utilities.putArrow(evt.getProjectile(), arrow, (Player) evt.getEntity());
        return true;
    }
}
