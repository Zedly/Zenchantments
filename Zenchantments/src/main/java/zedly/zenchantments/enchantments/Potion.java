package zedly.zenchantments.enchantments;

import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.potion.PotionEffectType;
import zedly.zenchantments.CustomEnchantment;
import zedly.zenchantments.EnchantArrow;
import zedly.zenchantments.enums.*;
import zedly.zenchantments.Utilities;

import static zedly.zenchantments.enums.Tool.BOW;

public class Potion extends CustomEnchantment {

    PotionEffectType[] potions;

    public Potion() {
        maxLevel = 3;
        loreName = "Potion";
        probability = 0;
        enchantable = new Tool[]{BOW};
        conflicting = new Class[]{};
        description = "Gives the shooter random positive potion effects when attacking";
        cooldown = 0;
        power = 1.0;
        handUse = Hand.RIGHT;
        id = 44;
    }

    @Override
    public boolean onEntityShootBow(EntityShootBowEvent evt, int level, boolean usedHand) {
        EnchantArrow.ArrowEnchantPotion arrow =
                new EnchantArrow.ArrowEnchantPotion((Projectile) evt.getProjectile(), level, power);
        Utilities.putArrow(evt.getProjectile(), arrow, (Player) evt.getEntity());
        return true;
    }
}
