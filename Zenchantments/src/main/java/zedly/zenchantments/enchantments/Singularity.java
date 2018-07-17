package zedly.zenchantments.enchantments;

import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityShootBowEvent;
import zedly.zenchantments.CustomEnchantment;
import zedly.zenchantments.EnchantArrow;
import zedly.zenchantments.enums.*;
import zedly.zenchantments.Utilities;

import static zedly.zenchantments.enums.Tool.BOW_;

public class Singularity extends CustomEnchantment {

    public Singularity() {
        maxLevel = 1;
        loreName = "Singularity";
        probability = 0;
        enchantable = new Tool[]{BOW_};
        conflicting = new Class[]{};
        description = "Creates a black hole that attracts nearby entities and then discharges them";
        cooldown = 0;
        power = -1.0;
        handUse = Hand.RIGHT;
    }

    public int getEnchantmentId() {
        return 72;
    }

    @Override
    public boolean onEntityShootBow(EntityShootBowEvent evt, int level, boolean usedHand) {
        EnchantArrow.ArrowAdminSingularity arrow =
                new EnchantArrow.ArrowAdminSingularity((Projectile) evt.getProjectile(), level);
        Utilities.putArrow(evt.getProjectile(), arrow, (Player) evt.getEntity());
        return true;
    }

}
